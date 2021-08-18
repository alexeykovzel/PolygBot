package com.alexeykovzel.bot.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CallbackQueryType {
    SAVE_WORD("0"),
    VIEW_LIST("1"),
    SELECT_ITEM("2");

    @Getter
    private String key;

    public static CallbackQueryType fromId(String key){
        for (CallbackQueryType callbackQueryType : values()){
            if (callbackQueryType.key.equals(key)){
                return callbackQueryType;
            }
        }
        return null;
    }
}
