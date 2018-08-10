package cms.sre.persistenceapi;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfiguration.class)
@RunWith(SpringRunner.class)
public class AppTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void contextLoadsTest(){

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
