package se.ifmo.origin_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "addresses",
    uniqueConstraints = @UniqueConstraint(name = "uk_address_street", columnNames = {"street"}))
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addr_seq")
    @SequenceGenerator(name = "addr_seq", sequenceName = "addr_seq")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(length = 63)
    @Size(max=63)
    private String street; //Поле может быть null
}