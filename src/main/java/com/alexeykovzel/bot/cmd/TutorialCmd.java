package com.alexeykovzel.bot.cmd;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class TutorialCmd extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "tutorial";
    private static final String COMMAND_DESCRIPTION = "learn to use this bot";

    public TutorialCmd(String commandIdentifier, String description) {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

    }
}
