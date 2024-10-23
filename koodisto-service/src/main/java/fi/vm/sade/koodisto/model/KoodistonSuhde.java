package fi.vm.sade.koodisto.model;

import org.hibernate.annotations.Comment;

import fi.vm.sade.koodisto.util.FieldLengths;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = KoodistonSuhde.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_" + KoodistonSuhde.TABLE_NAME + "_01", columnNames = {
        KoodistonSuhde.ALAKOODISTO_COLUMN_NAME, KoodistonSuhde.YLAKOODISTO_COLUMN_NAME, KoodistonSuhde.SUHTEEN_TYYPPI_COLUMN_NAME, KoodistonSuhde.VERSIO_COLUMN_NAME }))
@Comment("Määrittää kahden koodiston välisen suhteen. Suhteen tyyppi voi olla SISALTYY tai RINNASTEINEN.")
@Cacheable
@Getter
@Setter
public class KoodistonSuhde extends BaseEntity {

    private static final long serialVersionUID = -8875747407128912635L;

    public static final String TABLE_NAME = "koodistonSuhde";

    public static final String YLAKOODISTO_COLUMN_NAME = "ylakoodistoVersio_id";
    public static final String ALAKOODISTO_COLUMN_NAME = "alakoodistoVersio_id";
    public static final String SUHTEEN_TYYPPI_COLUMN_NAME = "suhteenTyyppi";
    public static final String VERSIO_COLUMN_NAME = "versio";

    @NotNull
    @Min(1)
    @Column(name = VERSIO_COLUMN_NAME, nullable = false)
    private Integer versio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = YLAKOODISTO_COLUMN_NAME, nullable = false)
    private KoodistoVersio ylakoodistoVersio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ALAKOODISTO_COLUMN_NAME, nullable = false)
    private KoodistoVersio alakoodistoVersio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = SUHTEEN_TYYPPI_COLUMN_NAME, nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private SuhteenTyyppi suhteenTyyppi;

    @NotNull
    @Column(name = "ylakoodistostapassiivinen", nullable = false)
    private boolean ylaKoodistoPassive = false;

    @NotNull
    @Column(name = "alakoodistostapassiivinen", nullable = false)
    private boolean alaKoodistoPassive = false;

    public boolean isPassive() {
        return ylaKoodistoPassive || alaKoodistoPassive;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append(": ");
        sb.append(KoodistonSuhde.YLAKOODISTO_COLUMN_NAME).append('=').append(ylakoodistoVersio.getId()).append(',');
        sb.append(KoodistonSuhde.ALAKOODISTO_COLUMN_NAME).append('=').append(alakoodistoVersio.getId()).append(',');
        sb.append(KoodistonSuhde.SUHTEEN_TYYPPI_COLUMN_NAME).append('=').append(suhteenTyyppi);
        return sb.toString();
    }

}
