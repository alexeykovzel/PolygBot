package com.alexeykovzel;

import com.alexeykovzel.db.repository.CaseStudyRepository;
import com.alexeykovzel.db.repository.ChatRepository;
import com.alexeykovzel.util.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(
        locations = "classpath:application.properties")
public class ViewListCmdTest extends DefaultAbsSender {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private CaseStudyRepository caseStudyRepository;

    public ViewListCmdTest() {
        super(new DefaultBotOptions());
    }

    @Test
    void callVocabCommand() {
        String chatId = "";
        StringBuilder message = new StringBuilder().append("This is your word list! " +
                "Click the word to get its detailed info");

        /*try {
            execute(SendMessage.builder()
                    .text(message.toString())
                    .chatId(chatId)
                    .replyMarkup(buildWordAddReplyMarkup(rows))
                    .parseMode(ParseMode.MARKDOWN).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }*/
    }

    private InlineKeyboardMarkup buildWordAddReplyMarkup(List<Pair<String, String>> rows) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        int count = 1;
        for (Pair<String, String> termInfo : rows) {
            rowList.add(Collections.singletonList(
                    createInlineKeyboardButton(
                            count + ". " + termInfo.getFirst() + " - " + termInfo.getSecond() + "@del_",
                            "selectTerm?page=" + (count / 10 + 1)
                                    + "&value=" + termInfo.getFirst()
                                    + "&percent=" + termInfo.getSecond()
                    )));
            count++;
        }

        rowList.add(Arrays.asList(
                createInlineKeyboardButton("<<", "turnPage@"),
                createInlineKeyboardButton("<", "turnPage@"),
                createInlineKeyboardButton("-", "turnPage@"),
                createInlineKeyboardButton(">", "turnPage@"),
                createInlineKeyboardButton(">>", "turnPage@")));

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData).build();
    }

    @Override
    public String getBotToken() {
        return "";
    }
}
