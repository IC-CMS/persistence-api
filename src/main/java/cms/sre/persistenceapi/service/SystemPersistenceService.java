package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.persistenceapi.model.MongoPersistedSystem;
import cms.sre.persistenceapi.repository.PersistedSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * The Service that handles the business logic of what occurs in the controller
 */
@Service
public class SystemPersistenceService {

    private PersistedSystemRepository persistedSystemRepository;
    private MongoTemplate mongoTemplate;

    @Autowired
    private static S3PersistedScriptFileService fileService;

    /**
     * Method intended to hydrate a system from its persisted equivalent
     * @param mongoPersistedSystem the already stored system that contains our data
     * @return a new system that has been updated wit hthe data of its persisted equivalent
     */
    private static System strip(MongoPersistedSystem mongoPersistedSystem){

        //fileService.hydrateScripts(mongoPersistedSystem);

        return new System()
                .setToasters(mongoPersistedSystem.getToasters())
                .setOwner(mongoPersistedSystem.getOwner())
                .setName(mongoPersistedSystem.getName())
                .setDescription(mongoPersistedSystem.getDescription())
                .setDependenciesMap(mongoPersistedSystem.getDependenciesMap());
    }


    @Autowired
    public SystemPersistenceService(PersistedSystemRepository persistedSystemRepository, MongoTemplate mongoTemplate){
        this.persistedSystemRepository = persistedSystemRepository;
    }

    /**
     * HTTP GET method equivalent
     * @return a list of hydrated Systems
     */
    public List<System> getSystems(){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findAll().forEach(mongoPersistedSystem -> {
            ret.add(strip(mongoPersistedSystem));
        });

        return ret;
    }

    /**
     * HTTP GEt method equivalent with owner query param
     * @param owner name of the owner of a system
     * @return a list of hydrated systems belonigng to a specific owner
     */
    public List<System> getSystemsByOwner(String owner){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findByOwner(owner).forEach(mongoPersistedSystem -> {
            ret.add(strip(mongoPersistedSystem));
        });
        return ret;
    }

    /**
     * HTTP GET method equivalent with System Name and owner query params
     * @param owner the owner of a system
     * @param name the name assigned to the system
     * @return a list of hydrated systems with a specific name belonging to an owner
     */
    public List<System> getSystemsByOwnerAndName(String owner, String name){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findByOwnerAndName(owner, name).forEach(mongoPersistedSystem -> {
            ret.add(strip(mongoPersistedSystem));
        });
        return ret;
    }

    /**
     * Internal overloaded method that upserts a system into the API
     * @param system the system to be uploaded
     * @return a boolean confirmation that everything succeeded
     */
    public boolean upsert(System system){
        Query query = new Query()
                .addCriteria(Criteria.where("name").is(system.getName()));

        Update update = new Update()
                .set("description", system.getDescription())
                .set("owner", system.getOwner())
                .set("toasters", system.getToasters())
                .set("dependenciesMap", system.getDependenciesMap());

        return this.mongoTemplate.upsert(query, update, System.class).wasAcknowledged(); //&& fileService.persistScriptsInS3(system);
    }

    /**
     * HTTP Post method equivalent that handles the looping logic for uploading a list of systems
     * @param systems the list of systems to be uploaded
     * @return boolean confirmation that everything succeeded
     */
    public boolean upsert(List<System> systems){
        boolean ret = true;
        for(System system : systems){
            ret = this.upsert(system) && ret;
        }
        return ret;
    }

    /**
     * Http DELETE method equivalent
     * @param systems list of systems to be removed
     * @return boolean confirmation that everything succeeded
     */
    public boolean remove(List<System> systems){
        Criteria criteria = new Criteria();

        for(System system : systems){
            criteria.orOperator(Criteria.where("name").is(system.getName()));
        }

        return this.mongoTemplate.remove(new Query().addCriteria(criteria), System.class)
                .wasAcknowledged();
    }
}
