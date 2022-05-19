package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.model.constraint.fieldassert.DateIsNullOrNotBeforeAnotherDateAsserter;
import fi.vm.sade.koodisto.model.constraint.fieldassert.FieldAssert;
import fi.vm.sade.koodisto.util.FieldLengths;
import fi.vm.sade.koodisto.util.UserData;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.springframework.core.style.ToStringCreator;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@FieldAssert(field1 = "voimassaAlkuPvm", field2 = "voimassaLoppuPvm", asserter = DateIsNullOrNotBeforeAnotherDateAsserter.class, message = "{voimassaLoppuPvm.invalid}")
@Entity
@Table(name = KoodistoVersio.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_" + KoodistoVersio.TABLE_NAME
        + "_01", columnNames = { KoodistoVersio.VERSIO_COLUMN_NAME, KoodistoVersio.KOODISTO_COLUMN_NAME }))
@org.hibernate.annotations.Table(appliesTo = KoodistoVersio.TABLE_NAME, comment = "Koodistoversio sisältää mm. " +
        "koodiston päivityspäivämäärän, voimassaolopäivämäärät ja koodiston tilan.")
@Cacheable
@NamedEntityGraphs({@NamedEntityGraph(name = "koodistoWithRelations",
        attributeNodes = {
                @NamedAttributeNode(value = "ylakoodistos", subgraph = "ylakoodistos"),
                @NamedAttributeNode(value = "alakoodistos", subgraph = "alakoodistos"),
                @NamedAttributeNode("metadatas"),
                @NamedAttributeNode(value = "koodisto", subgraph = "koodisto"),
        },
        subgraphs = {
                @NamedSubgraph(name = "ylakoodistos", attributeNodes = @NamedAttributeNode("ylakoodistoVersio")),
                @NamedSubgraph(name = "alakoodistos", attributeNodes = @NamedAttributeNode("alakoodistoVersio")),
                @NamedSubgraph(name = "koodisto", attributeNodes = @NamedAttributeNode("koodistoRyhmas")),
        })
})
@Getter
@Setter
public class KoodistoVersio extends BaseEntity {

    private static final long serialVersionUID = 7811620155498209499L;

    public static final String TABLE_NAME = "koodistoVersio";
    public static final String VERSIO_COLUMN_NAME = "versio";
    public static final String KOODISTO_COLUMN_NAME = "koodisto_id";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = KOODISTO_COLUMN_NAME, nullable = false)
    private Koodisto koodisto;

    @NotNull
    @Min(1)
    @Column(name = VERSIO_COLUMN_NAME, nullable = false)
    private Integer versio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "paivitysPvm")
    private Date paivitysPvm;

    @Column(name = "paivittaja_oid")
    private String paivittajaOid;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "luotu", nullable = false)
    private Date luotu;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "voimassaAlkuPvm", nullable = false)
    private Date voimassaAlkuPvm;

    @Temporal(TemporalType.DATE)
    @Column(name = "voimassaLoppuPvm")
    private Date voimassaLoppuPvm;

    @NotNull
    @Column(name = "tila", nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Enumerated(EnumType.STRING)
    private Tila tila;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodistoVersio", cascade = { CascadeType.ALL })

    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<KoodistoVersioKoodiVersio> koodiVersios = new HashSet<>();

    @NotEmpty
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodistoVersio", cascade = { CascadeType.ALL })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 100)
    private Set<KoodistoMetadata> metadatas = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "alakoodistoVersio", cascade = { CascadeType.ALL })
    @BatchSize(size = 100)
    private Set<KoodistonSuhde> ylakoodistos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ylakoodistoVersio", cascade = { CascadeType.ALL })
    @BatchSize(size = 100)
    private Set<KoodistonSuhde> alakoodistos = new HashSet<>();


    @PrePersist
    protected void onCreate() {
        luotu = luotu == null ? new Date() : luotu;
        onUpdate();
    }

    @PreUpdate
    protected void onUpdate() {
        UserData.getCurrentUserOid().ifPresent(this::setPaivittajaOid);
        this.paivitysPvm = new Date();
    }


    public void addKoodiVersio(KoodistoVersioKoodiVersio koodiVersio) {
        this.koodiVersios.add(koodiVersio);
    }

    public Set<KoodistoVersioKoodiVersio> getKoodiVersios() {
        return Collections.unmodifiableSet(koodiVersios);
    }

    public void addMetadata(KoodistoMetadata metadata) {
        metadata.setKoodistoVersio(this);
        this.metadatas.add(metadata);
    }

    public void removeMetadata(KoodistoMetadata metadata) {
        metadata.setKoodistoVersio(null);
        this.metadatas.remove(metadata);
    }

    public Set<KoodistoMetadata> getMetadatas() {
        return Collections.unmodifiableSet(metadatas);
    }

    public void removeKoodiVersios(Collection<KoodistoVersioKoodiVersio> koodiVersios) {
        this.koodiVersios.removeAll(koodiVersios);
    }

    public void removeKoodiVersio(KoodistoVersioKoodiVersio koodiVersio) {
        this.koodiVersios.remove(koodiVersio);
    }

    public void removeYlaKoodistonSuhde(KoodistonSuhde ks) {
        ylakoodistos.remove(ks);
    }
    
    public void removeAlaKoodistonSuhde(KoodistonSuhde ks) {
        alakoodistos.remove(ks);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append(this.getId()).append(this.versio).toString();
    }

    @AssertTrue(message = "Validation end date must not be before start date")
    private boolean getValidateDates() {
        return voimassaAlkuPvm != null && (voimassaLoppuPvm == null || !voimassaLoppuPvm.before(voimassaAlkuPvm));
    }

    public int getKoodiCount(){
        return koodiVersios.size();
    }

}
