package com.alexeykovzel.bot.handler.remotehost;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.GetAccountSettingsRequest;
import software.amazon.awssdk.services.lambda.model.GetAccountSettingsResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.getenv;

public class RequestHandler implements RequestStreamHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    LambdaAsyncClient lambdaClient = LambdaAsyncClient.create();

    public RequestHandler() {
        CompletableFuture<GetAccountSettingsResponse> accountSettings = lambdaClient.getAccountSettings(GetAccountSettingsRequest.builder().build());
            try {
            GetAccountSettingsResponse settings = accountSettings.get();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        CompletableFuture<GetAccountSettingsResponse> accountSettings =
                lambdaClient.getAccountSettings(GetAccountSettingsRequest.builder().build());

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)));

        JSONObject responseJson = new JSONObject();
        JSONTokener jsonTokener = new JSONTokener(reader);
        JSONObject event = new JSONObject(jsonTokener);
        logger.info("EVENT: {}", event);
        String body = event.getString("body");
        logger.info("BODY: {}", body);

        try {
            GetAccountSettingsResponse settings = accountSettings.get();
            logger.info("ACCOUNT USAGE: {}", settings.accountUsage());
        } catch (Exception e) {
            e.getStackTrace();
        }

        try {
            Update update = MAPPER.readValue(body, Update.class);
            if (update != null) {
                TelegramBotHandler telegramBotHandler = AWSPolygBotHandler.getInstance(getenv("bot_username"), getenv("bot_token"));
                telegramBotHandler.handleUpdate(update);
            }
        } catch (IOException | TelegramApiException e) {
            e.getStackTrace();
        } finally {
            responseJson.put("statusCode", 200);
            writer.write(responseJson.toString());
            writer.close();
        }
    }
}
