package com.alexeykovzel.service;

import com.alexeykovzel.db.model.term.TermDto;
import com.alexeykovzel.util.Pair;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollinsDictionaryAPI extends WebDictionary {

    @Override
    public TermDto getTerm(String termValue) throws IOException {
        String searchQuery = "https://www.collinsdictionary.com/dictionary/english/" + toSearchForm(termValue);
        Element body = getPageBody(searchQuery);

        String origValue;

        try {
            origValue = body.getElementsByClass("orth").first().text();
        } catch (NullPointerException e) {
            return null;
        }

        List<Pair<String, String>> defs = getDefs(body, 3);
        List<String> examples = getExamples(body, 3);

        return new TermDto(origValue, searchQuery, defs, examples);
    }

    @Override
    public String getName() {
        return "Collins Dictionary";
    }

    private List<Pair<String, String>> getDefs(Element body, int maxValue) {
        List<Pair<String, String>> defs = new ArrayList<>();
        Elements defElements = body.getElementsByClass("hom");

        if (!defElements.isEmpty()) {
            for (int i = 0; i < defElements.size(); i++) {
                if (i >= maxValue) {
                    break;
                }
                Element defSection = defElements.get(i);
                Element defElement = defSection.getElementsByClass("def").first();
                if (defElement != null) {
                    Element posElement = defSection.getElementsByClass("pos").first();
                    String pos = null;
                    if (posElement != null) {
                        pos = escapeMarkdown(posElement.text().toUpperCase());
                    }
                    defs.add(new Pair<>(pos, defElement.text()));
                }
            }
        }
        return defs;
    }

    private List<String> getExamples(Element body, int maxValue) {
        List<String> examples = new ArrayList<>();
        Elements exampleElements = body.getElementsByClass("quote");

        if (!exampleElements.isEmpty()) {
            for (int i = 0; i < exampleElements.size(); i++) {
                if (i >= maxValue) {
                    break;
                }
                examples.add(escapeMarkdown(exampleElements.get(i).text()));
            }
        }
        return examples;
    }
}
