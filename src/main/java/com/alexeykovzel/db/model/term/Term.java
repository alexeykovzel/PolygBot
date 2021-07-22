package com.alexeykovzel.db.model.term;

import com.alexeykovzel.db.model.casestudy.CaseStudy;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "term")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NaturalId
    @Column(name = "value", nullable = false, unique = true)
    private String value;

    @ElementCollection
    @CollectionTable(
            name = "term_def",
            joinColumns = @JoinColumn(name = "term_id")
    )
    private Set<TermDef> defs = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "term_example",
            joinColumns = @JoinColumn(name = "term_id")
    )
    @Column(name = "value")
    private Set<String> examples = new HashSet<>();

    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CaseStudy> caseStudies = new HashSet<>();

    public Term(String value, Set<TermDef> defs, Set<String> examples) {
        this.value = value;
        this.defs = defs;
        this.examples = examples;
    }

    @Override
    public String toString() {
        return String.format("Term{id=%s, value='%s'}", id, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return value.equals(term.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

