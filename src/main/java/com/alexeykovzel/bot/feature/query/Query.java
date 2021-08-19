package com.alexeykovzel.bot.feature.query;

public interface Query {
    void execute();

    void setStatusByKey(String statusId);
}
