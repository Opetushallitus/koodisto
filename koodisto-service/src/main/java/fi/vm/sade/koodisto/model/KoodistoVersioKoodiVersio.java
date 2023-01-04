package fi.vm.sade.koodisto.model;

import fi.vm.sade.koodisto.model.constraint.KoodistoVersioKoodiVersioConstraint;
import fi.vm.sade.koodisto.util.FieldLengths;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "koodistoVersio_koodiVersio", uniqueConstraints = {
        @UniqueConstraint(name = "UK_koodisto_koodiVersio_01", columnNames = {"koodistoVersio_id", "koodiVersio_id" }),
        @UniqueConstraint(name = "UK_koodisto_koodiVersio_02", columnNames = {"koodistoVersio_id", "koodiarvo" })
})
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

    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "koodiarvo", length = FieldLengths.DEFAULT_FIELD_LENGTH, nullable = false)
    private String koodiarvo;

    @PrePersist
    @PreUpdate
    public void resolveKoodiarvo() {
        koodiarvo = koodiVersio.getKoodiarvo();
    }
}
