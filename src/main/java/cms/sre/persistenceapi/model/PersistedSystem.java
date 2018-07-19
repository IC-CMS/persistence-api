package cms.sre.persistenceapi.model;

import cms.sre.dna_common_data_model.system.System;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PersistedSystem extends System {

    @Id
    private String id;


    public PersistedSystem(){
        super();
    }

    public PersistedSystem(System system){
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

    public PersistedSystem setId(String id) {
        this.id = id;
        return this;
    }
}
