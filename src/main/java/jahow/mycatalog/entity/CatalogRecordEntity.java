package jahow.mycatalog.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Entity(name = "CatalogRecord")
@Table(name = "records")
@Getter
@Setter
public class CatalogRecordEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  Long internalId;

  @Column(unique = true, nullable = false)
  @NaturalId
  String identifier;

  @Column
  String title;

  @Column
  String description;

  @Column
  OffsetDateTime created;

  @Column
  OffsetDateTime updated;

  @Column
  RecordKind kind;

  public enum RecordKind {
    SERVICE, DATASET
  }
}
