package cms.sre.persistenceapi.repository;

import cms.sre.persistenceapi.controller.ProductControllerTest;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class PersistedProductRepositoryTest {
    private static Logger logger = LoggerFactory.getLogger(PersistedProductRepositoryTest.class);

    private static MongodExecutable mongodExecutable;

    @BeforeClass
    public static void beforeClass(){
        logger.info("Starting up Local MongoDb");
        int port = 27017;

        try {
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(port, Network.localhostIsIPv6()))
                    .build();

            mongodExecutable = MongodStarter.getDefaultInstance()
                    .prepare(mongodConfig);

            mongodExecutable.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterClass(){
        logger.info("Stopping up Local MongoDb");
        if(mongodExecutable != null){
            mongodExecutable.stop();
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private PersistedProductRepository persistedProductRepository;

    @Test
    public void autowiringTest(){
        Assertions.assertThat(this.persistedProductRepository)
                .isNotNull();
    }



}
