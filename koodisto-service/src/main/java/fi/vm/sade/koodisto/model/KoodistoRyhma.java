/**
 *
 */
package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.util.FieldLengths;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Table(name = KoodistoRyhma.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_" + KoodistoRyhma.TABLE_NAME + "_01", columnNames = {KoodistoRyhma.KOODISTO_RYHMA_URI_COLUMN_NAME}))
@Comment("Koodistoryhmä sisältää aina tietyn tyyppisiä koodistoja, esim. alueet. Koodisto voi kuulua useaan koodistoryhmään.")
@Entity
@Cacheable
@Getter
@Setter
public class KoodistoRyhma extends BaseEntity {

    private static final long serialVersionUID = 4137284135569188700L;

    public static final String TABLE_NAME = "koodistoRyhma";
    public static final String KOODISTO_RYHMA_URI_COLUMN_NAME = "koodistoRyhmaUri";

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "koodistoRyhma_koodisto", joinColumns = @JoinColumn(name = "koodistoRyhma_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME), inverseJoinColumns = @JoinColumn(name = "koodisto_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    private Set<Koodisto> koodistos = new HashSet<>();

    @NotEmpty
    @OneToMany(mappedBy = "koodistoRyhma", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<KoodistoRyhmaMetadata> koodistoRyhmaMetadatas = new HashSet<>();

    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = KoodistoRyhma.KOODISTO_RYHMA_URI_COLUMN_NAME, length = FieldLengths.DEFAULT_FIELD_LENGTH, nullable = false)
    private String koodistoRyhmaUri;

    public Set<Koodisto> getKoodistos() {
        return Collections.unmodifiableSet(koodistos);
    }

    public Set<KoodistoRyhmaMetadata> getKoodistoJoukkoMetadatas() {
        return Collections.unmodifiableSet(koodistoRyhmaMetadatas);
    }

    public void addKoodistoRyhmaMetadata(KoodistoRyhmaMetadata metadata) {
        this.koodistoRyhmaMetadatas.add(metadata);
        metadata.setKoodistoRyhma(this);
    }

    public void removeKoodistoRyhmaMetadata(KoodistoRyhmaMetadata metadata) {
        this.koodistoRyhmaMetadatas.remove(metadata);
    }

    public void addKoodisto(Koodisto koodisto) {
        this.koodistos.add(koodisto);
    }

    public void removeKoodisto(Koodisto koodisto) {
        this.koodistos.remove(koodisto);
    }

    public String getNimi(Kieli kieli) {
        return getKoodistoRyhmaMetadatas().stream().filter(a -> a.getKieli().equals(kieli)).findFirst().orElseGet(KoodistoRyhmaMetadata::new).getNimi();
    }
}
