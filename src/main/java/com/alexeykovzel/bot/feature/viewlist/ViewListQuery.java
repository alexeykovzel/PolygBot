package com.alexeykovzel.bot.feature.viewlist;

import com.alexeykovzel.bot.query.CallbackQueryType;
import com.alexeykovzel.bot.query.DefaultCallbackQuery;
import com.alexeykovzel.db.service.CaseStudyDataService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.alexeykovzel.bot.feature.viewlist.ViewListBuilder.getAlphabeticalMarkup;
import static com.alexeykovzel.bot.feature.viewlist.ViewListBuilder.getListViewMarkup;

public class ViewListQuery extends DefaultCallbackQuery {
    private static final CallbackQueryType type = CallbackQueryType.VIEW_LIST;
    public final static int maxTermsPerPage = 5;

    private final CaseStudyDataService caseStudyDataService;
    private ViewListStatus status;

    public ViewListQuery(String[] args, AbsSender absSender, CallbackQuery basicQuery,
                         CaseStudyDataService caseStudyDataService) {
        super(args, absSender, basicQuery);
        this.caseStudyDataService = caseStudyDataService;
    }

    @Override
    public void execute() {
        Message message = basicQuery.getMessage();
        String chatId = message.getChatId().toString();
        Integer messageId = message.getMessageId();

        switch (status) {
            case DEFAULT:
                int page = Integer.parseInt(args[0]);
                Optional<List<String>> optTermValues = caseStudyDataService.findTermValuesByChatId(chatId);
                optTermValues.ifPresent(terms -> editMessageReplyMarkup(chatId,
                        messageId, getListViewMarkup(terms, page)));
                break;
            case PAGE_PANEL:
                editMessageReplyMarkup(chatId, messageId, getAlphabeticalMarkup(7));
                break;
            case ABC:
                page = Integer.parseInt(args[0]);
                char letter = args[1].charAt(0);
                optTermValues = caseStudyDataService.findTermValuesByChatId(chatId);
                optTermValues.ifPresent(terms -> {
                    List<String> termsByLetter = terms.stream()
                            .collect(Collectors.partitioningBy(s -> s.charAt(0) == letter)).get(Boolean.TRUE);
                    editMessageReplyMarkup(chatId, messageId, getListViewMarkup(termsByLetter, page));
                });
                break;
        }
    }

    @Override
    public void setStatusByKey(String key) {
        ViewListStatus status = ViewListStatus.fromKey(key);
        if (status != null) {
            this.status = status;
        }
    }

    @AllArgsConstructor
    public enum ViewListStatus {
        PAGE_PANEL("0"), DEFAULT("1"), ABC("2");

        @Getter
        private String key;

        private static ViewListStatus fromKey(String key) {
            for (ViewListStatus status : values()) {
                if (status.key.equals(key)) {
                    return status;
                }
            }
            return null;
        }
    }
}
