package cms.sre.persistenceapi.model;

import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;

public class S3PersistedScriptFile extends ScriptFile {
    private String s3Location;

    public S3PersistedScriptFile(){
        super();
    }

    public S3PersistedScriptFile(ScriptFile scriptFile, String s3Location){
        super();
        this.setBinaryFile(scriptFile.getBinaryFile());
        this.setContents(scriptFile.getContents());
        this.setFilename(scriptFile.getFilename());
        this.s3Location = s3Location;
    }

    public String getS3Location() {
        return s3Location;
    }

    public S3PersistedScriptFile setS3Location(String s3Location) {
        this.s3Location = s3Location;
        return this;
    }
}
