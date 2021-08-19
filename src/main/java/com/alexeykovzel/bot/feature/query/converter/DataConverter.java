package com.alexeykovzel.bot.feature.query.converter;

public interface DataConverter<E, D> {
    E encode(D d);

    D decode(E e);
}
