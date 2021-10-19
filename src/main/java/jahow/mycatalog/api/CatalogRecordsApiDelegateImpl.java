package jahow.mycatalog.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogRecordsApiDelegateImpl implements RecordsApiDelegate {
  @Autowired
  CatalogRecordsService recordService;

  @Override
  public ResponseEntity<CatalogRecord> getRecord(String identifier) {
    var result = this.recordService.readCatalogRecord(identifier);
    if (!result.hasSucceeded()) {
      return ResponseEntity.badRequest().body(null); // FIXME: return actual result
    } else {
      return ResponseEntity.ok(result.getResultValue());
    }
  }

  @Override
  public ResponseEntity<CatalogRecord> createRecord(CatalogRecord catalogRecord) {
    var result = this.recordService.createCatalogRecord(catalogRecord);
    if (!result.hasSucceeded()) {
      return ResponseEntity.badRequest().body(null); // FIXME: return actual result
    } else {
      return ResponseEntity.status(HttpStatus.CREATED).body(result.getResultValue());
    }
  }

  @Override
  public ResponseEntity<List<CatalogRecordBrief>> getAllRecords() {
    return ResponseEntity.ok(this.recordService.getCatalogRecords().getResultValue());
  }

  @Override
  public ResponseEntity<CatalogRecord> updateRecord(String identifier,
      CatalogRecord catalogRecord) {
    var result = this.recordService.updateCatalogRecord(catalogRecord);
    if (!result.hasSucceeded()) {
      return ResponseEntity.badRequest().body(null); // FIXME: return actual result
    } else {
      return ResponseEntity.ok(result.getResultValue());
    }
  }
}
