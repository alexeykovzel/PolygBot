package com.alexeykovzel.bot.feature.viewlist;

import com.alexeykovzel.bot.feature.query.QueryBuilder;
import com.alexeykovzel.bot.feature.query.QueryType;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.eclipse.collections.impl.list.Interval;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.alexeykovzel.bot.feature.viewlist.ViewListQuery.ViewListStatus.*;
import static com.alexeykovzel.bot.feature.viewlist.ViewListQuery.maxTermsPerPage;

public class ViewListBuilder extends QueryBuilder {
    private static final QueryType type = QueryType.VIEW_LIST;
    private static final List<Character> alphabet = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

    public static InlineKeyboardMarkup getAlphabeticalMarkup(int elPerRow) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        Map<Integer, List<Character>> groups =
                alphabet.stream().collect(Collectors.groupingBy(s -> alphabet.indexOf(s) / elPerRow));

        groups.values().forEach(list -> {
            List<InlineKeyboardButton> row = new ArrayList<>();
            list.forEach(el -> {
                String letter = String.valueOf(el);
                row.add(InlineKeyboardButton.builder()
                        .text(letter)
                        .callbackData(buildCallbackData(type.getKey(), DEFAULT.getKey(), new String[]{letter})).build());
            });
            rowList.add(row);
        });
        rowList.get(rowList.size() - 1).add(InlineKeyboardButton.builder()
                .text("←")
                .callbackData(buildCallbackData(type.getKey(), DEFAULT.getKey(), new String[]{"1"})).build());
        markup.setKeyboard(rowList);
        return markup;
    }

    public static InlineKeyboardMarkup getPagePanelMarkup(int elNum, int elPerRow) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        Map<Integer, List<Integer>> groups =
                Interval.oneTo(elNum).stream().collect(Collectors.groupingBy(s -> (s - 1) / elPerRow));

        groups.values().forEach(list -> {
            List<InlineKeyboardButton> row = new ArrayList<>();
            list.forEach(el -> {
                String letter = String.valueOf(el);
                row.add(InlineKeyboardButton.builder()
                        .text(letter)
                        .callbackData(buildCallbackData(type.getKey(), DEFAULT.getKey(), new String[]{letter})).build());
            });
            rowList.add(row);
        });
        markup.setKeyboard(rowList);
        return markup;
    }

    public static InlineKeyboardMarkup getListViewMarkup(List<String> terms, int page) {
        int numOfTerms = terms.size();
        int maxPage = (int) Math.ceil((double) numOfTerms / maxTermsPerPage);

        int indexI = (page - 1) * maxTermsPerPage;
        int indexF = page == maxPage ? numOfTerms : indexI + maxTermsPerPage;

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i = indexI; i < indexF; i++) {
            String termValue = terms.get(i);
            String callbackData = buildCallbackData(type.getKey(), DEFAULT.getKey(),
                    new String[]{String.valueOf(page), termValue});

            rowList.add(Collections.singletonList(InlineKeyboardButton.builder()
                    .text(termValue)
                    .callbackData(callbackData)
                    .build()));
        }

        if (maxPage > 1) {
            rowList.add(getPaginationPanel(page, maxPage));
        }

        return InlineKeyboardMarkup.builder().keyboard(rowList).build();
    }

    private static List<InlineKeyboardButton> getPaginationPanel(int page, int maxPage) {
        String prevPage = String.valueOf(page > 1 ? page - 1 : maxPage);
        String nextPage = String.valueOf(page < maxPage ? page + 1 : 1);

        return Arrays.asList(
                getPaginationBtn("«",
                        buildCallbackData(type.getKey(), DEFAULT.getKey(), new String[]{prevPage})),
                getPaginationBtn(page + " / " + maxPage,
                        buildCallbackData(type.getKey(), PAGE_PANEL.getKey(), new String[]{})),
                getPaginationBtn("A - Z",
                        buildCallbackData(type.getKey(), ABC_PANEL.getKey(), new String[]{})),
                getPaginationBtn("»",
                        buildCallbackData(type.getKey(), DEFAULT.getKey(), new String[]{nextPage})));
    }

    private static InlineKeyboardButton getPaginationBtn(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData).build();
    }
}
