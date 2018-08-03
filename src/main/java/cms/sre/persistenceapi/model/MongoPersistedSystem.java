package cms.sre.persistenceapi.model;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.App;
import cms.sre.persistenceapi.util.CustomDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document
public class MongoPersistedSystem extends System {

    @Id
    private String id;


    public MongoPersistedSystem(){
        super();
    }

    public MongoPersistedSystem(System system){

        super();

        super.setDependenciesMap(system.getDependenciesMap())
                .setDescription(system.getDescription())
                .setName(system.getName())
                .setOwner(system.getOwner())
                .setToasters(system.getToasters());
    }

    public String getId() {
        return id;
    }

    public MongoPersistedSystem setId(String id) {
        this.id = id;
        return this;
    }

}
