package cms.sre.persistenceapi.util;

import cms.sre.dna_common_data_model.system.Toaster;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ToasterKeyDeserializer extends KeyDeserializer {

    private Logger logger = LoggerFactory.getLogger(ToasterKeyDeserializer.class);

    @Override
    public Toaster deserializeKey(String key, DeserializationContext ctxt) throws IOException{

        logger.info("Custom Key Deserialization has been accessed.");
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(key, Toaster.class);

    }
}
