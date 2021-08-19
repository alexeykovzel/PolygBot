package com.alexeykovzel.bot.feature.saveword;

import com.alexeykovzel.bot.feature.query.QueryType;
import com.alexeykovzel.bot.feature.query.BasicQuery;
import com.alexeykovzel.db.model.casestudy.CaseStudy;
import com.alexeykovzel.db.repository.TermRepository;
import com.alexeykovzel.db.service.CaseStudyDataService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.Timestamp;

public class SaveWordQuery extends BasicQuery {
    public static final QueryType type = QueryType.SAVE_WORD;
    private final CaseStudyDataService caseStudyDataService;
    private final TermRepository termRepository;
    private SaveWordStatus status;

    public SaveWordQuery(String[] args, AbsSender absSender, CallbackQuery basicQuery,
                         TermRepository termRepository, CaseStudyDataService caseStudyDataService) {
        super(args, absSender, basicQuery);
        this.termRepository = termRepository;
        this.caseStudyDataService = caseStudyDataService;
    }

    @Override
    public void execute() {
        Message message = basicQuery.getMessage();
        String queryId = basicQuery.getId();
        String chatId = message.getChatId().toString();
        Integer messageId = message.getMessageId();

        switch (status) {
            case SAVE:
                String termValue = args[0];
                Long termId = termRepository.findIdByValue(termValue);

                caseStudyDataService.save(new CaseStudy(termId, chatId,
                        0.5, new Timestamp(System.currentTimeMillis())));

                deleteMsg(chatId, messageId);
                sendAnswerCallbackQuery(String.format("The word '%s' is successfully added to your list",
                        termValue), false, queryId);
                break;
            case IGNORE:
                deleteMsg(chatId, messageId);
                sendAnswerCallbackQuery(queryId);
                break;
        }
    }

    @Override
    public void setStatusByKey(String key) {
        SaveWordStatus status = SaveWordStatus.fromKey(key);
        if (status != null) {
            this.status = status;
        }
    }

    @AllArgsConstructor
    public enum SaveWordStatus {
        SAVE("0"), IGNORE("1");

        @Getter
        private String key;

        private static SaveWordStatus fromKey(String key) {
            for (SaveWordStatus status : values()) {
                if (status.key.equals(key)) {
                    return status;
                }
            }
            return null;
        }
    }
}
