package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.TestConfiguration;
import cms.sre.dna_common_data_model.system.System;
import cms.sre.persistenceapi.controller.SystemController;
import cms.sre.persistenceapi.repository.PersistedSystemRepository;
import cms.sre.persistenceapi.util.SystemListWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfiguration.class)
@RunWith(SpringRunner.class)
public class SystemPersistenceServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;


}
