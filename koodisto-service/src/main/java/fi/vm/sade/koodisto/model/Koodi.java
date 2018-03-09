/**
 *
 */
package fi.vm.sade.koodisto.model;

import fi.vm.sade.generic.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author tommiha
 */
@Table(name = Koodi.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Koodi.TABLE_NAME, comment = "Koodin pääentiteetti, johon eri koodiversiot liittyvät.<br>" +
        "Sisältää koodiUrin.")
@Entity
@Cacheable
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
    private Set<KoodiVersio> koodiVersios = new HashSet<KoodiVersio>();

    public Koodisto getKoodisto() {
        return koodisto;
    }

    public void setKoodisto(Koodisto koodisto) {
        this.koodisto = koodisto;
    }

    public Set<KoodiVersio> getKoodiVersios() {
        return Collections.unmodifiableSet(koodiVersios);
    }

    public void removeKoodiVersion(KoodiVersio koodiVersio) {
        this.koodiVersios.remove(koodiVersio);
    }

    public String getKoodiUri() {
        return koodiUri;
    }

    public void setKoodiUri(String koodiUri) {
        this.koodiUri = koodiUri;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    
    
}
