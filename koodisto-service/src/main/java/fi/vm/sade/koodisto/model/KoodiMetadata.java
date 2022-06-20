package fi.vm.sade.koodisto.model;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.util.FieldLengths;
import fi.vm.sade.koodisto.views.JsonViews;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
@Getter
@Setter
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

}
