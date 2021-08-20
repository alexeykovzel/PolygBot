package com.alexeykovzel.bot.query;

import com.alexeykovzel.bot.query.converter.DataConverter;
import com.alexeykovzel.bot.query.converter.QueryDataConverter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryRegistry {
    private final Map<QueryType, Query> queryRegistryMap = new HashMap<>();

    public void registerAll(Query... querySet) {
        queryRegistryMap.putAll(Arrays.stream(querySet)
                .collect(Collectors.toMap(Query::getType, query -> query)));
    }

    public boolean register(Query query) {
        if (!queryRegistryMap.containsKey(query.getType())) {
            queryRegistryMap.put(query.getType(), query);
            return true;
        }
        return false;
    }

    public void executeQuery(AbsSender absSender, CallbackQuery callbackQuery) {
        QueryDto queryDto = decodeQueryData(callbackQuery.getData());
        QueryType type = QueryType.fromKey(queryDto.getTypeKey());
        if (queryRegistryMap.containsKey(type)) {
            queryRegistryMap.get(type).execute(absSender, queryDto, callbackQuery);
        }
    }

    private QueryDto decodeQueryData(String data) {
        DataConverter<String, QueryDto> dataConverter = new QueryDataConverter();
        return dataConverter.decode(data);
    }
}
