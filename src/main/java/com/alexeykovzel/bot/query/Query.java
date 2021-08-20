package com.alexeykovzel.bot.query;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface Query {
    void execute(AbsSender absSender, QueryDto queryDto, CallbackQuery basicQuery);

    QueryType getType();
}
