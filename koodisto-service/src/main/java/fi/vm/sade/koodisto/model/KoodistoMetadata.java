package fi.vm.sade.koodisto.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.util.FieldLengths;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = KoodistoMetadata.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_"
        + KoodistoMetadata.TABLE_NAME + "_01", columnNames = { KoodistoMetadata.KIELI_COLUMN_NAME,
        KoodistoMetadata.KOODISTO_VERSIO_COLUMN_NAME }))
@Comment("Sisältää koodiston metatiedot, kuten nimi, kuvaus, jne.")
@Cacheable
@Getter
@Setter
public class KoodistoMetadata extends BaseEntity {

    public static final String TABLE_NAME = "koodistoMetadata";
    public static final String KIELI_COLUMN_NAME = "kieli";
    public static final String KOODISTO_VERSIO_COLUMN_NAME = "koodistoVersio_id";

    private static final long serialVersionUID = -6880293349203597691L;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class })
    @NotNull(message = "error.validation.language")
    @Column(name = KIELI_COLUMN_NAME, nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Enumerated(EnumType.STRING)
    private Kieli kieli;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class, JsonViews.Internal.class })
    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "nimi", length = FieldLengths.DEFAULT_FIELD_LENGTH, nullable = false)
    private String nimi;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH)
    @Column(name = "kuvaus", length = FieldLengths.LONG_FIELD_LENGTH, nullable = true)
    private String kuvaus;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH)
    @Column(name = "kayttoohje", length = FieldLengths.LONG_FIELD_LENGTH)
    private String kayttoohje;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "kasite", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String kasite;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "kohdealue", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String kohdealue;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "sitovuustaso", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String sitovuustaso;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "kohdealueenOsaAlue", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String kohdealueenOsaAlue;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "toimintaYmparisto", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String toimintaymparisto;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "tarkentaaKoodistoa", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String tarkentaaKoodistoa;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "huomioitavaKoodisto", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String huomioitavaKoodisto;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "koodistonLahde", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String koodistonLahde;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = KOODISTO_VERSIO_COLUMN_NAME, nullable = false)
    private KoodistoVersio koodistoVersio;

}
