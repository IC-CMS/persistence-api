package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.persistenceapi.model.PersistedSystem;
import cms.sre.persistenceapi.repository.PersistedSystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class SystemPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private PersistedSystemRepository persistedSystemRepository;
    private MongoTemplate mongoTemplate;

    private static System strip(PersistedSystem persistedSystem){
        return new System()
                .setToasters(persistedSystem.getToasters())
                .setOwner(persistedSystem.getOwner())
                .setName(persistedSystem.getName())
                .setDescription(persistedSystem.getDescription())
                .setDependenciesMap(persistedSystem.getDependenciesMap());
    }

    @Autowired
    public SystemPersistenceService(PersistedSystemRepository persistedSystemRepository, MongoTemplate mongoTemplate){
        this.persistedSystemRepository = persistedSystemRepository;
    }

    public List<System> getSystems(){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findAll().forEach(persistedSystem -> {
            ret.add(strip(persistedSystem));
        });

        return ret;
    }

    public List<System> getSystemsByOwner(String owner){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findByOwner(owner).forEach(persistedSystem -> {
            ret.add(strip(persistedSystem));
        });
        return ret;
    }

    public List<System> getSystemsByOwnerAndName(String owner, String name){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findByOwnerAndName(owner, name).forEach(persistedSystem -> {
            ret.add(strip(persistedSystem));
        });
        return ret;
    }

    public boolean upsert(System system){
        Query query = new Query()
                .addCriteria(Criteria.where("name").is(system.getName()));

        Update update = new Update()
                .set("description", system.getDescription())
                .set("owner", system.getOwner())
                .set("toasters", system.getToasters())
                .set("dependenciesMap", system.getDependenciesMap());

        return this.mongoTemplate.upsert(query, update, System.class)
            .wasAcknowledged();
    }

    public boolean upsert(List<System> systems){
        boolean ret = true;
        for(System system : systems){
            ret = this.upsert(system) && ret;
        }
        return ret;
    }

    public boolean remove(List<System> systems){
        Criteria criteria = new Criteria();

        for(System system : systems){
            criteria.orOperator(Criteria.where("name").is(system.getName()));
        }

        return this.mongoTemplate.remove(new Query().addCriteria(criteria), System.class)
                .wasAcknowledged();
    }

}
