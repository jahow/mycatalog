package jahow.mycatalog.api;

import jahow.mycatalog.entity.CatalogRecordEntity;
import jahow.mycatalog.mapping.CatalogRecordMapper;
import jahow.mycatalog.repository.CatalogRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CatalogRecordsService {
  @Autowired
  private CatalogRecordMapper mapper;

  @Autowired
  private CatalogRecordRepository repository;

  @Autowired
  private Validator validator;

  public OperationResult<CatalogRecord> createCatalogRecord(CatalogRecord record) {
    if (this.repository.findByIdentifier(record.getIdentifier()).isPresent()) {
      return OperationResult.error("record.create.alreadyExists");
    }
    var now = OffsetDateTime.now();
    record.setCreated(now);
    record.setUpdated(now);

    Set<ConstraintViolation<CatalogRecord>> violations = validator.validate(record);
    if (!violations.isEmpty()) {
      return OperationResult.error("record.create.invalidObject");
    }

    CatalogRecordEntity e = this.repository.save(this.mapper.toEntity(record));
    return OperationResult.ok(this.mapper.toModel(e));
  }

  public OperationResult<CatalogRecord> updateCatalogRecord(CatalogRecord record) {
    var result = this.repository.findByIdentifier(record.getIdentifier());
    if (result.isEmpty()) {
      return OperationResult.error("record.update.notFound");
    }
    var existingEntity = result.get();
    var existingRecord = this.mapper.toModel(existingEntity);
    record.created(existingRecord.getCreated());
    record.updated(OffsetDateTime.now());
    if (record.getDescription() == null)
      record.description(existingRecord.getDescription());
    if (record.getKind() == null)
      record.kind(existingRecord.getKind());

    Set<ConstraintViolation<CatalogRecord>> violations = validator.validate(record);
    if (!violations.isEmpty()) {
      return OperationResult.error("record.update.invalidObject");
    }

    var updatedEntity = this.mapper.toEntity(record);
    updatedEntity.setInternalId(existingEntity.getInternalId());

    var updated = this.repository.save(updatedEntity);
    return OperationResult.ok(this.mapper.toModel(updated));
  }

  public OperationResult<CatalogRecord> readCatalogRecord(String recordIdentifier) {
    Optional<CatalogRecordEntity> optRecord = this.repository.findByIdentifier(recordIdentifier);
    if (optRecord.isEmpty()) {
      return OperationResult.error("record.read.notFound");
    }
    return OperationResult.ok(this.mapper.toModel(optRecord.get()));
  }

  public OperationResult deleteCatalogRecord(String recordIdentifier) {
    if (this.repository.findByIdentifier(recordIdentifier).isEmpty()) {
      return OperationResult.error("record.delete.notFound");
    }
    this.repository.deleteByIdentifier(recordIdentifier);
    return OperationResult.ok();
  }

  public OperationResult<List<CatalogRecordBrief>> getCatalogRecords() {
    var list = this.repository.findAll().stream().map(entity -> this.mapper.toModelBrief(entity))
        .collect(Collectors.toList());
    return OperationResult.ok(list);
  }
}
