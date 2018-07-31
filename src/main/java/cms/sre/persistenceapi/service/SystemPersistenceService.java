package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;
import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.model.MongoPersistedSystem;
import cms.sre.persistenceapi.model.S3PersistedScriptFile;
import cms.sre.persistenceapi.repository.PersistedSystemRepository;
import cms.sre.persistenceapi.util.BucketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class SystemPersistenceService {

    private PersistedSystemRepository persistedSystemRepository;
    private MongoTemplate mongoTemplate;

    @Autowired
    private static S3PersistedScriptFileService fileService;

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

    public List<System> getSystems(){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findAll().forEach(mongoPersistedSystem -> {
            ret.add(strip(mongoPersistedSystem));
        });

        return ret;
    }

    public List<System> getSystemsByOwner(String owner){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findByOwner(owner).forEach(mongoPersistedSystem -> {
            ret.add(strip(mongoPersistedSystem));
        });
        return ret;
    }

    public List<System> getSystemsByOwnerAndName(String owner, String name){
        LinkedList<System> ret = new LinkedList<>();
        this.persistedSystemRepository.findByOwnerAndName(owner, name).forEach(mongoPersistedSystem -> {
            ret.add(strip(mongoPersistedSystem));
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

        return this.mongoTemplate.upsert(query, update, System.class).wasAcknowledged(); //&& fileService.persistScriptsInS3(system);
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
