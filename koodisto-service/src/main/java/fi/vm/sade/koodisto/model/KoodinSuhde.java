/**
 *
 */
package fi.vm.sade.koodisto.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.koodisto.common.util.FieldLengths;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * @author tommiha
 */
@Entity
@Table(name = KoodinSuhde.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_" + KoodinSuhde.TABLE_NAME + "_01", columnNames = {
        KoodinSuhde.ALAKOODI_COLUMN_NAME, KoodinSuhde.YLAKOODI_COLUMN_NAME, KoodinSuhde.SUHTEEN_TYYPPI_COLUMN_NAME }))
@org.hibernate.annotations.Table(appliesTo = KoodinSuhde.TABLE_NAME, comment = "Määrittää kahden koodin välinen suhteen. Suhteen tyyppi voi olla SISALTYY tai RINNASTEINEN.")
@Cacheable
public class KoodinSuhde extends BaseEntity {

    private static final long serialVersionUID = -8875747407128912635L;

    public static final String TABLE_NAME = "koodinSuhde";

    public static final String YLAKOODI_COLUMN_NAME = "ylakoodiVersio_id";
    public static final String ALAKOODI_COLUMN_NAME = "alakoodiVersio_id";
    public static final String SUHTEEN_TYYPPI_COLUMN_NAME = "suhteenTyyppi";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = YLAKOODI_COLUMN_NAME, nullable = false)
    private KoodiVersio ylakoodiVersio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ALAKOODI_COLUMN_NAME, nullable = false)
    private KoodiVersio alakoodiVersio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = SUHTEEN_TYYPPI_COLUMN_NAME, nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private SuhteenTyyppi suhteenTyyppi;

    public KoodiVersio getYlakoodiVersio() {
        return ylakoodiVersio;
    }

    public void setYlakoodiVersio(KoodiVersio ylakoodiVersio) {
        this.ylakoodiVersio = ylakoodiVersio;
    }

    public KoodiVersio getAlakoodiVersio() {
        return alakoodiVersio;
    }

    public void setAlakoodiVersio(KoodiVersio alakoodiVersio) {
        this.alakoodiVersio = alakoodiVersio;
    }

    public SuhteenTyyppi getSuhteenTyyppi() {
        return suhteenTyyppi;
    }

    public void setSuhteenTyyppi(SuhteenTyyppi suhteenTyyppi) {
        this.suhteenTyyppi = suhteenTyyppi;
    }

}
