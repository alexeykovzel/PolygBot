package com.alexeykovzel.bot.feature.query.converter;

import com.alexeykovzel.bot.feature.query.QueryDto;

public class QueryDataConverter implements DataConverter<String, QueryDto> {
    public static final String DATA_DELIMITER = "&";
    public static final String NON_ARGS_DELIMITER = "@";

    @Override
    public String encode(QueryDto data) {
        StringBuilder result = new StringBuilder();
        result.append(data.getTypeKey()).append(DATA_DELIMITER)
                .append(data.getStatusKey()).append(NON_ARGS_DELIMITER);
        String[] args = data.getArgs();

        if (args.length != 0) {
            result.append(String.join(DATA_DELIMITER, args));
        }

        return result.toString();
    }

    @Override
    public QueryDto decode(String encoded) {
        String[] dataGroups = encoded.split(NON_ARGS_DELIMITER);
        String[] nonArgs = dataGroups[0].split(DATA_DELIMITER);
        String[] args = null;

        if (dataGroups.length > 1) {
            args = dataGroups[1].split(DATA_DELIMITER);
        }

        return QueryDto.builder()
                .typeKey(nonArgs[0])
                .statusKey(nonArgs[1])
                .args(args).build();
    }
}
