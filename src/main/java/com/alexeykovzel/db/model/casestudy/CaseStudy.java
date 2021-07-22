package com.alexeykovzel.db.model.casestudy;

import com.alexeykovzel.db.model.Chat;
import com.alexeykovzel.db.model.term.Term;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "case_study")
@IdClass(CaseStudyId.class)

@Getter
@Setter
@NoArgsConstructor
public class CaseStudy {

    @Id
    @Column(name = "term_id")
    @Setter(AccessLevel.PROTECTED)
    private Long termId;

    @Id
    @Column(name = "chat_id")
    @Setter(AccessLevel.PROTECTED)
    private String chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("termId")
    @JoinColumn(name = "term_id")
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "retrievability")
    private Double retrievability;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    public CaseStudy(Long termId, String chatId, Double retrievability, Timestamp timestamp) {
        this.termId = termId;
        this.chatId = chatId;
        this.retrievability = retrievability;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("CaseStudy{termId=%s, chatId=%s, retrievability=%s, timestamp=%s}",
                termId, chatId, retrievability, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseStudy caseStudy = (CaseStudy) o;
        return termId.equals(caseStudy.termId) && chatId.equals(caseStudy.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termId, chatId);
    }
}
