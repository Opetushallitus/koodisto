package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.common.util.FieldLengths;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = KoodistoMetadata.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_"
        + KoodistoMetadata.TABLE_NAME + "_01", columnNames = { KoodistoMetadata.KIELI_COLUMN_NAME,
        KoodistoMetadata.KOODISTO_VERSIO_COLUMN_NAME }))
@org.hibernate.annotations.Table(appliesTo = KoodistoMetadata.TABLE_NAME, comment = "Sisältää koodiston metatiedot, kuten nimi, kuvaus, jne.")
@Cacheable
public class KoodistoMetadata extends BaseEntity {

    public static final String TABLE_NAME = "koodistometadata";
    public static final String KIELI_COLUMN_NAME = "kieli";
    public static final String KOODISTO_VERSIO_COLUMN_NAME = "koodistoversio_id";

    private static final long serialVersionUID = -6880293349203597691L;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
    @NotNull
    @Column(name = KIELI_COLUMN_NAME, nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Enumerated(EnumType.STRING)
    private Kieli kieli;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class })
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
    @Column(name = "kohdealueenosaalue", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String kohdealueenOsaAlue;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "toimintaymparisto", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String toimintaymparisto;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "tarkentaakoodistoa", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String tarkentaaKoodistoa;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "huomioitavakoodisto", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String huomioitavaKoodisto;

    @JsonView({ JsonViews.Extended.class, JsonViews.Basic.class })
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "koodistonlahde", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String koodistonLahde;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = KOODISTO_VERSIO_COLUMN_NAME, nullable = false)
    private KoodistoVersio koodistoVersio;

    public Kieli getKieli() {
        return kieli;
    }

    public void setKieli(Kieli kieli) {
        this.kieli = kieli;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getKayttoohje() {
        return kayttoohje;
    }

    public void setKayttoohje(String kayttoohje) {
        this.kayttoohje = kayttoohje;
    }

    public String getKasite() {
        return kasite;
    }

    public void setKasite(String kasite) {
        this.kasite = kasite;
    }

    public String getKohdealue() {
        return kohdealue;
    }

    public void setKohdealue(String kohdealue) {
        this.kohdealue = kohdealue;
    }

    public String getKohdealueenOsaAlue() {
        return kohdealueenOsaAlue;
    }

    public void setKohdealueenOsaAlue(String kohdealueenOsaAlue) {
        this.kohdealueenOsaAlue = kohdealueenOsaAlue;
    }

    public String getToimintaymparisto() {
        return toimintaymparisto;
    }

    public void setToimintaymparisto(String toimintaymparisto) {
        this.toimintaymparisto = toimintaymparisto;
    }

    public KoodistoVersio getKoodistoVersio() {
        return koodistoVersio;
    }

    public void setKoodistoVersio(KoodistoVersio koodistoVersio) {
        this.koodistoVersio = koodistoVersio;
    }

    public String getSitovuustaso() {
        return sitovuustaso;
    }

    public void setSitovuustaso(String sitovuustaso) {
        this.sitovuustaso = sitovuustaso;
    }

    public String getTarkentaaKoodistoa() {
        return tarkentaaKoodistoa;
    }

    public void setTarkentaaKoodistoa(String tarkentaaKoodistoa) {
        this.tarkentaaKoodistoa = tarkentaaKoodistoa;
    }

    public String getHuomioitavaKoodisto() {
        return huomioitavaKoodisto;
    }

    public void setHuomioitavaKoodisto(String huomioitavaKoodisto) {
        this.huomioitavaKoodisto = huomioitavaKoodisto;
    }

    public String getKoodistonLahde() {
        return koodistonLahde;
    }

    public void setKoodistonLahde(String koodistonLahde) {
        this.koodistonLahde = koodistonLahde;
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
