package cms.sre.persistenceapi.model.deserializer;

import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A Custom deserializer written to combat Jackson's issue of being unable to deserialize non-int or non-String keys of maps
 * The deserializer is just a basic object mapper that tells Jackson to deserialize the key the same way it deserializes
 * a normal Toaster Object
 */
public class ToasterKeyDeserializer extends KeyDeserializer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Toaster deserializeKey(String key, DeserializationContext ctxt) throws IOException {

        logger.info("Custom Key Deserialization has been accessed.");

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(key, Toaster.class);

    }
}
