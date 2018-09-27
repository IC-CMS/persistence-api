package cms.sre.persistenceapi.repository;

import cms.sre.persistenceapi.model.PersistedEmail;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PersistedEmailRepository extends PagingAndSortingRepository<PersistedEmail, String> {

    @Override
    List<PersistedEmail> findAll();

    PersistedEmail findByUuid(String uuid);

    PersistedEmail save(PersistedEmail persistedEmail);

    Long deleteByUuid(String uuid);

}
