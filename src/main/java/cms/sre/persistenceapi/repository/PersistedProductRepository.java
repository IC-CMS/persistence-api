package cms.sre.persistenceapi.repository;

import cms.sre.persistenceapi.model.PersistedProduct;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PersistedProductRepository extends PagingAndSortingRepository<PersistedProduct, String> {

    List<PersistedProduct> findByTitle(String title);

    List<PersistedProduct> findByScmLocation(String scmLocation);

}
