package com.alexeykovzel.db.model.term;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TermDef {

    @Column(name = "pos")
    private String pos;

    @Column(name = "value", columnDefinition = "VARCHAR(1023) NOT NULL")
    private String value;

    @Override
    public String toString() {
        return String.format("TermDef{pos='%s', value='%s'}", pos, value);
    }
}
