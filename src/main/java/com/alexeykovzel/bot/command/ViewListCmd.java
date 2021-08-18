package com.alexeykovzel.bot.command;

import com.alexeykovzel.bot.feature.viewlist.ViewListQuery;
import com.alexeykovzel.db.service.CaseStudyDataService;
import org.json.JSONObject;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;

import static com.alexeykovzel.bot.feature.viewlist.ViewListBuilder.getListViewMarkup;

public class ViewListCmd extends BotCommand {
    private final static int maxTermsPerPage = ViewListQuery.maxTermsPerPage;
    private static final String COMMAND_IDENTIFIER = "vocab";
    private static final String COMMAND_DESCRIPTION = "shows user vocabulary";
    private final CaseStudyDataService caseStudyDataService;

    public ViewListCmd(CaseStudyDataService caseStudyDataService) {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
        this.caseStudyDataService = caseStudyDataService;
    }

    public void execute(AbsSender absSender, User user, Chat chat, String[] args) {
        String chatId = chat.getId().toString();
        Optional<List<String>> optTermValues = caseStudyDataService.findTermValuesByChatId(chatId);

        if (optTermValues.isPresent()) {
            List<String> terms = optTermValues.get();
            int defaultPage = 1;

            String message = String.format("Your list consists of *%s* words! You can click the word to get its full info", terms.size());
            try {
                absSender.execute(SendMessage.builder().text(message).chatId(chatId)
                        .replyMarkup(getListViewMarkup(terms, defaultPage))
                        .parseMode(ParseMode.MARKDOWN).build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            String message = "Right now, your list is empty";
            try {
                absSender.execute(SendMessage.builder().text(message).chatId(chatId).parseMode(ParseMode.MARKDOWN).build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
