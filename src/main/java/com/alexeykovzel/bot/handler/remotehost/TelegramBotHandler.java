package com.alexeykovzel.bot.handler.remotehost;

import com.alexeykovzel.bot.handler.BotHandler;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class TelegramBotHandler extends DefaultAbsSender implements BotHandler {
    protected static CommandRegistry commandRegistry;

    protected TelegramBotHandler() {
        this(new DefaultBotOptions());
    }

    protected TelegramBotHandler(DefaultBotOptions options) {
        this(options, true);
    }

    protected TelegramBotHandler(DefaultBotOptions options, boolean allowCommandsWithUsername) {
        super(options);
        commandRegistry = new CommandRegistry(allowCommandsWithUsername, this::getBotUsername);
    }

    @Override
    public void handleUpdate(Update update) throws TelegramApiException {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isCommand() && !filter(message)) {
                if (!commandRegistry.executeCommand(this, message)) {
                    handleInvalidCommandUpdate(update);
                }
            } else {
                handleNonCommandUpdate(update);
            }
        } else {
            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
            }
        }
    }
    protected boolean filter(Message message) {
        return false;
    }

    public synchronized void sendMsg(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .disableWebPagePreview(true)
                .replyMarkup(inlineKeyboardMarkup).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public synchronized void sendMsg(String chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .disableWebPagePreview(true).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public synchronized void deleteMsg(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId).build();
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public String escapeMarkdown(String text) {
        return text
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("`", "\\`");
    }
}
