package se.ifmo.origin_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_seq")
    @SequenceGenerator(name = "org_seq", sequenceName = "org_seq")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть
                        // уникальным, Значение этого поля должно генерироваться автоматически

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name; // Поле не может быть null, Строка не может быть пустой

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "coords_id", nullable = false)
    private Coordinates coordinates; // Поле не может быть null

    @NotNull
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDate creationDate; // Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @ManyToOne
    @JoinColumn(name = "off_addr_id")
    @NotNull
    private Location officialAddress; // Поле не может быть null

    @Positive
    @Column(name = "annual_turnover")
    private Long annualTurnover; // Поле может быть null, Значение поля должно быть больше 0

    @NotNull
    @Positive
    @Column(name = "employees_count", nullable = false)
    private Long employeesCount; // Поле не может быть null, Значение поля должно быть больше 0

    @Positive
    @Column(name = "rating")
    private int rating; // Значение поля должно быть больше 0

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OrganizationType type; // Поле может быть null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "postal_addr_id", nullable = false)
    private Address postalAddress; // Поле не может быть null

    @PrePersist
    void prePersist() {
        if (creationDate == null)
            creationDate = LocalDate.now();
    }
}
