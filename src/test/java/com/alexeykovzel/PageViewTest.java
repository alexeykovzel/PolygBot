package com.alexeykovzel;

import org.eclipse.collections.impl.list.Interval;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PageViewTest {
    List<Character> alphabet = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

    @Test
    void getPageView() throws TelegramApiException {
        String chatId = "597554184";

        AbsSender absSender = new DefaultAbsSender(new DefaultBotOptions()) {
            @Override
            public String getBotToken() {
                return "1402979569:AAEuPHqAzkc1cTYwGI7DXuVb76ZSptD4zPM";
            }
        };

        absSender.execute(SendMessage.builder().text("Page view:").chatId(chatId)
                .replyMarkup(getPageViewMarkup(15, 7))
                .parseMode(ParseMode.MARKDOWN).build());
    }

    private InlineKeyboardMarkup getPageViewMarkup(int maxPage, int elPerRow) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

//        List<Integer> intList = Interval.oneTo(maxPage);
        Map<Integer, List<Character>> groups =
                alphabet.stream().collect(Collectors.groupingBy(s -> alphabet.indexOf(s) / elPerRow));

        groups.values().forEach(list -> {
            List<InlineKeyboardButton> row = new ArrayList<>();
            list.forEach(el -> row.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(el))
                    .callbackData(new JSONObject()
                            .put("command", "turn_page")
                            .put("status", "list_view")
                            .put("page", el).toString()).build()));
            rowList.add(row);
        });
        markup.setKeyboard(rowList);
        return markup;
    }
}
