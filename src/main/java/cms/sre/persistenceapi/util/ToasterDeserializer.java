package cms.sre.persistenceapi.util;

import cms.sre.dna_common_data_model.hashicorpFile.PackerScript;
import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;
import cms.sre.dna_common_data_model.hashicorpFile.TerraformScript;
import cms.sre.dna_common_data_model.system.Toaster;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;

public class ToasterDeserializer extends StdDeserializer<Toaster> {

    public ToasterDeserializer(){
        this(null);
    }

    public ToasterDeserializer(Class<?> vc){

        super(vc);

    }

    @Override
    public Toaster deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Toaster mmmToast = new Toaster();
        JsonNode rootNode = jp.getCodec().readTree(jp);

        mmmToast.setPackerScript(
                new PackerScript()
                        .setScriptFile(
                                new ScriptFile()
                                        .setFilename(
                                                rootNode
                                                        .get("packerScript")
                                                        .get("scriptFile")
                                                        .get("fileName")
                                                        .asText())
                                        .setBinaryFile(
                                                rootNode.get("packerScript")
                                                .get("scriptFile")
                                                .get("binaryFile")
                                                .binaryValue())
                                        .setContents(
                                                rootNode.get("packerScript")
                                                        .get("scriptFile")
                                                        .get("contents")
                                                        .asText())))
                .setTerraformScript(
                        new TerraformScript()
                                .setMainScript(
                                        new ScriptFile()
                                        .setFilename(
                                                rootNode
                                                        .get("terraformScript")
                                                        .get("scriptFile")
                                                        .get("fileName")
                                                        .asText()
                                        )
                                .setBinaryFile(
                                        rootNode.get("terraformScript")
                                                .get("scriptFile")
                                                .get("binaryFile")
                                                .binaryValue()
                                ).setContents(
                                                rootNode.get("terraformScript")
                                                        .get("scriptFile")
                                                        .get("contents")
                                                        .asText()))
                                .setVariableScript(
                                new ScriptFile()
                                        .setFilename(
                                                rootNode
                                                        .get("terraformScript")
                                                        .get("scriptFile")
                                                        .get("fileName")
                                                        .asText()
                                        )
                                        .setBinaryFile(
                                                rootNode.get("terraformScript")
                                                        .get("scriptFile")
                                                        .get("binaryFile")
                                                        .binaryValue()
                                        ).setContents(
                                        rootNode.get("terraformScript")
                                                .get("scriptFile")
                                                .get("contents")
                                                .asText()))
                                .setProviderScript(
                                new ScriptFile()
                                        .setFilename(
                                                rootNode
                                                        .get("terraformScript")
                                                        .get("scriptFile")
                                                        .get("fileName")
                                                        .asText()
                                        )
                                        .setBinaryFile(
                                                rootNode.get("terraformScript")
                                                        .get("scriptFile")
                                                        .get("binaryFile")
                                                        .binaryValue()
                                        ).setContents(
                                        rootNode.get("terraformScript")
                                                .get("scriptFile")
                                                .get("contents")
                                                .asText()))
                                .setDataSourcesScript(
                                new ScriptFile()
                                        .setFilename(
                                                rootNode
                                                        .get("terraformScript")
                                                        .get("scriptFile")
                                                        .get("fileName")
                                                        .asText()
                                        )
                                        .setBinaryFile(
                                                rootNode.get("terraformScript")
                                                        .get("scriptFile")
                                                        .get("binaryFile")
                                                        .binaryValue()
                                        ).setContents(
                                        rootNode.get("terraformScript")
                                                .get("scriptFile")
                                                .get("contents")
                                                .asText())))
                .setPersistentVolumes(new ArrayList<>());

        return mmmToast;
    }
}
