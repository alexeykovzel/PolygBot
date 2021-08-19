package com.alexeykovzel.bot.feature.query;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryDto {
    private String typeKey;
    private String statusKey;
    private String[] args;
}
