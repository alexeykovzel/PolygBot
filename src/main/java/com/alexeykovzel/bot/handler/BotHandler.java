package com.alexeykovzel.bot.handler;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

interface BotHandler {
    void handleUpdate(Update update) throws TelegramApiException;

    void handleInvalidCommandUpdate(Update update);

    void handleTextMessage(Message message);

    void handleNonTextMessage(Message message);

    void handleNonCommandUpdate(Update update);

    void handleCallbackQuery(Update update) throws TelegramApiException;

    String getBotUsername();

    void sendMsg(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup);

    void sendMsg(String chatId, String text);

    void deleteMsg(String chatId, Integer messageId);

    void sendAnswerCallbackQuery(String text, boolean alert, String callbackQueryId);

    void sendAnswerCallbackQuery(String callbackQueryId);

    InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData);

    String escapeMarkdown(String text);
}
