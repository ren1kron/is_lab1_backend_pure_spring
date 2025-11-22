package se.ifmo.origin_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import se.ifmo.origin_backend.model.file_import.ImportOperation;

public interface ImportOperationRepo extends JpaRepository<ImportOperation, Long>, JpaSpecificationExecutor<ImportOperation> {}
