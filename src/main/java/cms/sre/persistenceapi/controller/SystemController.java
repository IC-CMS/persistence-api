package cms.sre.persistenceapi.controller;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.service.SystemPersistenceService;
import cms.sre.persistenceapi.util.ToasterKeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class SystemController {

    private SystemPersistenceService systemPersistenceService;
    private Logger logger = LoggerFactory.getLogger(SystemController.class);

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
            ret = systems;
        }
        return ret;
    }

    //TEST METHOD
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public System postSystem(@RequestBody String system){
        try{
            ObjectMapper mapper = new ObjectMapper();

            SimpleModule module = new SimpleModule();

            module.addKeyDeserializer(Toaster.class, new ToasterKeyDeserializer());
            mapper.registerModule(module);
            return mapper.readValue(system, System.class);
        }
        catch(Exception e){
            logger.error("Mapping Failed");
            e.printStackTrace();
            return null;
        }

    }
    @DeleteMapping("/systems")
    public List<System> deleteSystems(List<System> systems){
        return systems;
    }

}

