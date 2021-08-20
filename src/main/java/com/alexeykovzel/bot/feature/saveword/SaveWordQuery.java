package com.alexeykovzel.bot.feature.saveword;

import com.alexeykovzel.bot.query.QueryTemplate;
import com.alexeykovzel.bot.query.QueryDto;
import com.alexeykovzel.bot.query.QueryType;
import com.alexeykovzel.db.model.casestudy.CaseStudy;
import com.alexeykovzel.db.repository.TermRepository;
import com.alexeykovzel.db.service.CaseStudyDataService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.Timestamp;

public class SaveWordQuery extends QueryTemplate {
    private final CaseStudyDataService caseStudyDataService;
    private final TermRepository termRepository;

    public SaveWordQuery(TermRepository termRepository, CaseStudyDataService caseStudyDataService) {
        this.termRepository = termRepository;
        this.caseStudyDataService = caseStudyDataService;
    }

    @Override
    public void execute(AbsSender absSender, QueryDto queryDto, CallbackQuery basicQuery) {
        Message message = basicQuery.getMessage();
        String chatId = message.getChatId().toString();
        Integer messageId = message.getMessageId();
        String queryId = basicQuery.getId();
        String[] args = queryDto.getArgs();

        SaveWordStatus status = SaveWordStatus.fromKey(queryDto.getStatusKey());
        if (status != null) {
            switch (status) {
                case SAVE -> {
                    String termValue = args[0];
                    Long termId = termRepository.findIdByValue(termValue);
                    caseStudyDataService.save(new CaseStudy(termId, chatId,
                            0.5, new Timestamp(System.currentTimeMillis())));

                    executeApiMethod(absSender, DeleteMessage.builder().chatId(chatId).messageId(messageId).build());

                    String response = String.format("The word '%s' is successfully added to your list", termValue);
                    executeApiMethod(absSender, AnswerCallbackQuery.builder().callbackQueryId(queryId)
                            .text(response).showAlert(false).build());
                }
                case IGNORE -> {
                    executeApiMethod(absSender, DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
                    executeApiMethod(absSender, AnswerCallbackQuery.builder().callbackQueryId(queryId).build());
                }
            }
        }
    }

    @Override
    public QueryType getType() {
        return QueryType.SAVE_WORD;
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
