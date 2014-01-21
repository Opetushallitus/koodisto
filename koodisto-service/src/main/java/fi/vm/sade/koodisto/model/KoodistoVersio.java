package fi.vm.sade.koodisto.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.validator.constraints.NotEmpty;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.koodisto.common.util.FieldLengths;
import fi.vm.sade.koodisto.model.constraint.fieldassert.DateIsNullOrAfterAnotherDateAsserter;
import fi.vm.sade.koodisto.model.constraint.fieldassert.FieldAssert;

@FieldAssert(field1 = "voimassaAlkuPvm", field2 = "voimassaLoppuPvm", asserter = DateIsNullOrAfterAnotherDateAsserter.class, message = "{voimassaLoppuPvm.invalid}")
@Entity
@Table(name = KoodistoVersio.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_" + KoodistoVersio.TABLE_NAME
        + "_01", columnNames = { KoodistoVersio.VERSIO_COLUMN_NAME, KoodistoVersio.KOODISTO_COLUMN_NAME }))
@org.hibernate.annotations.Table(appliesTo = KoodistoVersio.TABLE_NAME, comment = "Koodistoversio sisältää mm. " +
        "koodiston päivityspäivämäärän, voimassaolopäivämäärät ja koodiston tilan.")
@Cacheable
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodistoVersio", cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<KoodistoVersioKoodiVersio> koodiVersios = new HashSet<KoodistoVersioKoodiVersio>();

    @NotEmpty
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodistoVersio", cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<KoodistoMetadata> metadatas = new ArrayList<KoodistoMetadata>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "alakoodistoVersio", cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    private Set<KoodistonSuhde> ylakoodisto = new HashSet<KoodistonSuhde>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ylakoodistoVersio", cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    private Set<KoodistonSuhde> alakoodisto = new HashSet<KoodistonSuhde>();


    @PrePersist
    protected void onCreate() {
        onUpdate();
    }

    @PreUpdate
    protected void onUpdate() {
        this.paivitysPvm = new Date();
    }

    public Koodisto getKoodisto() {
        return koodisto;
    }

    public void setKoodisto(Koodisto koodisto) {
        this.koodisto = koodisto;
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

    public List<KoodistoMetadata> getMetadatas() {
        return Collections.unmodifiableList(metadatas);
    }

    public void removeKoodiVersios(Collection<KoodistoVersioKoodiVersio> koodiVersios) {
        this.koodiVersios.removeAll(koodiVersios);
    }

    public void removeKoodiVersio(KoodistoVersioKoodiVersio koodiVersio) {
        this.koodiVersios.remove(koodiVersio);
    }

    public Set<KoodistonSuhde> getYlakoodisto() {
        return ylakoodisto;
    }

    public void setYlakoodisto(final Set<KoodistonSuhde> ylakoodisto) {
        this.ylakoodisto = ylakoodisto;
    }

    public Set<KoodistonSuhde> getAlakoodisto() {
        return alakoodisto;
    }

    public void setAlakoodisto(final Set<KoodistonSuhde> alakoodisto) {
        this.alakoodisto = alakoodisto;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(this.getId()).append(this.versio).toString();
    }

    @AssertTrue(message = "Validation start date must be before validation end date")
    private boolean getValidateDates() {
        return voimassaAlkuPvm != null && (voimassaLoppuPvm == null || voimassaLoppuPvm.after(voimassaAlkuPvm));
    }
}
