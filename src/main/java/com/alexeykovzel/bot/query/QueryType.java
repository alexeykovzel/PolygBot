package com.alexeykovzel.bot.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum QueryType {
    SAVE_WORD("0"),
    VIEW_LIST("1"),
    SELECT_ITEM("2");

    @Getter
    private String key;

    public static QueryType fromKey(String key){
        for (QueryType queryType : values()){
            if (queryType.key.equals(key)){
                return queryType;
            }
        }
        return null;
    }
}
