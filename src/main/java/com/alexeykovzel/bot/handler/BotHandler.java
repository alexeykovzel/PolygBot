package com.alexeykovzel.bot.handler;

import com.alexeykovzel.bot.handler.abs.MessageHandler;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotHandler extends MessageHandler {
    void handleUpdate(Update update) throws TelegramApiException;

    void handleInvalidCommandUpdate(Update update);

    void handleTextMessage(Message message);

    void handleNonTextMessage(Message message);

    void handleNonCommandUpdate(Update update);

    void handleCallbackQuery(Update update) throws TelegramApiException;

    String getBotUsername();
}
