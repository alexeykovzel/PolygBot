package com.alexeykovzel.bot.util;

import org.telegram.telegrambots.meta.bots.AbsSender;

public interface DataConverter<E, D> {
    E encode(D d);

    D decode(E e);
}
