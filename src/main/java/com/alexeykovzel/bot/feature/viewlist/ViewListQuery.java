package com.alexeykovzel.bot.feature.viewlist;

import com.alexeykovzel.bot.query.*;
import com.alexeykovzel.db.service.CaseStudyDataService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ViewListQuery extends QueryTemplate {
    private final CaseStudyDataService caseStudyDataService;
    public final int termsPerPage = ViewListBuilder.defaultTermsPerPage;

    public ViewListQuery(CaseStudyDataService caseStudyDataService) {
        this.caseStudyDataService = caseStudyDataService;
    }

    @Override
    public void execute(AbsSender absSender, QueryDto queryDto, CallbackQuery basicQuery) {
        Message message = basicQuery.getMessage();
        String chatId = message.getChatId().toString();
        Integer messageId = message.getMessageId();
        String[] args = queryDto.getArgs();
        ViewListStatus status = ViewListStatus.fromKey(queryDto.getStatusKey());

        if (status != null) {
            switch (status) {
                case DEFAULT:
                    int page = Integer.parseInt(args[0]);
                    Optional<List<String>> optTermValues = caseStudyDataService.findTermValuesByChatId(chatId);
                    optTermValues.ifPresent(terms -> executeApiMethod(absSender, EditMessageReplyMarkup.builder()
                            .chatId(chatId).messageId(messageId)
                            .replyMarkup(ViewListBuilder.getListViewMarkup(terms, page, termsPerPage)).build()));
                    break;
                case PAGE_PANEL:
                    optTermValues = caseStudyDataService.findTermValuesByChatId(chatId);
                    optTermValues.ifPresent(values -> {
                        int pagesSum = values.size() / termsPerPage;

                        executeApiMethod(absSender, EditMessageText.builder().chatId(chatId).messageId(messageId)
                                .text("Please select a page")
                                .replyMarkup(ViewListBuilder.getPagePanelMarkup(pagesSum,
                                        ViewListBuilder.defaultItemsPerRow)).build());
                    });
                    break;
                case ABC_VIEW:
                    page = Integer.parseInt(args[0]);
                    char letter = args[1].charAt(0);
                    optTermValues = caseStudyDataService.findTermValuesByChatId(chatId);
                    optTermValues.ifPresent(terms -> {
                        List<String> termsByLetter = terms.stream()
                                .collect(Collectors.partitioningBy(s -> s.charAt(0) == letter)).get(Boolean.TRUE);

                        executeApiMethod(absSender, EditMessageReplyMarkup.builder()
                                .chatId(chatId).messageId(messageId)
                                .replyMarkup(ViewListBuilder.getListViewMarkup(termsByLetter,
                                        page, termsPerPage)).build());
                    });
                    break;
                case ABC_PANEL:
                    executeApiMethod(absSender, EditMessageReplyMarkup.builder()
                            .chatId(chatId).messageId(messageId)
                            .replyMarkup(ViewListBuilder.getAlphabeticalMarkup(7)).build());
                    break;
            }
            AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                    .callbackQueryId(basicQuery.getId()).build();
            executeApiMethod(absSender, answerCallbackQuery);
        }
    }

    @Override
    public QueryType getType() {
        return QueryType.VIEW_LIST;
    }

    @AllArgsConstructor
    enum ViewListStatus {
        DEFAULT("0"), PAGE_PANEL("1"), ABC_VIEW("2"), ABC_PANEL("3");

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
