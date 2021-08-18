package com.alexeykovzel.bot.feature;

import com.alexeykovzel.bot.query.CallbackQueryDto;
import com.alexeykovzel.bot.util.DataConverter;
import com.alexeykovzel.bot.util.QueryDataConverter;

public abstract class QueryBuilder {
    protected static String buildCallbackData(String typeKey, String statusKey, String[] args) {
        DataConverter<String, CallbackQueryDto> dataConverter = new QueryDataConverter();
        return dataConverter.encode(CallbackQueryDto.builder()
                .typeKey(typeKey)
                .statusKey(statusKey)
                .args(args).build());
    }
}
