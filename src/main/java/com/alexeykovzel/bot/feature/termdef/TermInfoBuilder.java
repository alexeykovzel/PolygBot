package com.alexeykovzel.bot.feature.termdef;

import com.alexeykovzel.db.model.term.TermDto;
import com.alexeykovzel.util.Pair;

import java.util.List;

public class TermInfoBuilder {
    public static StringBuilder buildTermInfoMessage(TermDto termDto) {
        StringBuilder response = new StringBuilder();

        // Add 'definitions' section
        response.append(getDefSection(termDto.getDefs(), 2));

        // Add 'examples' section
        response.append(getExamplesSection(termDto.getExamples(), 4));

        // Add 'more' section
        response.append("[More](").append(termDto.getLink()).append(") about '*").append(termDto.getValue()).append("*'");

        return response;
    }

    private static StringBuilder getDefSection(List<Pair<String, String>> defList, int maxValue) {
        StringBuilder defSection = new StringBuilder();
        if (!defList.isEmpty()) {
            for (int i = 0; i < defList.size(); i++) {
                if (i == maxValue) {
                    break;
                }
                Pair<String, String> def = defList.get(i);
                String pos = def.getFirst();
                String value = def.getSecond();
                if (pos != null) {
                    defSection.append(String.format("*[%s]* %s\n\n", pos, value));
                } else {
                    defSection.append(value).append("\n\n");
                }
            }
        }
        return defSection;
    }

    private static StringBuilder getExamplesSection(List<String> termExamples, int maxValue) {
        StringBuilder examplesSection = new StringBuilder();

        if (!termExamples.isEmpty()) {
            examplesSection.append("*EXAMPLES*\n\n");
            StringBuilder examples = new StringBuilder();
            for (int i = 0; i < termExamples.size(); i++) {
                if (i == maxValue) {
                    break;
                }
                examples.append("- ").append(termExamples.get(i)).append("\n");
            }
            examplesSection.append(examples).append("\n");
        }
        return examplesSection;
    }

    public static String buildHelpMsg() {
        return "I am good at teaching new words and new meanings. You can start by writing /tutorial in the chat.\n\n" +
                "Also, you can control me by using these commands:\n\n" +
                "/help - shows all commands\n" +
                "/tutorial - learn to use this bot\n\n" +
                "*Bot Usage*\n" +
                "/status - view user status\n" +
                "/vocab - open your vocabulary\n" +
                "/newquiz - take quick quiz\n" +
                "/newterm - suggest new term\n\n" +
                "*Bot Settings*\n" +
                "/settings - change bot settings\n" +
                "/language - change bot language\n" +
                "/reset - reset user progress";
    }
}
