package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.model.constraint.KoodistoVersioKoodiVersioConstraint;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "koodistoVersio_koodiVersio", uniqueConstraints = @UniqueConstraint(name = "UK_koodisto_koodiVersio_01", columnNames = {
        "koodistoVersio_id", "koodiVersio_id" }))
@KoodistoVersioKoodiVersioConstraint
@Cacheable
@Getter
@Setter
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

}
