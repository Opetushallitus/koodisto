package fi.vm.sade.koodisto.model;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.util.FieldLengths;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = KoodiMetadata.TABLE_NAME, uniqueConstraints = {@UniqueConstraint(name = "UK_" + KoodiMetadata.TABLE_NAME
        + "_01", columnNames = {KoodiMetadata.KIELI_COLUMN_NAME, KoodiMetadata.KOODIVERSIO_COLUMN_NAME})})
@org.hibernate.annotations.Table(appliesTo = KoodiMetadata.TABLE_NAME, comment = "KoodiMetadata sisältää mm. koodin nimen, " +
        "lyhytnimen ja kuvauksen yhdellä kielellä.")
@Cacheable
@BatchSize(size = 20)
@NamedEntityGraphs({@NamedEntityGraph(name = "koodiMetadataWithKoodiVersio",
        attributeNodes = {
                @NamedAttributeNode(value = "koodiVersio", subgraph = "koodiVersio")
        },
        subgraphs = {
                @NamedSubgraph(name = "koodiVersio", attributeNodes = {
                        @NamedAttributeNode(value = "metadatas"),
                        @NamedAttributeNode(value = "koodistoVersios"),
                        @NamedAttributeNode(value = "koodi"),
                }),
        }),
})
@NamedQuery(
        name = "KoodiMetadata.initializeByKoodiVersioIds",
        query = "SELECT km FROM KoodiMetadata km WHERE km.koodiVersio.id IN :versioIds",
        hints = {
                @QueryHint(name = KoodiMetadata.FETCH_GRAPH_HINT, value = "koodiMetadataWithKoodiVersio")
        })
public class KoodiMetadata extends BaseEntity {

    public static final String TABLE_NAME = "koodiMetadata";
    public static final String KIELI_COLUMN_NAME = "kieli";
    public static final String KOODIVERSIO_COLUMN_NAME = "koodiVersio_id";
    public static final String FETCH_GRAPH_HINT = "javax.persistence.fetchgraph";

    private static final long serialVersionUID = -6996174469669634802L;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH * 2)
    @Column(name = "nimi", length = FieldLengths.DEFAULT_FIELD_LENGTH * 2, nullable = false)
    private String nimi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH)
    @Column(name = "kuvaus", length = FieldLengths.LONG_FIELD_LENGTH, nullable = true)
    private String kuvaus;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "lyhytNimi", length = FieldLengths.DEFAULT_FIELD_LENGTH, nullable = true)
    private String lyhytNimi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH)
    @Column(name = "kayttoohje", length = FieldLengths.LONG_FIELD_LENGTH)
    private String kayttoohje;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "kasite", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String kasite;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH)
    @Column(name = "sisaltaaMerkityksen", length = FieldLengths.LONG_FIELD_LENGTH)
    private String sisaltaaMerkityksen;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH)
    @Column(name = "eiSisallaMerkitysta", length = FieldLengths.LONG_FIELD_LENGTH)
    private String eiSisallaMerkitysta;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "huomioitavaKoodi", length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private String huomioitavaKoodi;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class})
    @Size(min = 0, max = FieldLengths.LONG_FIELD_LENGTH)
    @Column(name = "sisaltaaKoodiston", length = FieldLengths.LONG_FIELD_LENGTH)
    private String sisaltaaKoodiston;

    @JsonView({JsonViews.Extended.class, JsonViews.Basic.class, JsonViews.Simple.class})
    @NotNull
    @Column(name = KIELI_COLUMN_NAME, nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Enumerated(EnumType.STRING)
    private Kieli kieli;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = KOODIVERSIO_COLUMN_NAME, nullable = false)
    private KoodiVersio koodiVersio;

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

    public String getLyhytNimi() {
        return lyhytNimi;
    }

    public void setLyhytNimi(String lyhytNimi) {
        this.lyhytNimi = lyhytNimi;
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

    public String getSisaltaaMerkityksen() {
        return sisaltaaMerkityksen;
    }

    public void setSisaltaaMerkityksen(String sisaltaaMerkityksen) {
        this.sisaltaaMerkityksen = sisaltaaMerkityksen;
    }

    public String getEiSisallaMerkitysta() {
        return eiSisallaMerkitysta;
    }

    public void setEiSisallaMerkitysta(String eiSisallaMerkitysta) {
        this.eiSisallaMerkitysta = eiSisallaMerkitysta;
    }

    public String getHuomioitavaKoodi() {
        return huomioitavaKoodi;
    }

    public void setHuomioitavaKoodi(String huomioitavaKoodi) {
        this.huomioitavaKoodi = huomioitavaKoodi;
    }

    public String getSisaltaaKoodiston() {
        return sisaltaaKoodiston;
    }

    public void setSisaltaaKoodiston(String sisaltaaKoodiston) {
        this.sisaltaaKoodiston = sisaltaaKoodiston;
    }

    public Kieli getKieli() {
        return kieli;
    }

    public void setKieli(Kieli kieli) {
        this.kieli = kieli;
    }

    public KoodiVersio getKoodiVersio() {
        return koodiVersio;
    }

    public void setKoodiVersio(KoodiVersio koodiVersio) {
        this.koodiVersio = koodiVersio;
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
