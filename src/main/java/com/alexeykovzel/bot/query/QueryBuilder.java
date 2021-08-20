package com.alexeykovzel.bot.query;

import com.alexeykovzel.bot.query.converter.DataConverter;
import com.alexeykovzel.bot.query.converter.QueryDataConverter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public abstract class QueryBuilder {
    protected static String buildCallbackData(String typeKey, String statusKey, String[] args) {
        DataConverter<String, QueryDto> dataConverter = new QueryDataConverter();
        return dataConverter.encode(QueryDto.builder()
                .typeKey(typeKey)
                .statusKey(statusKey)
                .args(args).build());
    }

    protected static InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }
}
