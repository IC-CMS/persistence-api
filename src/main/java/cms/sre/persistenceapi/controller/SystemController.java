package cms.sre.persistenceapi.controller;

import cms.sre.dna_common_data_model.hashicorpFile.PackerScript;
import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;
import cms.sre.dna_common_data_model.hashicorpFile.TerraformScript;
import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.service.SystemPersistenceService;
import cms.sre.persistenceapi.util.CustomDeserializer;
import cms.sre.persistenceapi.util.SystemListWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class SystemController {

    private SystemPersistenceService systemPersistenceService;

    @Autowired
    public SystemController(SystemPersistenceService systemPersistenceService){
        this.systemPersistenceService = systemPersistenceService;
    }

    @GetMapping("/systems")
    public List<System> getSystems(){ return this.systemPersistenceService.getSystems(); }

    @GetMapping("/systems/{owner}")
    public List<System> getSystemsByOwner(@PathVariable("owner") String owner){ return this.systemPersistenceService.getSystemsByOwner(owner); }

    @GetMapping("/systems/{owner}/{name}")
    public List<System> getSystemsByNameAndOwner(@PathVariable("owner") String owner, @PathVariable("name") String name){ return this.systemPersistenceService.getSystemsByOwnerAndName(owner, name); }

    //@PutMapping("/systems")
    @PostMapping("/systems")
    public @ResponseBody List<System> upsertSystems(@RequestBody List<System> systems){
        List<System> ret = null;
        if(this.systemPersistenceService.upsert(systems)){
            ret = systems; //originally list of systems
        }
        return ret;
    }
    //TEST METHOD
    @RequestMapping(value = "/test", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public System postSystem(@JsonDeserialize(keyUsing = CustomDeserializer.class) @RequestBody String system){
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.readValue(system, System.class);
        }
        catch(Exception e){
            java.lang.System.out.println("Mapping Failed");
            return null;
        }

    }

    @DeleteMapping("/systems")
    public List<System> deleteSystems(SystemListWrapper wrapper){
        return wrapper.getListOfSystems();
    }
}
