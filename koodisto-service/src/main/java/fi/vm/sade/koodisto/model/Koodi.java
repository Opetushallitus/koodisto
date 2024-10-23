package fi.vm.sade.koodisto.model;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Table(name = Koodi.TABLE_NAME)
@Comment("Koodin pääentiteetti, johon eri koodiversiot liittyvät. Sisältää koodiUrin.")
@Entity
@Cacheable
@BatchSize(size = 100)
@Getter
@Setter
public class Koodi extends BaseEntity {

    public static final String TABLE_NAME = "koodi";

    private static final long serialVersionUID = 3884954149014861779L;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "koodisto_id", nullable = false)
    private Koodisto koodisto;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @NotNull
    @Column(name="koodiUri", nullable =  false, unique = true)
    private String koodiUri;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodi", cascade = CascadeType.ALL)
    private Set<KoodiVersio> koodiVersios = new HashSet<>();

    public Set<KoodiVersio> getKoodiVersios() {
        return Collections.unmodifiableSet(koodiVersios);
    }

    public void removeKoodiVersion(KoodiVersio koodiVersio) {
        this.koodiVersios.remove(koodiVersio);
    }

}
