package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.util.FieldLengths;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Table(name = Koodisto.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_" + Koodisto.TABLE_NAME + "_01", columnNames = { Koodisto.KOODISTO_URI_COLUMN_NAME }))
@org.hibernate.annotations.Table(appliesTo = Koodisto.TABLE_NAME, comment = "Koodiston pääentiteetti, johon eri koodistoversiot "
        + "liittyvät. Sisältää koodistoUrin.")
@Entity
@Cacheable
@BatchSize(size = 100)
@Getter
@Setter
public class Koodisto extends BaseEntity {

    private static final long serialVersionUID = -6903116815069994046L;

    public static final String TABLE_NAME = "koodisto";
    public static final String KOODISTO_URI_COLUMN_NAME = "koodistoUri";

    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = KOODISTO_URI_COLUMN_NAME, nullable = false, unique = true)
    private String koodistoUri;

    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "omistaja", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String omistaja;

    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "organisaatioOid", length = FieldLengths.DEFAULT_FIELD_LENGTH, nullable = false)
    private String organisaatioOid;

    @Column(name = "lukittu")
    private Boolean lukittu;

    @ManyToMany(mappedBy = "koodistos", fetch = FetchType.LAZY)
    private Set<KoodistoRyhma> koodistoRyhmas = new HashSet<>();

    @OneToMany(mappedBy = "koodisto", fetch = FetchType.LAZY)
    private Set<KoodistoVersio> koodistoVersios = new HashSet<>();

    @OneToMany(mappedBy = "koodisto", fetch = FetchType.LAZY)
    private Set<Koodi> koodis = new HashSet<>();

    public Set<KoodistoRyhma> getKoodistoRyhmas() {
        return Collections.unmodifiableSet(koodistoRyhmas);
    }

    public void addKoodistoRyhma(KoodistoRyhma joukko) {
        this.koodistoRyhmas.add(joukko);
    }

    public void removeKoodistoRyhma(KoodistoRyhma joukko) {
        this.koodistoRyhmas.remove(joukko);
    }

    public Set<KoodistoVersio> getKoodistoVersios() {
        return Collections.unmodifiableSet(koodistoVersios);
    }

    public int getLatestKoodistoVersioNumber() {
        int latestVersio = 1;
        for (KoodistoVersio kv : koodistoVersios) {
            latestVersio = kv.getVersio() > latestVersio ? kv.getVersio() : latestVersio;
        }
        return latestVersio;
    }

    public void addKoodistoVersion(KoodistoVersio koodistoVersio) {
        this.koodistoVersios.add(koodistoVersio);
    }

    public void removeKoodistoVersion(KoodistoVersio koodistoVersio) {
        this.koodistoVersios.remove(koodistoVersio);
    }

    public void addKoodi(Koodi koodi) {
        this.koodis.add(koodi);
    }

    public Set<Koodi> getKoodis() {
        return Collections.unmodifiableSet(koodis);
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
