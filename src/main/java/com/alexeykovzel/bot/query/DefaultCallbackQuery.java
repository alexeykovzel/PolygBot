package com.alexeykovzel.bot.query;

import com.alexeykovzel.bot.handler.abs.BasicAbsSenderImpl;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public abstract class DefaultCallbackQuery extends BasicAbsSenderImpl implements Query {
    protected final String[] args;
    protected final CallbackQuery basicQuery;

    public DefaultCallbackQuery(String[] args, AbsSender absSender, CallbackQuery basicQuery) {
        this.args = args;
        this.absSender = absSender;
        this.basicQuery = basicQuery;
    }
}
