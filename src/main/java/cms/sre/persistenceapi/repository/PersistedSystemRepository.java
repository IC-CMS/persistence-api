package cms.sre.persistenceapi.repository;

import cms.sre.persistenceapi.model.MongoPersistedSystem;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PersistedSystemRepository extends PagingAndSortingRepository<MongoPersistedSystem, String> {

    List<MongoPersistedSystem> findByOwner(String owner);

    List<MongoPersistedSystem> findByOwnerAndName(String owner, String name);

}
