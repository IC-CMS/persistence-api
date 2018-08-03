package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.hashicorpFile.PackerScript;
import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;
import cms.sre.dna_common_data_model.hashicorpFile.TerraformScript;
import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.TestConfiguration;
import cms.sre.persistenceapi.model.MongoPersistedSystem;
import cms.sre.persistenceapi.util.CustomDeserializer;
import cms.sre.persistenceapi.util.SystemListWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    @Before
    public void setUp(){
        json.initFields(this, new ObjectMapper());
    }

    @Test
    public void upsertTest() throws Exception{

        String jsonRequest = "{\"name\":\"Name\",\"description\":\"Desc\",\"owner\":\"Owner\",\"toasters\":[null],\"dependenciesMap\":null}";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:" + this.port + "/test");
        StringEntity json = new StringEntity(jsonRequest);
        httpPost.setEntity(json);
        httpPost.setHeader("Accept","application/json");
        httpPost.setHeader("Content-Type","application/json");
        CloseableHttpResponse response = httpClient.execute(httpPost);

        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);

        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertEquals(jsonRequest, responseString);
        Assert.assertNotNull(response);




    }

}