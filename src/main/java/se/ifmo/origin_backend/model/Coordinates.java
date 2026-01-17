package se.ifmo.origin_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coordinates")
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cord_seq_gen")
    @SequenceGenerator(name = "cord_seq_gen", sequenceName = "cord_id_seq", initialValue = 1, allocationSize = 50)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Max(562)
    @Column(nullable = false)
    private Long x; // Максимальное значение поля: 562, Поле не может быть null

    @NotNull
    @Column(nullable = false)
    private Long y; // Поле не может быть null
}
