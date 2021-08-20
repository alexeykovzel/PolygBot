package com.alexeykovzel.bot.feature.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StatusCommand extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "status";
    private static final String COMMAND_DESCRIPTION = "shows user status";

    public StatusCommand(){
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

    }
}
