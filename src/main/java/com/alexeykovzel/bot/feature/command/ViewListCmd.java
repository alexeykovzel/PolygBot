package com.alexeykovzel.bot.feature.command;

import com.alexeykovzel.bot.feature.viewlist.ViewListBuilder;
import com.alexeykovzel.db.service.CaseStudyDataService;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

public class ViewListCmd extends BotCommand {
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

            String text = String.format("Your list consists of *%s* words! You can click the word to get its full info", terms.size());
            SendMessage message = SendMessage.builder().text(text).chatId(chatId)
                    .replyMarkup(ViewListBuilder.getListViewMarkup(terms, defaultPage))
                    .parseMode(ParseMode.MARKDOWN).build();

            try {
                absSender.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            String text = "Right now, your list is empty";
            SendMessage message = SendMessage.builder().text(text).chatId(chatId)
                    .parseMode(ParseMode.MARKDOWN).build();

            try {
                absSender.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
