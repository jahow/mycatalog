package jahow.mycatalog.mapping;

import jahow.mycatalog.api.CatalogRecord;
import jahow.mycatalog.api.CatalogRecordBrief;
import jahow.mycatalog.entity.CatalogRecordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatalogRecordMapper {
  CatalogRecordEntity toEntity(CatalogRecord model);

  CatalogRecord toModel(CatalogRecordEntity entity);

  CatalogRecordBrief toModelBrief(CatalogRecordEntity entity);
}
