package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.model.constraint.fieldassert.DateIsNullOrNotBeforeAnotherDateAsserter;
import fi.vm.sade.koodisto.model.constraint.fieldassert.FieldAssert;
import fi.vm.sade.koodisto.util.FieldLengths;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static fi.vm.sade.koodisto.util.userData.getCurrentUserOid;

@FieldAssert(field1 = "voimassaAlkuPvm", field2 = "voimassaLoppuPvm", asserter = DateIsNullOrNotBeforeAnotherDateAsserter.class, message = "{voimassaLoppuPvm.invalid}")
@Entity
@Table(name = KoodiVersio.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_" + KoodiVersio.TABLE_NAME
        + "_01", columnNames = { KoodiVersio.VERSIO_COLUMN_NAME, KoodiVersio.KOODI_COLUMN_NAME }))
@org.hibernate.annotations.Table(appliesTo = KoodiVersio.TABLE_NAME, comment = "Koodiversio sisältää mm. koodiarvon, " +
        "voimassaolopäivämäärät ja koodin tilan.")
@Cacheable
@BatchSize(size = 100)
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodiVersio", cascade = { CascadeType.ALL })
    private Set<KoodistoVersioKoodiVersio> koodistoVersios = new HashSet<>();

    @NotEmpty
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodiVersio", cascade = { CascadeType.ALL })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<KoodiMetadata> metadatas = new HashSet<>();

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "alakoodiVersio", cascade = { CascadeType.ALL })
    private Set<KoodinSuhde> ylakoodis = new HashSet<>();

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ylakoodiVersio", cascade = { CascadeType.ALL })
    private Set<KoodinSuhde> alakoodis = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        luotu = luotu == null ? new Date() : luotu;
        onUpdate();
    }
    
    @PreUpdate
    protected void onUpdate() {
        getCurrentUserOid().ifPresent(this::setPaivittajaOid);
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

    public Koodi getKoodi() {
        return koodi;
    }

    public void setKoodi(Koodi koodi) {
        this.koodi = koodi;
    }

    public Integer getVersio() {
        return versio;
    }

    public void setVersio(Integer versio) {
        this.versio = versio;
    }

    public Date getPaivitysPvm() {
        return paivitysPvm;
    }

    public void setPaivitysPvm(Date paivitysPvm) {
        this.paivitysPvm = paivitysPvm;
    }
    
    public Date getLuotu() {
        return luotu;
    }
    
    public void setLuotu(Date luotu) {
        this.luotu = luotu;
    }

    public Date getVoimassaAlkuPvm() {
        return voimassaAlkuPvm;
    }

    public void setVoimassaAlkuPvm(Date voimassaAlkuPvm) {
        this.voimassaAlkuPvm = voimassaAlkuPvm;
    }

    public Date getVoimassaLoppuPvm() {
        return voimassaLoppuPvm;
    }

    public void setVoimassaLoppuPvm(Date voimassaLoppuPvm) {
        this.voimassaLoppuPvm = voimassaLoppuPvm;
    }

    public Tila getTila() {
        return tila;
    }

    public void setTila(Tila tila) {
        this.tila = tila;
    }

    public String getKoodiarvo() {
        return koodiarvo;
    }

    public void setKoodiarvo(String koodiarvo) {
        this.koodiarvo = koodiarvo;
    }

    public Set<KoodinSuhde> getYlakoodis() {
        return ylakoodis;
    }

    public Set<KoodinSuhde> getAlakoodis() {
        return alakoodis;
    }

    public void addYlakoodi(KoodinSuhde ylakoodi) {
        this.ylakoodis.add(ylakoodi);
    }

    public void addAlakoodi(KoodinSuhde alakoodi) {
        this.alakoodis.add(alakoodi);
    }
    
    public void removeYlakoodi(KoodinSuhde ylakoodi) {
        this.ylakoodis.remove(ylakoodi);
    }
    
    public void removeAlakoodi(KoodinSuhde alakoodi) {
        this.alakoodis.remove(alakoodi);
    }

    public void removeKoodistoVersio(KoodistoVersioKoodiVersio koodistoVersio) {
        this.koodistoVersios.remove(koodistoVersio);
    }

    @AssertTrue(message = "Validation end date must not be before start date")
    public boolean getValidateDates() {
        return voimassaAlkuPvm != null && (voimassaLoppuPvm == null || !voimassaLoppuPvm.before(voimassaAlkuPvm));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getPaivittajaOid() {
        return paivittajaOid;
    }

    public void setPaivittajaOid(String paivittajaOid) {
        this.paivittajaOid = paivittajaOid;
    }

}
