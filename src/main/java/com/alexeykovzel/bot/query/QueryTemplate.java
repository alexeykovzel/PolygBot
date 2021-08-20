package com.alexeykovzel.bot.query;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

public abstract class QueryTemplate implements Query {
    protected <T extends Serializable, Method extends BotApiMethod<T>> void executeApiMethod(AbsSender absSender, Method method) {
        try {
            absSender.execute(method);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }
}
