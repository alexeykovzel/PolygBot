package com.alexeykovzel.bot.feature.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HelloCommand extends BotCommand {

    public HelloCommand() {
        super("hello", "Experimental command");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        try {
            absSender.execute(SendMessage.builder()
                    .chatId(chat.getId().toString())
                    .text("This command does not do anything yet").build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        /*CustomTimerTask task = new CustomTimerTask("Some task", 1) {
            @Override
            public void execute() {
                SendMessage message = new SendMessage();
                message.setText("Hello!");
                message.setText(String.valueOf(Math.exp(-timeline.getCurrentRate())/0.5));
                message.setChatId(chatId);
                try {
                    absSender.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        };
        TimerExecutor.startExecutionEveryDayAt(task, 14, 41, 0);
        try {
            scheduleMessage("Some text", String.valueOf(chatId), absSender, 0, 0, 1, 0.7, ChatHome.getMemoryStabilityByChatId(chatId), 11, 0.5);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        /*SendMessage message = new SendMessage();
        StringBuilder messageText = new StringBuilder();
        try {
            List<Word> vulnerableWords = new ArrayList<>();
            for (Word word : WordHome.getWordListByChatId(chatId)) {
                String wordText = word.getWordText();
                long passedTime = new java.util.Date().getTime() - word.getTimestamp().getTime();
                double memoryStability = ChatHome.getMemoryStabilityByChatId(chatId);
                double retrievability = word.getRetrievability(memoryStability, millisToMinutes(passedTime), 11, 0.5);
                word.setRetrievability(retrievability);

                int vulnerableWordsNumber = 10;
                if (vulnerableWords.size() >= vulnerableWordsNumber) {
                    for (int i = 0; i <= vulnerableWordsNumber; i++) {
                        if (vulnerableWords.get(i).getRetrievability() < retrievability) {
                            vulnerableWords.set(i, word);
                        }
                    }
                } else {
                    vulnerableWords.add(word);
                }

                messageText.append(wordText).append(" - ").append(retrievability * 100).append("%\n\n");
            }
            messageText.append("*Vulnerable Words:*\n\n");
            for (Word word : vulnerableWords) {

                messageText.append(word.getWordText()).append("\n");
            }

            message.setText(messageText.toString());
            message.setChatId(chatId);
            message.enableMarkdown(true);
            absSender.execute(message);
        } catch (TelegramApiException | SQLException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * @param text              text of a sending message
     * @param chatId            chat_id of a sending message
     * @param absSender         telegram bot API executor
     * @param delayTime         time of delay before sending a message
     * @param passedTime        passed time after the first message in thread
     * @param sendingTime       the time of sending a message in a thread
     * @param minRetrievability minimum retrievability value of the user for checking his ability to remember given information
     * @param memoryStability   coefficient of memory stability of the user
     *                          <p>
     *                          Retrievability = exp ^ (-(time / memoryStability) ^ pow)
     */
    private void scheduleMessage(String text, String chatId, AbsSender absSender,
                                 double delayTime, double passedTime, int sendingTime,
                                 double minRetrievability, double memoryStability, double exp, double pow) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        SendMessage message = new SendMessage();
                        double nextDelayTime = memoryStability * Math.pow(-Math.log(Math.pow(minRetrievability, sendingTime)) / Math.log(exp), 1 / pow);
                        double retrievability = Math.pow(exp, -Math.pow(millisToMinutes(delayTime) / memoryStability, pow));
                        message.setText(text + "\n" + Math.round(retrievability * 100) + "%\n" + nextDelayTime);
                        message.setChatId(chatId);
                        try {
                            absSender.execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        if (sendingTime <= 10) {
                            scheduleMessage(text, chatId, absSender, minutesToMillis(nextDelayTime),
                                    passedTime + delayTime, sendingTime + 1,
                                    minRetrievability, memoryStability, exp, pow);
                        }
                    }
                },
                Math.round(delayTime)
        );
    }

    private double minutesToMillis(double seconds) {
        return seconds * 60 * 1000;
    }

    private double millisToMinutes(double millis) {
        return millis / 60 / 1000;
    }
}
