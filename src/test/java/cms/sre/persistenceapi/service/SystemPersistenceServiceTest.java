package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.persistenceapi.TestConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfiguration.class)
@RunWith(SpringRunner.class)
public class SystemPersistenceServiceTest {

    private JacksonTester<System> json;

    @Autowired
    private SystemPersistenceService service;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;


    /**
     * method that sets up a Jackson tester instance to write System objects to/from JSON
     * @deprecated The use of the tester instance can be circumvented through other means, it's simply there
     *             to aid in the conversion of JSON objects back and forth as opposed to creating new Strings
     */
    @Deprecated
    @Before
    public void setUp(){
        json.initFields(this, new ObjectMapper());
    }

    /**
     * Method to test that the Service handles an upsertion operation correctly, using an Http client method as a mean of
     * delivering a request
     * @throws Exception exception thrown if the Http Client operation is unsuccessful on the client-side(server sided issues
     *                   should give HTTP errors such as 415 or 500).
     */
    @Test
    public void upsertTest() throws Exception{

        System system = new System();

        ObjectMapper mapper = new ObjectMapper();

        String jsonRequest = "{\"name\":\"Name\",\"description\":\"Desc\",\"owner\":\"Owner\",\"toasters\":[{\"packerScript\":{\"scriptFile\":{\"filename\":\"File\",\"binaryFile\":null,\"contents\":null}},\"terraformScript\":{\"mainScript\":{\"filename\":\"File\",\"binaryFile\":null,\"contents\":null},\"variableScript\":null,\"providerScript\":null,\"dataSourcesScript\":null},\"persistentVolumes\":[]}],\"dependenciesMap\":null}";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:" + this.port + "/test");
        StringEntity json = new StringEntity(jsonRequest);
        httpPost.setEntity(json);
        httpPost.setHeader("Accept","application/json");
        httpPost.setHeader("Content-Type","application/json");
        CloseableHttpResponse response = httpClient.execute(httpPost);

        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);


        //java.lang.System.out.println(responseString);

        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertEquals(jsonRequest, responseString);
        Assert.assertNotNull(response);

    }




}
