package com.alexeykovzel.bot.query;

public interface Query {
    void execute();

    void setStatusByKey(String statusId);
}
