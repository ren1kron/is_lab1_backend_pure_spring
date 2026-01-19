package se.ifmo.origin_backend.model.file_import;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.Instant;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "import_operation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "import_op_seq")
    @SequenceGenerator(name = "org_seq", sequenceName = "org_seq")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status;

    @Column(name = "created_count")
    private Integer createdCount; // null for failed / in_progress

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_object_key", length = 512)
    private String fileObjectKey;

    @Column(name = "file_content_type", length = 255)
    private String fileContentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @PrePersist
    public void onCreate() {
        if (startedAt == null) {
            startedAt = Instant.now();
        }
    }
}
