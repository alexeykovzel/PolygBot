package com.alexeykovzel.bot.feature.command;

import com.alexeykovzel.db.model.Chat;
import com.alexeykovzel.db.model.User;
import com.alexeykovzel.db.repository.ChatRepository;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;
import java.util.List;

/**
 * This command starts the bot
 *
 * @author alexeykovzel
 */
public class StartCommand extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "start";
    private static final String COMMAND_DESCRIPTION = "this command starts the bot";
    private final ChatRepository chatRepository;
    private final HelpCommand helpCommand;


    public StartCommand(HelpCommand helpCommand, ChatRepository chatRepository) {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
        this.helpCommand = helpCommand;
        this.chatRepository = chatRepository;
    }

    @Override
    public void execute(AbsSender absSender,
                        org.telegram.telegrambots.meta.api.objects.User user,
                        org.telegram.telegrambots.meta.api.objects.Chat chat,
                        String[] arguments) {
        String chatId = chat.getId().toString();

        if (!chatRepository.existsById(chatId)) {
            chatRepository.save(new Chat(chatId,
                    new User(user.getFirstName(), user.getLastName(), user.getUserName(), null)));
        }

        helpCommand.execute(absSender, user, chat, new String[]{});
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup() { // is not used
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = Arrays.asList(
                Arrays.asList(
                        InlineKeyboardButton.builder().text("Excellent").callbackData("data").build(),
                        InlineKeyboardButton.builder().text("Good").callbackData("data").build()
                ),
                Arrays.asList(
                        InlineKeyboardButton.builder().text("Normal").callbackData("data").build(),
                        InlineKeyboardButton.builder().text("Bad").callbackData("data").build()
                )
        );
        markup.setKeyboard(rowList);
        return markup;
    }
}
