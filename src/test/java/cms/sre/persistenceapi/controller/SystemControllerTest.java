package cms.sre.persistenceapi.controller;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.persistenceapi.TestConfiguration;
import de.flapdoodle.embed.mongo.MongodExecutable;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfiguration.class)
@RunWith(SpringRunner.class)
public class SystemControllerTest {

    private static Logger logger = LoggerFactory.getLogger(SystemControllerTest.class);

    private static MongodExecutable mongodExecutable;



    @LocalServerPort
    private int port;

    @Autowired
    private SystemController systemController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Tests to see if the autowiring has created/found the necessary beans
     */
    @Test
    public void autowiringTest(){
        Assertions.assertThat(this.systemController)
                .isNotNull();
    }

    @Test
    public void swagger2Test() {
        String json = this.testRestTemplate.getForObject("http://localhost:" + this.port + "/v2/api-docs", String.class);

        Assertions.assertThat(json)
                .isNotNull()
                .isNotEmpty()
                .contains("\"name\":\"system-controller\"")
                .contains("\"paths\"")
                .contains("\"/systems\"")
                .contains("\"/systems/{owner}\"")
                .contains("\"/systems/{owner}/{name}\"");
    }

    /**
     * Test method to see if the API can handle a GET request
     * Asserts to see if the returned system list is not null and pulls nothing
     */
    @Test
    public void getSystemsTest(){
        List<? extends System> ret = this.testRestTemplate.getForObject("http://localhost:"+this.port+"/systems", List.class);
        Assertions.assertThat(ret)
                .isNotNull();

        Assertions.assertThat(ret.size())
                .isEqualTo(0);
    }

    /**
     * Test method to see if the API can handle a Get request and that the MongoDB queries by owner correctly
     * Asserts to see if the returned system list is not null and pulls nothing
     */
    @Test
    public void getSystemsByOwnerTest(){
        List<? extends System> ret = this.testRestTemplate.getForObject("http://localhost:"+this.port+"/systems/test_owner", List.class);
        Assertions.assertThat(ret)
                .isNotNull();

        Assertions.assertThat(ret.size())
                .isEqualTo(0);
    }

    /** Test method to see if the API can handle a Get request and that the MongoDB queries by owner and system name correctly
     * Asserts to see if the returned system list is not null and pulls nothing
     */
    @Test
    public void getSystemsByNameAndOwnerTest(){
        List<? extends System> ret = this.testRestTemplate.getForObject("http://localhost:"+this.port+"/systems/test_owner/test_name", List.class);
        Assertions.assertThat(ret)
                .isNotNull();

        Assertions.assertThat(ret.size())
                .isEqualTo(0);
    }
}
