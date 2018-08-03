package cms.sre.persistenceapi.util;

import cms.sre.dna_common_data_model.system.System;

import java.util.ArrayList;
import java.util.List;

public class SystemListWrapper {

    private List<System> listOfSystems;


    public List<System> getListOfSystems() {
        return listOfSystems;
    }

    public SystemListWrapper setListOfSystems(List<System> listOfSystems) {
        this.listOfSystems = listOfSystems;
        return this;
    }
}
