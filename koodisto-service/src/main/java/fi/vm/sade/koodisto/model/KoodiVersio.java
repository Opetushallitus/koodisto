package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.service.business.exception.KoodistoNotFoundException;
import fi.vm.sade.koodisto.util.FieldLengths;
import fi.vm.sade.koodisto.util.UserData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(name = KoodiVersio.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_" + KoodiVersio.TABLE_NAME
        + "_01", columnNames = {KoodiVersio.VERSIO_COLUMN_NAME, KoodiVersio.KOODI_COLUMN_NAME}))
@org.hibernate.annotations.Table(appliesTo = KoodiVersio.TABLE_NAME, comment = "Koodiversio sisältää mm. koodiarvon, " +
        "voimassaolopäivämäärät ja koodin tilan.")
@Cacheable
@BatchSize(size = 100)
@Getter
@Setter
public class KoodiVersio extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "koodiVersio";
    public static final String VERSIO_COLUMN_NAME = "versio";
    public static final String KOODI_COLUMN_NAME = "koodi_id";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "koodi_id", nullable = false)
    private Koodi koodi;

    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "koodiarvo", length = FieldLengths.DEFAULT_FIELD_LENGTH, nullable = false)
    private String koodiarvo;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "tila", nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    private Tila tila;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodiVersio", cascade = {CascadeType.ALL})
    private Set<KoodistoVersioKoodiVersio> koodistoVersios = new HashSet<>();

    @NotEmpty
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodiVersio", cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<KoodiMetadata> metadatas = new HashSet<>();

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "alakoodiVersio", cascade = {CascadeType.ALL})
    private Set<KoodinSuhde> ylakoodis = new HashSet<>();

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ylakoodiVersio", cascade = {CascadeType.ALL})
    private Set<KoodinSuhde> alakoodis = new HashSet<>();

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

    public Set<KoodistoVersioKoodiVersio> getKoodistoVersios() {
        return Collections.unmodifiableSet(koodistoVersios);
    }

    public Set<KoodiMetadata> getMetadatas() {
        return Collections.unmodifiableSet(metadatas);
    }

    public void addKoodistoVersio(KoodistoVersioKoodiVersio koodistoVersio) {
        koodistoVersios.add(koodistoVersio);
    }

    public void addMetadata(KoodiMetadata metadata) {
        metadata.setKoodiVersio(this);
        metadatas.add(metadata);
    }

    public void removeMetadata(KoodiMetadata metadata) {
        metadata.setKoodiVersio(null);
        metadatas.remove(metadata);
    }

    public void addYlakoodi(KoodinSuhde ylakoodi) {
        this.ylakoodis.add(ylakoodi);
    }

    public void addAlakoodi(KoodinSuhde alakoodi) {
        this.alakoodis.add(alakoodi);
    }

    public void removeKoodistoVersio(KoodistoVersioKoodiVersio koodistoVersio) {
        this.koodistoVersios.remove(koodistoVersio);
    }

    public Map<String, String> getKoodistoNimi() {
        return this.getKoodi().getKoodisto().getKoodistoVersios().stream()
                .reduce((a, b) -> a.getVersio() > b.getVersio() ? a : b)
                .orElseThrow(KoodistoNotFoundException::new)
                .getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), KoodistoMetadata::getNimi));
    }

    public Map<String, String> getNimi() {
        return this.getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), KoodiMetadata::getNimi));
    }

    public Map<String, String> getKuvaus() {
        return this.getMetadatas().stream()
                .collect(Collectors.toMap(metadata -> metadata.getKieli().name().toLowerCase(), koodiMetadata -> Optional.ofNullable(koodiMetadata.getKuvaus()).orElse("")));
    }

    public boolean isLocked() {
        return tila == Tila.HYVAKSYTTY;
    }

    @AssertTrue(message = "error.validation.enddate")
    public boolean isStartDateBeforeEndDate() {
        return Optional.ofNullable(voimassaLoppuPvm).map(date -> date.after(voimassaAlkuPvm)).orElse(true);
    }
}
