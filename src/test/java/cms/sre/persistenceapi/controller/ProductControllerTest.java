package cms.sre.persistenceapi.controller;

import cms.sre.dna_common_data_model.product_list.Product;
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
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ProductControllerTest {

    private static Logger logger = LoggerFactory.getLogger(ProductControllerTest.class);

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
    private ProductController productController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void autowiringTest(){
        Assertions.assertThat(this.productController)
                .isNotNull();
    }

    @Test
    public void swagger2Test() {
        String json = this.testRestTemplate.getForObject("http://localhost:" + this.port + "/v2/api-docs", String.class);

        Assertions.assertThat(json)
                .isNotNull()
                .isNotEmpty()
                .contains("\"name\":\"product-controller\"")
                .contains("\"paths\"")
                .contains("\"/products\"")
                .contains("\"/products/{title}\"")
                .contains("\"/productsByScmLocation/{scmLocation}\"");
    }

    @Test
    public void getProductsTest(){
        List<? extends Product> ret = this.testRestTemplate.getForObject("http://localhost:"+this.port+"/products", List.class);
        Assertions.assertThat(ret)
                .isNotNull();

        Assertions.assertThat(ret.size())
                .isEqualTo(0);
    }

    @Test
    public void getProductsByTitleTest(){
        List<? extends Product> ret = this.testRestTemplate.getForObject("http://localhost:"+this.port+"/products/test_title", List.class);
        Assertions.assertThat(ret)
                .isNotNull();

        Assertions.assertThat(ret.size())
                .isEqualTo(0);
    }

    @Test
    public void getProductsByScmLocationTest(){
        List<? extends Product> ret = this.testRestTemplate.getForObject("http://localhost:"+this.port+"/productsByScmLocation/test_scm_location", List.class);
        Assertions.assertThat(ret)
                .isNotNull();

        Assertions.assertThat(ret.size())
                .isEqualTo(0);
    }
}
