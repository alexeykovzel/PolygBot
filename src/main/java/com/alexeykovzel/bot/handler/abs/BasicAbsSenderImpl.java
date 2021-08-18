package com.alexeykovzel.bot.handler.abs;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class BasicAbsSenderImpl implements BasicAbsSender {
    protected AbsSender absSender;

    public synchronized void sendMsg(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .disableWebPagePreview(true)
                .replyMarkup(inlineKeyboardMarkup).build();
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    @Override
    public synchronized void sendMsg(String chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .disableWebPagePreview(true).build();
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    @Override
    public synchronized void deleteMsg(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId).build();
        try {
            absSender.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    @Override
    public synchronized void sendAnswerCallbackQuery(String text, boolean alert, String callbackQueryId) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .showAlert(alert)
                .text(text).build();
        try {
            absSender.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    @Override
    public synchronized void sendAnswerCallbackQuery(String callbackQueryId) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId).build();
        try {
            absSender.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    @Override
    public String escapeMarkdown(String text) {
        return text
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("`", "\\`");
    }

    @Override
    public InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }

    @Override
    public void editMessageReplyMarkup(String chatId, Integer messageId, InlineKeyboardMarkup markup) {
        EditMessageReplyMarkup replyMarkup = EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(markup).build();
        try {
            absSender.execute(replyMarkup);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }
}
