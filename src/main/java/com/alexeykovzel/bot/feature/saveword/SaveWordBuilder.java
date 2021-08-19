package com.alexeykovzel.bot.feature.saveword;

import com.alexeykovzel.bot.feature.query.QueryBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SaveWordBuilder extends QueryBuilder {
    public static InlineKeyboardMarkup buildSaveWordMarkup(String wordText) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = Collections.singletonList(
                Arrays.asList(
                        createInlineKeyboardButton("Actually, I do!",
                                buildCallbackData(SaveWordQuery.type.getKey(), SaveWordQuery.SaveWordStatus.SAVE.getKey(),
                                        new String[]{wordText})),

                        createInlineKeyboardButton("Not really...",
                                buildCallbackData(SaveWordQuery.type.getKey(), SaveWordQuery.SaveWordStatus.IGNORE.getKey(),
                                        new String[]{wordText}))
                )
        );
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }
}
