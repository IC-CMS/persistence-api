package cms.sre.persistenceapi.controller;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.model.deserializer.ToasterKeyDeserializer;
import cms.sre.persistenceapi.service.SystemPersistenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * The REST Controller of the interface, intakes and returns System data based on operations, follows a general REST
 * Interface format.
 */
@RestController
public class SystemController {

    private SystemPersistenceService systemPersistenceService;
    private Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    public SystemController(SystemPersistenceService systemPersistenceService){
        this.systemPersistenceService = systemPersistenceService;
    }

    /**
     * Mapped method to return all systems
     * @return a list of each system in the API
     */
    @GetMapping("/systems")
    public List<System> getSystems(){ return this.systemPersistenceService.getSystems(); }

    /**
     * Controller Method that queries MongoDB for System Objects & their Data
     * @param owner the String value of the Owner of a system
     * @return A list of systems from the API that are owned by a specific user
     */
    @GetMapping("/systems/{owner}")
    public List<System> getSystemsByOwner(@PathVariable("owner") String owner){ return this.systemPersistenceService.getSystemsByOwner(owner); }

    /**
     * Controller Method that queries MongoDB for System Objects & their Data
     * @param owner the String value of the Owner of a system
     * @param name The name assigned to the System
     * @return A list of systems from the API that are owned by a specific user and have a specific name
     */
    @GetMapping("/systems/{owner}/{name}")
    public List<System> getSystemsByNameAndOwner(@PathVariable("owner") String owner, @PathVariable("name") String name){ return this.systemPersistenceService.getSystemsByOwnerAndName(owner, name); }

    /**
     * Controller Method meant to take data from the RequestBody  and convert it to a System object. The data is meant to
     * be ingested and split between S3 and Mongo
     * @param systems the list of systems to push to the API
     * @return the list of systems that have just been pushed
     */
    //@PutMapping("/systems")
    @PostMapping("/systems")
    public @ResponseBody List<System> upsertSystems(@RequestBody List<System> systems){
        List<System> ret = null;
        if(this.systemPersistenceService.upsert(systems)){
            ret = systems;
        }
        return ret;
    }

    //TODO- TEST METHOD: This Method is not meant to be included in the final build, and is used for testing purposes.
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public System postSystem(@RequestBody String system) throws Exception{

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        module.addKeyDeserializer(Toaster.class, new ToasterKeyDeserializer());
        mapper.registerModule(module);

        return mapper.readValue(system, System.class);
    }

    /**
     * Controller Method meant to delete a certain set of System Objects
     * @param systems the list of systems to be removed from the API
     * @return the list of systems that have been removed
     */
    @DeleteMapping("/systems")
    public List<System> deleteSystems(List<System> systems){
        return systems;
    }

}

