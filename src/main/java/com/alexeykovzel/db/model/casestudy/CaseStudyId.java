package com.alexeykovzel.db.model.casestudy;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable

@Getter
@Setter
@NoArgsConstructor
public class CaseStudyId implements Serializable {
    private Long termId;
    private String chatId;

    public CaseStudyId(Long termId, String chatId) {
        this.termId = termId;
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return String.format("CaseStudyId{termId=%s, chatId=%s}", termId, chatId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseStudyId that = (CaseStudyId) o;
        return termId.equals(that.termId) && chatId.equals(that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termId, chatId);
    }
}
