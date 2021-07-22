package com.alexeykovzel.bot.query;

public enum QueryType {
    SAVE_WORD("0"),
    VIEW_LIST("1"),
    SELECT_ITEM("2");

    public String id;

    QueryType(String id) {
        this.id = id;
    }

    public static QueryType fromId(String id){
        for (QueryType queryType : values()){
            if (queryType.id.equals(id)){
                return queryType;
            }
        }
        return null;
    }
}
