package jahow.mycatalog.api;

import jahow.mycatalog.entity.CatalogRecordEntity;
import jahow.mycatalog.mapping.CatalogRecordMapper;
import jahow.mycatalog.repository.CatalogRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CatalogRecordsService {
  @Autowired
  private CatalogRecordMapper mapper;

  @Autowired
  private CatalogRecordRepository repository;

  public OperationResult<CatalogRecord> createCatalogRecord(CatalogRecord record) {
    if (this.repository.findByIdentifier(record.getIdentifier()).isPresent()) {
      return OperationResult.error("record.create.alreadyExists");
    }
    var now = OffsetDateTime.now();
    record.setCreated(now);
    record.setUpdated(now);
    CatalogRecordEntity e = this.repository.save(this.mapper.toEntity(record));
    return OperationResult.ok(this.mapper.toModel(e));
  }

  public OperationResult<CatalogRecord> updateCatalogRecord(CatalogRecord record) {
    var result = this.repository.findByIdentifier(record.getIdentifier());
    if (result.isEmpty()) {
      return OperationResult.error("record.update.notFound");
    }
    var existing = result.get();
    var entity = this.mapper.toEntity(record);
    entity.setUpdated(OffsetDateTime.now());
    entity.setInternalId(existing.getInternalId());
    this.repository.save(entity);
    return OperationResult.ok(this.mapper.toModel(entity));
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
