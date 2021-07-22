package com.alexeykovzel.bot.handler;

import com.alexeykovzel.bot.Emoji;
import com.alexeykovzel.bot.MessageBuilder;
import com.alexeykovzel.bot.cmd.HelpCmd;
import com.alexeykovzel.bot.cmd.StartCmd;
import com.alexeykovzel.bot.cmd.VocabCmd;
import com.alexeykovzel.bot.query.QueryDto;
import com.alexeykovzel.bot.query.QueryExecutor;
import com.alexeykovzel.bot.query.QueryType;
import com.alexeykovzel.bot.query.SaveWordQuery;
import com.alexeykovzel.bot.query.converter.DataConverter;
import com.alexeykovzel.bot.query.converter.QueryDataConverter;
import com.alexeykovzel.db.model.term.Term;
import com.alexeykovzel.db.model.term.TermDef;
import com.alexeykovzel.db.model.term.TermDto;
import com.alexeykovzel.db.repository.ChatRepository;
import com.alexeykovzel.db.repository.TermRepository;
import com.alexeykovzel.db.service.CaseStudyDataService;
import com.alexeykovzel.service.CollinsDictionaryAPI;
import com.alexeykovzel.service.WebDictionary;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

import static com.alexeykovzel.bot.query.QueryType.*;

@Component
public class LocalPolygBotHandler extends LongPollingBotHandler {
    private static final Properties properties = new Properties();
    private final ChatRepository chatRepository;
    private final TermRepository termRepository;
    private final CaseStudyDataService caseStudyDataService;

    private static final String botToken = "1402979569:AAEuPHqAzkc1cTYwGI7DXuVb76ZSptD4zPM";
    private static final String botUsername = "polyg_bot";


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

    public LocalPolygBotHandler(ChatRepository chatRepository, TermRepository termRepository,
                                CaseStudyDataService caseStudyDataService) {
        setCommandRegistry();
        this.chatRepository = chatRepository;
        this.termRepository = termRepository;
        this.caseStudyDataService = caseStudyDataService;
    }

    private void setCommandRegistry() {
        commandRegistry = new CommandRegistry(true, () -> botUsername);
        HelpCmd helpCmd = new HelpCmd();
        commandRegistry.registerAll(helpCmd, new StartCmd(helpCmd, chatRepository),
                new VocabCmd(caseStudyDataService));

        commandRegistry.registerDefaultAction((absSender, message) -> {
            try {
                absSender.execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(String.format("The command '%s' is not known by this bot. Here comes some help %s",
                                message.getText(), Emoji.AMBULANCE)).build());
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
        Message message = basicQuery.getMessage();
        Integer messageId = message.getMessageId();
        String chatId = message.getChatId().toString();
        String queryId = basicQuery.getId();

        DataConverter<String, QueryDto> dataConverter = new QueryDataConverter();
        QueryDto queryDto = dataConverter.decode(basicQuery.getData());
        QueryType queryType = QueryType.fromId(queryDto.getTypeKey());
        QueryExecutor query;

        if (queryType != null) {
            switch (queryType) {
                case SAVE_WORD:
                    System.out.println("Saving word...");
                    query = new SaveWordQuery(queryDto.getArgs(), this);
                    break;
                case VIEW_LIST:
                    System.out.println("Viewing list...");
                    query = new SaveWordQuery(queryDto.getArgs(), this);
                    break;
                case SELECT_ITEM:
                    System.out.println("Selecting item...");
                    query = new SaveWordQuery(queryDto.getArgs(), this);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid query type!");
            }
            query.setStatusByKey(queryDto.getStatusKey());
            query.execute();
        }

        /*QueryArgsConverter dataHandler = new QueryArgsConverter(query.getData());
        Query.Type type = dataHandler.getType();

        if (type != null) {
            switch (type) {
                case SAVE_WORD_QUERY:
                    String termValue = dataHandler.getTermValue();
                    Long termId = termRepository.findIdByValue(termValue);

                    caseStudyDataService.save(new CaseStudy(termId, chatId,
                            0.5, new Timestamp(System.currentTimeMillis())));

                    deleteMsg(chatId, messageId);
                    sendAnswerCallbackQuery(String.format("The word '%s' is successfully added to your list",
                            termValue), false, queryId);
                    break;
                case NOT_SAVE_WORD_QUERY:
                    deleteMsg(chatId, messageId);
                    sendAnswerCallbackQuery(queryId);
                    break;

                case SELECT_ITEM_QUERY:
                    sendAnswerCallbackQuery(queryId);
                    break;

                case LIST_VIEW_QUERY:
                    VocabCmd.VocabStatus vocabStatus = dataHandler.getStatus();
                    String[] args;

                    switch (vocabStatus) {
                        case PAGE_PANEL_STATUS:
                            args = new String[]{String.valueOf(vocabStatus.id), messageId.toString()};
                            break;
                        case LIST_VIEW_STATUS:
                            args = new String[]{String.valueOf(vocabStatus.id), messageId.toString(),
                                    dataHandler.getPage()};
                            break;
                        case LIST_VIEW_ABS_STATUS:
                            args = new String[]{String.valueOf(vocabStatus.id), messageId.toString(),
                                    dataHandler.getPage(), dataHandler.getLetter()};
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + vocabStatus);
                    }

                    ((VocabCmd) commandRegistry.getRegisteredCommand("vocab"))
                            .execute(this, message.getFrom(), message.getChat(), args);
                    sendAnswerCallbackQuery(queryId);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
        } else {
            throw new NullPointerException();
        }*/
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
            //get and send term details
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
                        buildWordSaveMarkup(termValue));
            }

        } catch (NullPointerException e) {
            sendMsg(chatId, String.format("Ahh, I don't know what is '*%s*' %s",
                    messageText, Emoji.DISAPPOINTED_BUT_RELIEVED_FACE));

        } catch (IOException e) {
            sendMsg(chatId, String.format("%s is not responding.. %s",
                    dictionary.getName(), Emoji.FACE_WITH_COLD_SWEAT));
        }
    }

    private InlineKeyboardMarkup buildWordSaveMarkup(String wordText) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = Collections.singletonList(
                Arrays.asList(
                        createInlineKeyboardButton("Actually, I do!",
                                new JSONObject().put("cmd", "save_word").put("value", wordText).toString()),

                        createInlineKeyboardButton("Not really...",
                                new JSONObject().put("cmd", "not_save_word").put("value", wordText).toString())
                )
        );
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
