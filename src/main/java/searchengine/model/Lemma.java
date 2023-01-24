package searchengine.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "lemmas")
public class Lemma{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(columnDefinition = "VARCHAR(255), UNIQUE KEY uk_site_lemma(site_id, lemma(255))")
    private String lemma;

    @Column(nullable = false)
    private int frequency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lemma lemma1 = (Lemma) o;
        return site.getId() == lemma1.site.getId()
                && lemma.equals(lemma1.lemma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(site.getId(), lemma);
    }
}
