package com.alexeykovzel.bot.handler;

import com.alexeykovzel.bot.command.HelpCmd;
import com.alexeykovzel.bot.config.EmojiConfig;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class AWSPolygBotHandler extends TelegramBotHandler {
    private static final Properties properties = new Properties();
    private static AWSPolygBotHandler polygBotController;
    private final String botToken;

    @Getter
    private final String botUsername;

    @Override
    public String getBotToken() {
        return botToken;
    }

    public static synchronized AWSPolygBotHandler getInstance(String botUsername, String botToken) {
        if (polygBotController == null) {
            polygBotController = new AWSPolygBotHandler(botUsername, botToken);
        }
        return polygBotController;
    }

    public AWSPolygBotHandler(String botUsername, String botToken) {
        this.botToken = botToken;
        this.botUsername = botUsername;

        commandRegistry = new CommandRegistry(true, () -> botUsername);
        HelpCmd helpCmd = new HelpCmd();
//        commandRegistry.registerAll(helpCommand, new StartCommand(helpCommand), new HelloCommand());

        commandRegistry.registerDefaultAction((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(String.valueOf(message.getChatId()));
            commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot. Here comes some help " + EmojiConfig.AMBULANCE);
            try {
                absSender.execute(commandUnknownMessage);
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
        CallbackQuery callbackquery = update.getCallbackQuery();
        Message message = callbackquery.getMessage();
        Integer messageId = message.getMessageId();
        String chatId = String.valueOf(message.getChatId());
        String command = callbackquery.getData();
        String commandQuery = null;
        if (command.contains("@")) {
            int separatorIndex = command.indexOf("@");
            commandQuery = command.substring(separatorIndex + 1);
            command = command.substring(0, separatorIndex);
        }
        switch (command) {
            case "saveWord":
                sendMsg(chatId, "I will do my best!" + EmojiConfig.SMILING_FACE_WITH_OPEN_MOUTH_AND_SMILING_EYES);
                    /*if (!WordHome.isDublicate(chatId, commandQuery)) {
                        assert commandQuery != null;
                        WordHome.saveWord(chatId, commandQuery, 0.7);
                        sendAnswerCallbackQuery("the word '" + commandQuery + "' is successfully added to your list!", false, callbackquery);
                    }*/
                break;
            case "notSaveWord":
                break;
        }
        deleteMsg(chatId, messageId);
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
        Document doc;
        try {
            String searchQuery = "https://www.collinsdictionary.com/dictionary/english/" + toSearchForm(messageText);
            doc = Jsoup.connect(searchQuery).get();
            try {
                Element body = doc.body();
                String origTerm = body.getElementsByClass("orth").first().text();

                // send term info message
                sendMsg(chatId, buildTermInfoMessage(body, origTerm, searchQuery).toString());

                // send message query to add a term to user local vocabulary
//                    if (!WordHome.isDublicate(chatId, trueTerm)) {
                sendMsg(chatId, "Would you like to add '*" + origTerm + "*' to your local vocabulary?", buildWordAddReplyMarkup(origTerm));
//                    }
            } catch (NullPointerException e) {
                sendMsg(chatId, "Ahh, I don't know what is '*" + messageText + "*' " + EmojiConfig.DISAPPOINTED_BUT_RELIEVED_FACE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup buildWordAddReplyMarkup(String wordText) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = Collections.singletonList(
                Arrays.asList(
                        createInlineKeyboardButton("Actually, I do!", "saveWord@" + wordText),
                        createInlineKeyboardButton("Not really...", "notSaveWord@" + wordText)
                )
        );

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }

    private StringBuilder buildTermInfoMessage(Element body, String origTerm, String searchQuery) {
        StringBuilder response = new StringBuilder();
        Elements defList = body.getElementsByClass("hom");
        Elements examplesList = body.getElementsByClass("quote");

        // Add 'definitions' section
        response.append(getDefSection(defList, 2));

        // Add 'examples' section
        response.append(getExamplesSection(examplesList, 3));

        // Add 'more' section
        response.append("[More](").append(searchQuery).append(") about '*").append(origTerm).append("*'");

        return response;
    }

    private StringBuilder getDefSection(Elements defList, int maxValue) {
        StringBuilder defSection = new StringBuilder();
        if (!defList.isEmpty()) {

            if (defList.size() >= maxValue) {
                for (int i = 0; i < maxValue; i++) {
                    defSection.append(getDefText(defList.get(i)));
                }
            } else {
                for (Element defElement : defList) {
                    defSection.append(getDefText(defElement));
                }
            }
        }
        return defSection;
    }

    private String getDefText(Element defSection) {
        StringBuilder defText = new StringBuilder();

        Element defElement = defSection.getElementsByClass("def").first();
        if (defElement != null) {
            Element posElement = defSection.getElementsByClass("pos").first();
            if (posElement != null) {
                defText.append("*[").append(escapeMarkdown(posElement.text().toUpperCase())).append("]* ");
            }
            defText.append(defElement.text()).append("\n\n");
        }
        return defText.toString();
    }

    private StringBuilder getExamplesSection(Elements examplesList, int maxValue) {
        StringBuilder examplesSection = new StringBuilder();

        if (!examplesList.isEmpty()) {
            examplesSection.append("*EXAMPLES*\n\n");
            StringBuilder examples = new StringBuilder();
            if (examplesList.size() >= maxValue) {
                for (int i = 0; i < maxValue; i++) {
                    examples.append("- ").append(examplesList.get(i).text()).append("\n");
                }
            } else {
                for (Element exampleElement : examplesList) {
                    examples.append("- ").append(exampleElement.text()).append("\n");
                }
            }
            examplesSection.append(escapeMarkdown(examples.toString())).append("\n");
        }
        return examplesSection;
    }

    private String toSearchForm(String text) {
        return text.toLowerCase().replace(" ", "-");
    }
}
