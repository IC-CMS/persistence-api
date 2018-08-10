package cms.sre.persistenceapi.repository;

import cms.sre.persistenceapi.TestConfiguration;
import de.flapdoodle.embed.mongo.MongodExecutable;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

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
