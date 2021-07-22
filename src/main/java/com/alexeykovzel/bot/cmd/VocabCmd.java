package com.alexeykovzel.bot.cmd;

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

public class VocabCmd extends BotCommand {
    private final static int maxTermsPerPage = 5;
    private static final String COMMAND_IDENTIFIER = "vocab";
    private static final String COMMAND_DESCRIPTION = "shows user vocabulary";
    private final CaseStudyDataService caseStudyDataService;

    List<Character> alphabet = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

    public VocabCmd(CaseStudyDataService caseStudyDataService) {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
        this.caseStudyDataService = caseStudyDataService;
    }

    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String chatId = chat.getId().toString();
        Optional<List<String>> optTermValues = caseStudyDataService.findTermValuesByChatId(chatId);

        if (optTermValues.isPresent()) {
            List<String> terms = optTermValues.get();
            int defaultPage = 1;

            if (arguments.length != 0) {
                JSONObject jsonData = new JSONObject(arguments[0]);
                if (jsonData.has("stat")) {
                    String status = jsonData.getString("stat");
                    int messageId;
                    int page;
                    switch (status) {
                        case "panel":
                            messageId = jsonData.getInt("message_id");
                            try {
                                absSender.execute(EditMessageReplyMarkup.builder().chatId(chatId).messageId(messageId)
                                        .replyMarkup(getPageViewMarkup(4)).build());
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "list":
                            messageId = jsonData.getInt("msg_id");
                            page = jsonData.getInt("pg");
                            try {
                                absSender.execute(EditMessageReplyMarkup.builder().chatId(chatId).messageId(messageId)
                                        .replyMarkup(getListViewMarkup(terms, page)).build());
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "abc":
                            messageId = jsonData.getInt("msg_id");
                            page = jsonData.getInt("pg");
                            char letter = jsonData.getString("s").charAt(0);

                            List<String> termsByLetter = terms.stream().collect(Collectors.partitioningBy(s -> s.charAt(0) == letter)).get(Boolean.TRUE);
                            try {
                                absSender.execute(EditMessageReplyMarkup.builder().chatId(chatId).messageId(messageId)
                                        .replyMarkup(getListViewMarkup(termsByLetter, page)).build());
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        default:
                            try {
                                absSender.execute(SendMessage.builder()
                                        .text("Invalid arguments on /vocab command!").chatId(chatId).build());
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                    }
                }
            } else {
                String message = String.format("Your list consists of *%s* words! You can click the word to get its full info", terms.size());
                try {
                    absSender.execute(SendMessage.builder().text(message).chatId(chatId)
                            .replyMarkup(getListViewMarkup(terms, defaultPage))
                            .parseMode(ParseMode.MARKDOWN).build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
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

    private InlineKeyboardMarkup getPageViewMarkup(int elPerRow) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

//        List<Integer> intList = Interval.oneTo(maxPage);
        Map<Integer, List<Character>> groups =
                alphabet.stream().collect(Collectors.groupingBy(s -> alphabet.indexOf(s) / elPerRow));

        groups.values().forEach(list -> {
            List<InlineKeyboardButton> row = new ArrayList<>();
            list.forEach(el -> row.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(el))
                    .callbackData(new JSONObject()
                            .put("cmd", "vocab")
                            .put("stat", "list")
                            .put("pg", el).toString()).build()));
            rowList.add(row);
        });
        markup.setKeyboard(rowList);
        return markup;
    }

    private InlineKeyboardMarkup getListViewMarkup(List<String> terms, int page) {
        int numOfTerms = terms.size();
        int maxPage = (int) Math.ceil((double) numOfTerms / maxTermsPerPage);

        int indexI = (page - 1) * maxTermsPerPage;
        int indexF = page == maxPage ? numOfTerms : indexI + maxTermsPerPage;

        String exm = new JSONObject()
                .put("cmd", "vocab")
                .put("stat", "list")
                .put("pg", "**")
                .put("val", "").toString();
        System.out.println(exm);
        System.out.println(exm.length());

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i = indexI; i < indexF; i++) {
            String termValue = terms.get(i);
            JSONObject jsonData = new JSONObject()
                    .put("cmd", "vocab")
                    .put("stat", "list")
                    .put("pg", page)
                    .put("val", termValue);

            rowList.add(Collections.singletonList(InlineKeyboardButton.builder()
                    .text(termValue)
                    .callbackData(jsonData.toString())
                    .build()));
        }

        if (maxPage > 1) {
            rowList.add(getPaginationPanel(page, maxPage));
        }

        return InlineKeyboardMarkup.builder().keyboard(rowList).build();
    }

    private List<InlineKeyboardButton> getPaginationPanel(int page, int maxPage) {
        int prevPage = page > 1 ? page - 1 : maxPage;
        int nextPage = page < maxPage ? page + 1 : 1;

        return Arrays.asList(
                getPaginationBtn("«", new JSONObject()
                        .put("cmd", "vocab")
                        .put("stat", "list")
                        .put("pg", prevPage)),
                getPaginationBtn(page + " / " + maxPage, new JSONObject()
                        .put("cmd", "vocab")
                        .put("stat", "panel")),
                getPaginationBtn("»", new JSONObject()
                        .put("cmd", "vocab")
                        .put("stat", "list")
                        .put("pg", nextPage)));
    }

    private InlineKeyboardButton getPaginationBtn(String text, JSONObject jsonData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(jsonData.toString()).build();
    }
}
