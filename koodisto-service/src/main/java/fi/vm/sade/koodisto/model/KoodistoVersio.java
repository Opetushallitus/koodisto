package fi.vm.sade.koodisto.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.koodisto.common.util.FieldLengths;
import fi.vm.sade.koodisto.model.constraint.fieldassert.DateIsNullOrNotBeforeAnotherDateAsserter;
import fi.vm.sade.koodisto.model.constraint.fieldassert.FieldAssert;

@FieldAssert(field1 = "voimassaAlkuPvm", field2 = "voimassaLoppuPvm", asserter = DateIsNullOrNotBeforeAnotherDateAsserter.class, message = "{voimassaLoppuPvm.invalid}")
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
    private Set<KoodistoVersioKoodiVersio> koodiVersios = new HashSet<KoodistoVersioKoodiVersio>();

    @NotEmpty
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koodistoVersio", cascade = { CascadeType.ALL })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<KoodistoMetadata> metadatas = new ArrayList<KoodistoMetadata>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "alakoodistoVersio", cascade = { CascadeType.ALL })
    private Set<KoodistonSuhde> ylakoodistos = new HashSet<KoodistonSuhde>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ylakoodistoVersio", cascade = { CascadeType.ALL })
    private Set<KoodistonSuhde> alakoodistos = new HashSet<KoodistonSuhde>();


    @PrePersist
    protected void onCreate() {
        luotu = luotu == null ? new Date() : luotu;
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

    public Set<KoodistonSuhde> getYlakoodistos() {
        return ylakoodistos;
    }

    public void setYlakoodistos(final Set<KoodistonSuhde> ylakoodisto) {
        this.ylakoodistos = ylakoodisto;
    }

    public Set<KoodistonSuhde> getAlakoodistos() {
        return alakoodistos;
    }

    public void setAlakoodistos(final Set<KoodistonSuhde> alakoodisto) {
        this.alakoodistos = alakoodisto;
    }
    
    public void removeYlaKoodistonSuhde(KoodistonSuhde ks) {
        ylakoodistos.remove(ks);
    }
    
    public void removeAlaKoodistonSuhde(KoodistonSuhde ks) {
        alakoodistos.remove(ks);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(this.getId()).append(this.versio).toString();
    }

    @AssertTrue(message = "Validation end date must not be before start date")
    private boolean getValidateDates() {
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

}
