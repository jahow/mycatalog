package jahow.mycatalog.api;

import jahow.mycatalog.api.CatalogRecord.KindEnum;
import jahow.mycatalog.entity.CatalogRecordEntity;
import jahow.mycatalog.mapping.CatalogRecordMapper;
import jahow.mycatalog.repository.CatalogRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CatalogRecordsServiceTest {

    @MockBean
    CatalogRecordRepository mockRepository;

    @Autowired
    CatalogRecordsService recordService;

    @Autowired
    CatalogRecordMapper mapper;

    CatalogRecord existingRecord;
    CatalogRecordEntity existingEntity;

    @BeforeEach
    public void setup() {
        this.existingRecord = new CatalogRecord();
        this.existingRecord.title("Existing title");
        this.existingRecord.description("Existing description");
        this.existingRecord.identifier("existing");
        this.existingRecord.created(OffsetDateTime.parse("2010-01-01T00:00:00Z"));
        this.existingRecord.updated(OffsetDateTime.parse("2011-01-01T00:00:00Z"));
        this.existingEntity = this.mapper.toEntity(this.existingRecord);
        this.existingEntity.setInternalId(1234L);

        Mockito.when(this.mockRepository.save(ArgumentMatchers.any(CatalogRecordEntity.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Mockito.when(this.mockRepository.findByIdentifier(this.existingRecord.getIdentifier()))
                .thenReturn(Optional.of(existingEntity));
        Mockito.when(this.mockRepository.findAll()).thenReturn(List.of(existingEntity));
    }

    @Test
    void createCatalogRecord() {
        var record = new CatalogRecord();
        record.identifier("aaa-bbb-ccc");
        record.title("record title");
        record.description("record abstract!");
        record.kind(KindEnum.DATASET);
        var result = this.recordService.createCatalogRecord(record);
        var created = result.getResultValue();

        assertTrue(result.hasSucceeded(), "creation should succeed");
        assertEquals(record.getTitle(), created.getTitle(), "returned record title is identical");
        assertEquals(record.getDescription(), created.getDescription(), "returned record description is identical");
        assertEquals(record.getKind(), created.getKind(), "returned record kind is identical");
        assertEquals(record.getIdentifier(), created.getIdentifier(), "returned record identifier is identical");
        assertNotNull(created.getCreated(), "creation date should not be null");
        assertNotNull(created.getUpdated(), "update date should not be null");
        assertEquals(created.getCreated(), created.getUpdated(), "creation and update date should be equal");
        verify(this.mockRepository).save(any(CatalogRecordEntity.class));
    }

    @Test
    void createCatalogRecord_existsAlready() {
        var record = new CatalogRecord();
        record.identifier("existing");
        record.title("record title");
        record.description("record abstract!");
        record.kind(KindEnum.DATASET);
        var result = this.recordService.createCatalogRecord(record);

        assertFalse(result.hasSucceeded(), "creation should fail");
        assertEquals("record.create.alreadyExists", result.getErrorCode());
        verify(this.mockRepository, times(0)).save(any(CatalogRecordEntity.class));
    }

    @Test
    void updateCatalogRecord() {
        var record = new CatalogRecord();
        record.identifier("existing");
        record.title("record title");
        record.description("record abstract!");
        record.created(OffsetDateTime.parse("2021-03-01T00:00:00Z"));
        record.updated(OffsetDateTime.parse("2021-03-01T00:00:00Z"));
        record.kind(KindEnum.DATASET);
        var result = this.recordService.updateCatalogRecord(record);
        var updated = result.getResultValue();

        assertTrue(result.hasSucceeded(), "updated should succeed");
        assertEquals(record.getTitle(), updated.getTitle(), "returned record title is identical");
        assertEquals(record.getDescription(), updated.getDescription(), "returned record description is identical");
        assertEquals(record.getKind(), updated.getKind(), "returned record kind is identical");
        assertEquals(record.getIdentifier(), updated.getIdentifier(), "returned record identifier is identical");
        assertEquals(record.getCreated(), updated.getCreated(), "returned record creation date is identical");
        assertEquals(1, updated.getUpdated().compareTo(updated.getCreated()),
                "update date should be more recent than creation date");
        verify(this.mockRepository).save(any(CatalogRecordEntity.class));
        verify(this.mockRepository)
                .save(argThat(arg -> arg.getInternalId().equals(this.existingEntity.getInternalId())));
    }

    @Test
    void updateCatalogRecord_notFound() {
        var record = new CatalogRecord();
        record.identifier("aa-bb-cc");
        record.title("record title");
        record.description("record abstract!");
        record.kind(KindEnum.DATASET);
        var result = this.recordService.updateCatalogRecord(record);

        assertFalse(result.hasSucceeded(), "update should fail");
        assertEquals("record.update.notFound", result.getErrorCode());
        verify(this.mockRepository, times(0)).save(any(CatalogRecordEntity.class));
    }

    @Test
    void readCatalogRecord() {
        var result = this.recordService.readCatalogRecord("existing");
        var record = result.getResultValue();

        assertTrue(result.hasSucceeded(), "read should succeed");
        assertEquals(this.existingRecord, record, "returned record is existing record");
        verify(this.mockRepository).findByIdentifier("existing");
    }

    @Test
    void readCatalogRecord_notFound() {
        var result = this.recordService.readCatalogRecord("aaa-aaa-aaa");
        assertFalse(result.hasSucceeded(), "read should fail");
        assertEquals("record.read.notFound", result.getErrorCode());
        verify(this.mockRepository).findByIdentifier("aaa-aaa-aaa");
    }

    @Test
    void deleteCatalogRecord() {
        var result = this.recordService.deleteCatalogRecord("existing");
        assertTrue(result.hasSucceeded(), "delete should succeed");
        verify(this.mockRepository).deleteByIdentifier("existing");
    }

    @Test
    void deleteCatalogRecord_notFound() {
        var result = this.recordService.deleteCatalogRecord("aaa-bbb");
        assertFalse(result.hasSucceeded(), "delete should fail");
        assertEquals("record.delete.notFound", result.getErrorCode());
        verify(this.mockRepository, times(0)).deleteByIdentifier(anyString());
    }

    @Test
    void getCatalogRecords() {
        var result = this.recordService.getCatalogRecords();
        var list = result.getResultValue();
        assertEquals(1, list.size());
        assertEquals(this.existingRecord.getTitle(), list.get(0).getTitle(), "title is identical");
        assertEquals(this.existingRecord.getIdentifier(), list.get(0).getIdentifier(), "identifier is identical");
    }
}
