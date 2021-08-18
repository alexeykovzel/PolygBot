package com.alexeykovzel.bot.query;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallbackQueryDto {
    private String typeKey;
    private String statusKey;
    private String[] args;
}
