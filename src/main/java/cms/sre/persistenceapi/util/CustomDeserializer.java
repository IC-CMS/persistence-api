package cms.sre.persistenceapi.util;

import cms.sre.dna_common_data_model.system.PersistentVolume;
import cms.sre.dna_common_data_model.system.Toaster;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomDeserializer extends KeyDeserializer {

    private Logger logger = LoggerFactory.getLogger(CustomDeserializer.class);

    @Override
    public Toaster deserializeKey(String key, DeserializationContext ctxt) throws IOException{

        System.out.println("Hello from the Deserialization method!");
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        Toaster mmmToast = new Toaster();

        //{"packerScript":"script","terraformScript":"","persistentVolumes":[""]}

        return mmmToast;

    }
}
