package se.ifmo.origin_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loc_seq")
    @SequenceGenerator(name = "loc_seq", sequenceName = "loc_seq")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private float x;
    private int y;
    private float z;

    @NotNull
    @Column(nullable = false)
    private String name; // Поле не может быть null
}
