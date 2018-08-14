package cms.sre.persistenceapi;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Configuration class used in conjunction with out test methods, helps to streamline stnading up a mongoDB instance
 * which is used to move test data around without impacting the actual instance.
 */
@org.springframework.boot.test.context.TestConfiguration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class TestConfiguration extends App {
    private MongodExecutable mongodExecutable;

    private static Logger logger = LoggerFactory.getLogger(TestConfiguration.class);
    public MongoClient mongoClient() {
        MongodStarter starter = MongodStarter.getDefaultInstance();

        int port = 27017;
        try {
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(port, Network.localhostIsIPv6()))
                    .build();

            this.mongodExecutable = starter.prepare(mongodConfig);
            MongodProcess process = mongodExecutable.start();

        } catch (Exception e) {
            //Wrapping Exceptions
            throw new RuntimeException(e);
        }
        return new MongoClient("localhost", port);
    }

    @Override
    public void finalize() {
        if (this.mongodExecutable != null) {
            this.mongodExecutable.stop();
        }
    }
}

