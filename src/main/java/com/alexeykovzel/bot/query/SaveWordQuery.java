package com.alexeykovzel.bot.query;

import com.alexeykovzel.db.model.casestudy.CaseStudy;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.sql.Timestamp;

public class SaveWordQuery extends TelegramBotQuery {
    public static final String typeId = QueryType.SAVE_WORD.id;
    private QueryStatus status;
    private String termValue;

    public SaveWordQuery(String[] args, AbsSender absSender) {
        super(args, absSender);
        initArgs(args);
    }

    @Override
    public void execute() { // Needed: AbsSender, TermRepository, CaseStudyDataService/CaseStudyRepository, CallbackQuery
        /*Long termId = termRepository.findIdByValue(termValue);

        caseStudyDataService.save(new CaseStudy(termId, chatId,
                0.5, new Timestamp(System.currentTimeMillis())));

        deleteMsg(chatId, messageId);
        sendAnswerCallbackQuery(String.format("The word '%s' is successfully added to your list",
                termValue), false, queryId);*/
    }

    @Override
    public void setStatusByKey(String key) {
        status = Status.fromKey(key);
    }

    private void initArgs(String[] args) {
        termValue = args[0];
    }

    public enum Status implements QueryStatus {
        SAVE("0"), IGNORE("1");

        public String id;

        Status(String id) {
            this.id = id;
        }

        public static QueryStatus fromKey(String key) {
            for (QueryStatus status : values()) {
                if (status.getKey().equals(key)) {
                    return status;
                }
            }
            return null;
        }

        @Override
        public String getKey() {
            return id;
        }
    }
}
