package com.alexeykovzel.bot.handler.localhost;

import com.alexeykovzel.bot.config.EmojiConfig;
import com.alexeykovzel.bot.feature.saveword.SaveWordBuilder;
import com.alexeykovzel.bot.feature.viewlist.ViewListQuery;
import com.alexeykovzel.bot.util.MessageBuilder;
import com.alexeykovzel.bot.command.HelpCmd;
import com.alexeykovzel.bot.command.StartCmd;
import com.alexeykovzel.bot.command.ViewListCmd;
import com.alexeykovzel.bot.query.CallbackQueryDto;
import com.alexeykovzel.bot.query.Query;
import com.alexeykovzel.bot.query.CallbackQueryType;
import com.alexeykovzel.bot.feature.saveword.SaveWordQuery;
import com.alexeykovzel.bot.util.DataConverter;
import com.alexeykovzel.bot.util.QueryDataConverter;
import com.alexeykovzel.db.model.term.Term;
import com.alexeykovzel.db.model.term.TermDef;
import com.alexeykovzel.db.model.term.TermDto;
import com.alexeykovzel.db.repository.ChatRepository;
import com.alexeykovzel.db.repository.TermRepository;
import com.alexeykovzel.db.service.CaseStudyDataService;
import com.alexeykovzel.service.CollinsDictionaryAPI;
import com.alexeykovzel.service.WebDictionary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

@Component
public class PolygBotHandler extends LongPollingBotHandler {
    private static final Properties properties = new Properties();
    private final ChatRepository chatRepository;
    private final TermRepository termRepository;
    private final CaseStudyDataService caseStudyDataService;

    private static final String botToken = "1402979569:AAEuPHqAzkc1cTYwGI7DXuVb76ZSptD4zPM";
    private static final String botUsername = "polyg_bot";

    public PolygBotHandler(ChatRepository chatRepository, TermRepository termRepository,
                           CaseStudyDataService caseStudyDataService) {
        this.chatRepository = chatRepository;
        this.termRepository = termRepository;
        this.caseStudyDataService = caseStudyDataService;
        setCommandRegistry();
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
        commandRegistry = new CommandRegistry(true, () -> botUsername);
        HelpCmd helpCmd = new HelpCmd();
        commandRegistry.registerAll(helpCmd,
                new StartCmd(helpCmd, chatRepository),
                new ViewListCmd(caseStudyDataService));

        commandRegistry.registerDefaultAction((absSender, message) -> {
            try {
                absSender.execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(String.format("The command '%s' is not known by this bot. Here comes some help %s",
                                message.getText(), EmojiConfig.AMBULANCE)).build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            helpCmd.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });
    }


    @Override
    public void handleInvalidCommandUpdate(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        sendMsg(chatId, "I don't know such command");
    }


    @Override
    public void handleCallbackQuery(Update update) {
        CallbackQuery basicQuery = update.getCallbackQuery();
        DataConverter<String, CallbackQueryDto> dataConverter = new QueryDataConverter();
        CallbackQueryDto callbackQueryDto = dataConverter.decode(basicQuery.getData());
        CallbackQueryType callbackQueryType = CallbackQueryType.fromId(callbackQueryDto.getTypeKey());
        Query query;

        if (callbackQueryType != null) {
            switch (callbackQueryType) {
                case SAVE_WORD:
                    query = new SaveWordQuery(callbackQueryDto.getArgs(), this, basicQuery,
                            termRepository, caseStudyDataService);
                    break;
                case VIEW_LIST:
                    query = new ViewListQuery(callbackQueryDto.getArgs(), this, basicQuery,
                            caseStudyDataService);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid callback query type!");
            }
            query.setStatusByKey(callbackQueryDto.getStatusKey());
            query.execute();
        } else {
            throw new NullPointerException();
        }
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
        sendMsg(chatId, "Send pls text");
    }

    @Override
    public void handleTextMessage(Message message) {
        String chatId = message.getChatId().toString();
        String messageText = message.getText();
        WebDictionary dictionary = new CollinsDictionaryAPI();

        try {
            TermDto termDto = dictionary.getTerm(messageText);
            sendMsg(chatId, MessageBuilder.buildTermInfoMessage(termDto).toString());

            String termValue = termDto.getValue();
            Long termId = termRepository.findIdByValue(termValue);
            boolean queryRequired;

            if (termId != null) {
                queryRequired = !caseStudyDataService.findById(termId, chatId).isPresent();
            } else {
                Set<String> termExamples = new HashSet<>(termDto.getExamples());

                Set<TermDef> termDefs = new HashSet<>();
                termDto.getDefs().forEach(def -> termDefs.add(new TermDef(def.getFirst(), def.getSecond())));

                termRepository.save(Term.builder().value(termValue).defs(termDefs).examples(termExamples).build());
                queryRequired = true;
            }
            if (queryRequired) {
                sendMsg(chatId, String.format("Would you like to learn '*%s*'?", termValue),
                        SaveWordBuilder.buildSaveWordMarkup(termValue));
            }

        } catch (NullPointerException e) {
            sendMsg(chatId, String.format("Ahh, I don't know what is '*%s*' %s",
                    messageText, EmojiConfig.DISAPPOINTED_BUT_RELIEVED_FACE));

        } catch (IOException e) {
            sendMsg(chatId, String.format("%s is not responding.. %s",
                    dictionary.getName(), EmojiConfig.FACE_WITH_COLD_SWEAT));
        }
    }
}
