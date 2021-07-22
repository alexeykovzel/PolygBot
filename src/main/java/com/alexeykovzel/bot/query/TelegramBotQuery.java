package com.alexeykovzel.bot.query;

import org.telegram.telegrambots.meta.bots.AbsSender;

public abstract class TelegramBotQuery implements QueryExecutor {
    final String[] args;
    final AbsSender absSender;

    public TelegramBotQuery(String[] args, AbsSender absSender) {
        this.args = args;
        this.absSender = absSender;
    }
}
