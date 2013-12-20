/**
 *
 */
package fi.vm.sade.koodisto.model;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.koodisto.common.util.FieldLengths;
import org.codehaus.jackson.map.annotate.JsonView;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author kkammone
 */
@Table(name = KoodistoRyhmaMetadata.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_"
        + KoodistoRyhmaMetadata.TABLE_NAME + "_01", columnNames = {KoodistoRyhmaMetadata.KIELI_COLUMN_NAME,
        KoodistoRyhmaMetadata.KOODISTO_RYHMA_COLUMN_NAME}))
@org.hibernate.annotations.Table(appliesTo = KoodistoRyhmaMetadata.TABLE_NAME, comment = "Sisältää koodistoryhmän metatiedot, kuten nimi ja kieli.")
@Entity
@Cacheable
public class KoodistoRyhmaMetadata extends BaseEntity {

    private static final long serialVersionUID = 287785307475416064L;

    public static final String TABLE_NAME = "koodistoRyhmaMetadata";
    public static final String KIELI_COLUMN_NAME = "kieli";
    public static final String KOODISTO_RYHMA_COLUMN_NAME = "koodistoRyhma_id";

    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "nimi", length = FieldLengths.DEFAULT_FIELD_LENGTH, nullable = false)
    @JsonView(JsonViews.Basic.class)
    private String nimi;

    @NotNull
    @Column(name = KIELI_COLUMN_NAME, nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Enumerated(EnumType.STRING)
    @JsonView(JsonViews.Basic.class)
    private Kieli kieli;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = KOODISTO_RYHMA_COLUMN_NAME, nullable = false)
    private KoodistoRyhma koodistoRyhma;

    public KoodistoRyhma getKoodistoRyhma() {
        return koodistoRyhma;
    }

    public void setKoodistoRyhma(KoodistoRyhma koodistoJoukko) {
        this.koodistoRyhma = koodistoJoukko;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Kieli getKieli() {
        return kieli;
    }

    public void setKieli(Kieli kieli) {
        this.kieli = kieli;
    }
}
