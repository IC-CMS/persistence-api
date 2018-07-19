package cms.sre.persistenceapi.repository;

import cms.sre.persistenceapi.model.PersistedSystem;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PersistedSystemRepository extends PagingAndSortingRepository<PersistedSystem, String> {

    List<PersistedSystem> findByOwner(String owner);

    List<PersistedSystem> findByOwnerAndName(String owner, String name);

}
