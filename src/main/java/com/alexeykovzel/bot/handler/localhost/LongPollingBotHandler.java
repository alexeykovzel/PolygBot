package com.alexeykovzel.bot.handler.localhost;

import com.alexeykovzel.bot.query.QueryRegistry;
import com.alexeykovzel.bot.handler.BotHandler;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

abstract class LongPollingBotHandler extends TelegramLongPollingBot implements BotHandler {
    protected CommandRegistry commandRegistry;
    protected QueryRegistry queryRegistry;

    public LongPollingBotHandler() {
        this(new DefaultBotOptions());
    }

    public LongPollingBotHandler(DefaultBotOptions options) {
        this(options, true);
    }

    public LongPollingBotHandler(DefaultBotOptions options, boolean allowCommandsWithUsername) {
        super(options);
        this.commandRegistry = new CommandRegistry(allowCommandsWithUsername, this::getBotUsername);
        this.queryRegistry = new QueryRegistry();
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

    protected <T extends Serializable, Method extends BotApiMethod<T>> void executeApiMethod(Method method) {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }
}
