package cms.sre.persistenceapi;

import cms.sre.mongo_connection_helper.MongoClientFactory;
import cms.sre.mongo_connection_helper.MongoClientParameters;
import cms.sre.persistenceapi.util.BucketHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@PropertySource(value="classpath:application.properties", ignoreResourceNotFound = true)
@EnableMongoRepositories
@EnableSwagger2
//@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@SpringBootApplication
public class App
{
//extends AbstractMongoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }

    private static String nullWrapper(String value){
        return value == null ? "(null)" : value;
    }

    private static String nullWrapper(String[] values){
        String ret = null;
        if(values != null && values.length > 0){
            ret = "[";
            for (int i = 0, len = values.length; i < len; i++) {
                String value = values[i];

                ret += nullWrapper(value);
                ret += i < len - 1 ? "," : "";
            }
            ret += "]";
        }
        return ret;
    }

    @Value("${mongodb.databaseName:#{null}}")
    private String mongoDatabaseName;

    @Value("${mongodb.keyStoreKeyPassword:#{null}}")
    private String mongoKeyStoreKeyPassword;

    @Value("${mongodb.keyStoreLocation:#{null}}")
    private String mongoKeyStoreLocation;

    @Value("${mongodb.keyStorePassword:#{null}}")
    private String mongoKeyStorePassword;

    @Value("${mongodb.trustStoreLocation:#{null}}")
    private String mongoTrustStoreLocation;

    @Value("${mongodb.trustStorePassword:#{null}}")
    private String mongoTrustStorePassword;

    @Value("${mongodb.username:#{null}}")
    private String mongoUsername;

    @Value("${mongodb.password:#{null}}")
    private String mongoPassword;

    @Value("${mongodb.replicaSetLocation:#{null}}")
    private String[] mongoReplicaSetLocation;

    @Value("${mongodb.mongoReplicaSetName:#{null}}")
    private String mongoReplicaSetName;

    @Value("${persistenceapi.defaultRegion:us-east-1}")
    private String defaultRegion;

    @Value("${persistenceapi.defaultName:defaultBucketName}")
    private String defaultBucketName;

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public BucketHandler bucketHandler(){
        return new BucketHandler(defaultBucketName, defaultRegion);
    }

    @Bean
    public AmazonS3 amazonS3(){
        AmazonS3 s3 = AmazonS3ClientBuilder
                        .standard()
                        .withRegion(bucketHandler().getRegionName())
                        .build();
        return s3;

    }


//    @Override
//    public MongoClient mongoClient() {
//        logger.info("Mongo Configuration Values");
//        logger.info("mongodb.keyStoreKeyPassword : " + nullWrapper(this.mongoKeyStoreKeyPassword));
//        logger.info("mongodb.keyStorePassword : " + nullWrapper(this.mongoKeyStorePassword));
//        logger.info("mongodb.keyStoreLocation : " + nullWrapper(this.mongoKeyStoreLocation));
//        logger.info("mongodb.trustStoreLocation : " + nullWrapper(this.mongoTrustStoreLocation));
//        logger.info("mongodb.trustStorePassword : " + nullWrapper(this.mongoTrustStorePassword));
//        logger.info("mongodb.databaseName : " + nullWrapper(this.mongoDatabaseName));
//        logger.info("mongodb.username : " + nullWrapper(this.mongoUsername));
//        logger.info("mongodb.password : " + nullWrapper(this.mongoPassword));
//        logger.info("mongodb.replicaSetLocation : " + nullWrapper(this.mongoReplicaSetLocation));
//        logger.info("mongodb.mongoReplicaSetName : " + nullWrapper(this.mongoReplicaSetName));
//
//        MongoClientParameters params = new MongoClientParameters()
//                .setKeyStoreKeyPassword(this.mongoKeyStoreKeyPassword)
//                .setKeyStoreLocation(this.mongoKeyStoreLocation)
//                .setKeyStorePassword(this.mongoKeyStorePassword)
//                .setTrustStoreLocation(this.mongoTrustStoreLocation)
//                .setTrustStorePassword(this.mongoTrustStorePassword)
//                .setDatabaseName(this.mongoDatabaseName)
//                .setMongoUsername(this.mongoUsername)
//                .setMongoPassword(this.mongoPassword)
//                .setReplicaSetLocations(this.mongoReplicaSetLocation)
//                .setReplicaSetName(this.mongoReplicaSetName);
//
//        MongoClient client = null;
//        if(this.mongoReplicaSetLocation != null && this.mongoReplicaSetLocation.length == 1 && this.mongoReplicaSetLocation[0].equalsIgnoreCase("localhost")) {
//            logger.debug("Connecting to Local Mongo Instance");
//            if (this.mongoUsername != null && !this.mongoUsername.isEmpty() &&
//                    this.mongoPassword != null && !this.mongoPassword.isEmpty() &&
//                    this.mongoDatabaseName != null && !this.mongoDatabaseName.isEmpty()) {
//                client = MongoClientFactory.getLocalhostMongoClient(this.mongoDatabaseName, this.mongoUsername, this.mongoPassword);
//            } else {
//                client = MongoClientFactory.getLocalhostMongoClient();
//            }
//        } else if(this.mongoReplicaSetLocation == null){
//            client = MongoClientFactory.getLocalhostMongoClient();
//        }else {
//            client = MongoClientFactory.getMongoClient(params);
//        }
//        return client;
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return this.mongoDatabaseName;
//    }
}
