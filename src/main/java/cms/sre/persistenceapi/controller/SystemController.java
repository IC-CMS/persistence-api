package cms.sre.persistenceapi.controller;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.persistenceapi.service.SystemPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/systems")
    @PostMapping("/systems")
    public @ResponseBody List<System> upsertSystems(@RequestBody List<System> systems){
        List<System> ret = null;
        if(this.systemPersistenceService.upsert(systems)){
            ret = systems;
        }
        return ret;
    }

    @DeleteMapping("/systems")
    public List<System> deleteSystems(List<System> systems){
        return systems;
    }
}
