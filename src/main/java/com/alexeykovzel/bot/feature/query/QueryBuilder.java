package com.alexeykovzel.bot.feature.query;

import com.alexeykovzel.bot.feature.query.converter.DataConverter;
import com.alexeykovzel.bot.feature.query.converter.QueryDataConverter;

public abstract class QueryBuilder {
    protected static String buildCallbackData(String typeKey, String statusKey, String[] args) {
        DataConverter<String, QueryDto> dataConverter = new QueryDataConverter();
        return dataConverter.encode(QueryDto.builder()
                .typeKey(typeKey)
                .statusKey(statusKey)
                .args(args).build());
    }
}
