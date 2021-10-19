package jahow.mycatalog.repository;

import jahow.mycatalog.entity.CatalogRecordEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogRecordRepository extends JpaRepository<CatalogRecordEntity, Long> {
  Optional<CatalogRecordEntity> findByIdentifier(@NonNull String recordIdentifier);

  void deleteByIdentifier(@NonNull String recordIdentifier);
}
