package com.alexeykovzel.bot.query;

public interface QueryExecutor {
    void execute();

    void setStatusByKey(String statusId);
}
