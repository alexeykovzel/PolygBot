package com.alexeykovzel.bot.handler.abs;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface BasicAbsSender {
    void sendMsg(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup);

    void sendMsg(String chatId, String text);

    void deleteMsg(String chatId, Integer messageId);

    void sendAnswerCallbackQuery(String text, boolean alert, String callbackQueryId);

    void sendAnswerCallbackQuery(String callbackQueryId);

    void editMessageReplyMarkup(String chatId, Integer messageId, InlineKeyboardMarkup markup);

    InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData);

    String escapeMarkdown(String text);
}
