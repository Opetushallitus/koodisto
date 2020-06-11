package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.model.constraint.KoodistoVersioKoodiVersioConstraint;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "koodistoversio_koodiversio", uniqueConstraints = @UniqueConstraint(name = "UK_koodisto_koodiversio_01", columnNames = {
        "koodistoversio_id", "koodiversio_id" }))
@KoodistoVersioKoodiVersioConstraint
@Cacheable
public class KoodistoVersioKoodiVersio extends BaseEntity {

    private static final long serialVersionUID = -5215588552498620061L;

    @NotNull
    @JoinColumn(name = "koodistoversio_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private KoodistoVersio koodistoVersio;

    @NotNull
    @JoinColumn(name = "koodiversio_id", nullable = false)
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
