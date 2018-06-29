package fi.vm.sade.koodisto.model;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.koodisto.model.constraint.KoodistoVersioKoodiVersioConstraint;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "koodistoVersio_koodiVersio", uniqueConstraints = @UniqueConstraint(name = "UK_koodisto_koodiVersio_01", columnNames = {
        "koodistoVersio_id", "koodiVersio_id" }))
@KoodistoVersioKoodiVersioConstraint
@Cacheable
public class KoodistoVersioKoodiVersio extends BaseEntity {

    private static final long serialVersionUID = -5215588552498620061L;

    @NotNull
    @JoinColumn(name = "koodistoVersio_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private KoodistoVersio koodistoVersio;

    @NotNull
    @JoinColumn(name = "koodiVersio_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private KoodiVersio koodiVersio;

    public KoodistoVersio getKoodistoVersio() {
        return koodistoVersio;
    }

    public void setKoodistoVersio(KoodistoVersio koodistoVersio) {
        this.koodistoVersio = koodistoVersio;
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
