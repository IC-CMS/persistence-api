package cms.sre.persistenceapi;

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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class AppTest {

    @LocalServerPort
    private int port;

    private static Logger logger = LoggerFactory.getLogger(AppTest.class);

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

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void contextLoadsTest(){
        ;
    }

    @Test
    public void swagger2Test(){
        String json = this.testRestTemplate.getForObject("http://localhost:"+this.port+"/v2/api-docs", String.class);
        Assertions.assertThat(json)
                .isNotNull()
                .isNotEmpty()
                .contains("\"swagger\":\"2.0\"")
                .contains("\"host\":\"localhost:"+this.port+"\",");

        String uiHtml = this.testRestTemplate.getForObject("http://localhost:"+this.port+"/swagger-ui.html", String.class);
        Assertions.assertThat(uiHtml)
                .isNotNull()
                .isNotEmpty()
                .contains("<!DOCTYPE html>")
                .contains("<body>")
                .contains("</body>")
                .contains("</html>");
    }
}
