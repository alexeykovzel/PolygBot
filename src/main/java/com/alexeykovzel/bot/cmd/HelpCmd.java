package com.alexeykovzel.bot.cmd;

import com.alexeykovzel.bot.MessageBuilder;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * This command helps the user to find the command they need
 *
 * @author Timo Schulz (Mit0x2)
 */
public class HelpCmd extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "help";
    private static final String COMMAND_DESCRIPTION = "shows all commands. Use /help [command] for more info";

    public HelpCmd() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String text = MessageBuilder.buildHelpMessage();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chat.getId().toString())
                .parseMode(ParseMode.MARKDOWN)
                .text(text).build();

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}