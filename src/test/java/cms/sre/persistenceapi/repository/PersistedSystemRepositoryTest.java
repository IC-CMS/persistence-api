package cms.sre.persistenceapi.repository;

import cms.sre.persistenceapi.TestConfiguration;
import cms.sre.persistenceapi.controller.SystemControllerTest;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfiguration.class)
@RunWith(SpringRunner.class)
public class PersistedSystemRepositoryTest {
    private static Logger logger = LoggerFactory.getLogger(PersistedSystemRepositoryTest.class);

    private static MongodExecutable mongodExecutable;

    @LocalServerPort
    private int port;

    @Autowired
    private PersistedSystemRepository persistedSystemRepository;

    @Test
    public void autowiringTest(){
        Assertions.assertThat(this.persistedSystemRepository)
                .isNotNull();
    }



}
