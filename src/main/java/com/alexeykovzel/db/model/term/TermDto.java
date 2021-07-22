package com.alexeykovzel.db.model.term;

import com.alexeykovzel.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TermDto {
    private String value;
    private String link;
    private List<Pair<String, String>> defs;
    private List<String> examples;
}