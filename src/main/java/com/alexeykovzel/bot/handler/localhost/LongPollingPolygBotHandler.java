package com.alexeykovzel.bot.handler.localhost;

import com.alexeykovzel.bot.config.EmojiConfig;
import com.alexeykovzel.bot.feature.saveword.SaveWordBuilder;
import com.alexeykovzel.bot.feature.termdef.TermInfoBuilder;
import com.alexeykovzel.bot.feature.command.HelpCommand;
import com.alexeykovzel.bot.feature.command.StartCommand;
import com.alexeykovzel.bot.feature.viewlist.ViewListCommand;
import com.alexeykovzel.bot.feature.saveword.SaveWordQuery;
import com.alexeykovzel.bot.feature.viewlist.ViewListQuery;
import com.alexeykovzel.db.model.term.Term;
import com.alexeykovzel.db.model.term.TermDef;
import com.alexeykovzel.db.model.term.TermDto;
import com.alexeykovzel.db.repository.ChatRepository;
import com.alexeykovzel.db.repository.TermRepository;
import com.alexeykovzel.db.service.CaseStudyDataService;
import com.alexeykovzel.service.CollinsDictionaryAPI;
import com.alexeykovzel.service.WebDictionary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

@Component
public class LongPollingPolygBotHandler extends LongPollingBotHandler {
    private static final Properties properties = new Properties();
    private final ChatRepository chatRepository;
    private final TermRepository termRepository;
    private final CaseStudyDataService caseStudyDataService;

    private static final String botToken = "1402979569:AAEuPHqAzkc1cTYwGI7DXuVb76ZSptD4zPM";
    private static final String botUsername = "polyg_bot";

    public LongPollingPolygBotHandler(ChatRepository chatRepository, TermRepository termRepository,
                                      CaseStudyDataService caseStudyDataService) {
        this.chatRepository = chatRepository;
        this.termRepository = termRepository;
        this.caseStudyDataService = caseStudyDataService;
        setCommandRegistry();
        setQueryRegistry();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handleUpdate(update);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void setCommandRegistry() {
        HelpCommand helpCommand = new HelpCommand();
        commandRegistry.registerAll(helpCommand,
                new StartCommand(helpCommand, chatRepository),
                new ViewListCommand(caseStudyDataService));

        commandRegistry.registerDefaultAction((absSender, message) -> {
            executeApiMethod(SendMessage.builder().chatId(message.getChatId().toString())
                    .text(String.format("The command '%s' is not known by this bot. Here comes some help %s",
                            message.getText(), EmojiConfig.AMBULANCE))
                    .parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());

            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });
    }

    private void setQueryRegistry() {
        queryRegistry.registerAll(
                new SaveWordQuery(termRepository, caseStudyDataService),
                new ViewListQuery(caseStudyDataService));
    }

    @Override
    public void handleInvalidCommandUpdate(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        executeApiMethod(SendMessage.builder().chatId(chatId)
                .text("I don't know such command")
                .parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());
    }


    @Override
    public void handleCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        queryRegistry.executeQuery(this, callbackQuery);
    }

    @Override
    public void handleNonCommandUpdate(Update update) {
        Message message = update.getMessage();
        if (!message.hasText()) {
            handleNonTextMessage(message);
        } else {
            handleTextMessage(message);
        }
    }

    @Override
    public void handleNonTextMessage(Message message) {
        String chatId = message.getChatId().toString();
        executeApiMethod(SendMessage.builder().chatId(chatId)
                .text("Send pls text")
                .parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());
    }

    @Override
    public void handleTextMessage(Message message) {
        String chatId = message.getChatId().toString();
        String messageText = message.getText();
        WebDictionary dictionary = new CollinsDictionaryAPI();

        try {
            TermDto termDto = dictionary.getTerm(messageText);

            executeApiMethod(SendMessage.builder().chatId(chatId)
                    .text(TermInfoBuilder.buildTermInfoMessage(termDto).toString())
                    .parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());

            String termValue = termDto.getValue();
            Long termId = termRepository.findIdByValue(termValue);
            boolean queryRequired;

            if (termId != null) {
                queryRequired = caseStudyDataService.findById(termId, chatId).isEmpty();
            } else {
                Set<String> termExamples = new HashSet<>(termDto.getExamples());

                Set<TermDef> termDefs = new HashSet<>();
                termDto.getDefs().forEach(def -> termDefs.add(new TermDef(def.getFirst(), def.getSecond())));

                termRepository.save(Term.builder().value(termValue).defs(termDefs).examples(termExamples).build());
                queryRequired = true;
            }
            if (queryRequired) {
                executeApiMethod(SendMessage.builder().chatId(chatId)
                        .text(String.format("Would you like to learn '*%s*'?", termValue))
                        .replyMarkup(SaveWordBuilder.buildSaveWordMarkup(termValue))
                        .parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());
            }

        } catch (NullPointerException e) {
            executeApiMethod(SendMessage.builder().chatId(chatId)
                    .text(String.format("Ahh, I don't know what is '*%s*' %s",
                            messageText, EmojiConfig.DISAPPOINTED_BUT_RELIEVED_FACE))
                    .parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());

        } catch (IOException e) {
            executeApiMethod(SendMessage.builder().chatId(chatId)
                    .text(String.format("%s is not responding.. %s",
                            dictionary.getName(), EmojiConfig.FACE_WITH_COLD_SWEAT))
                    .parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());
        }
    }
}
