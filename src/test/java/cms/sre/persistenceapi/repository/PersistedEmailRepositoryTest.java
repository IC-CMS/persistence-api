package cms.sre.persistenceapi.repository;

import cms.sre.dna_common_data_model.emailnotifier.Email;
import cms.sre.persistenceapi.model.PersistedEmail;
import cms.sre.persistenceapi.service.EmailPersistenceService;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class PersistedEmailRepositoryTest {

    private static Logger logger = LoggerFactory.getLogger(PersistedEmailRepositoryTest.class);

    private static MongodExecutable mongodExecutable;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    EmailPersistenceService emailPersistenceService;


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
    private PersistedEmailRepository persistedEmailRepository;

    @Test
    public void autowiringTest(){
        Assertions.assertThat(this.persistedEmailRepository)
                .isNotNull();
    }

    @Test
    public void writeDataToDatabase() {

        Email email = new Email();

        email.setEmailAddress("dummy@dummy.com");
        email.setSubject("Test Email");
        email.setBody("Now is the time for all good men\n");
        email.setCreatedDate(new Date(LocalDate.now().toEpochDay()));

        String uuid = email.getUuid();

        logger.info("Writing to database");

        PersistedEmail persistedEmail = new PersistedEmail(email);

        assertEquals(persistedEmail.getUuid(), uuid);

        emailPersistenceService.saveEmail(email);

        logger.info("Querying database");
        List<Email> emails = emailPersistenceService.getEmails();

        assertEquals(1, emails.size());

        logger.info("Deleting email from database");
        emailPersistenceService.deleteEmail(uuid);

       emails = emailPersistenceService.getEmails();

       assertEquals(0, emails.size());

    }
}