package com.alexeykovzel.bot.feature.query;

import com.alexeykovzel.bot.handler.abs.BasicAbsSender;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public abstract class BasicQuery extends BasicAbsSender implements Query {
    protected final String[] args;
    protected final CallbackQuery basicQuery;

    public BasicQuery(String[] args, AbsSender absSender, CallbackQuery basicQuery) {
        this.args = args;
        this.absSender = absSender;
        this.basicQuery = basicQuery;
    }
}
