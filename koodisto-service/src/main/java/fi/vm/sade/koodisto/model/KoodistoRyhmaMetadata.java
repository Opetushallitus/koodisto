/**
 *
 */
package fi.vm.sade.koodisto.model;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonView;
import fi.vm.sade.koodisto.util.FieldLengths;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author kkammone
 */
@Table(name = KoodistoRyhmaMetadata.TABLE_NAME, uniqueConstraints = @UniqueConstraint(name = "UK_"
        + KoodistoRyhmaMetadata.TABLE_NAME + "_01", columnNames = {KoodistoRyhmaMetadata.KIELI_COLUMN_NAME,
        KoodistoRyhmaMetadata.KOODISTO_RYHMA_COLUMN_NAME}))
@Comment("Sisältää koodistoryhmän metatiedot, kuten nimi ja kieli.")
@Entity
@Cacheable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KoodistoRyhmaMetadata extends BaseEntity {

    private static final long serialVersionUID = 287785307475416064L;

    public static final String TABLE_NAME = "koodistoRyhmaMetadata";
    public static final String KIELI_COLUMN_NAME = "kieli";
    public static final String KOODISTO_RYHMA_COLUMN_NAME = "koodistoRyhma_id";

    @NotBlank
    @Size(min = 0, max = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Column(name = "nimi", length = FieldLengths.DEFAULT_FIELD_LENGTH, nullable = false)
    @JsonView({JsonViews.Basic.class, JsonViews.Simple.class})
    private String nimi;

    @NotNull
    @Column(name = KIELI_COLUMN_NAME, nullable = false, length = FieldLengths.DEFAULT_FIELD_LENGTH)
    @Enumerated(EnumType.STRING)
    @JsonView({JsonViews.Basic.class, JsonViews.Simple.class})
    private Kieli kieli;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = KOODISTO_RYHMA_COLUMN_NAME, nullable = false)
    private KoodistoRyhma koodistoRyhma;

}
