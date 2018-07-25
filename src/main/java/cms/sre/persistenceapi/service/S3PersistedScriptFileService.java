package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;
import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.model.S3PersistedScriptFile;
import cms.sre.persistenceapi.util.BucketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3PersistedScriptFileService {

    @Autowired
    private BucketHandler bucketHandler;

    private byte[] getContents(String s3Location){
       return bucketHandler.getFileFromBucket(s3Location);
    }

    private boolean existsInS3(ScriptFile scriptFile){
        return bucketHandler.doesFileExist(scriptFile);
    }
    public File convertToFile(ScriptFile scriptFile){
        File file = new File(scriptFile.getFilename());
        try{
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(scriptFile.getBinaryFile());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return file;
    }

    private S3PersistedScriptFile updateFile(ScriptFile scriptFile, String location){
        bucketHandler.deleteFile(location);
        File file = convertToFile(scriptFile);
        bucketHandler.putObjectInBucket(file);
        return new S3PersistedScriptFile(scriptFile, location);
        /*
        bucketHandler.putObjectInBucket(file)
        */

    }

    private S3PersistedScriptFile insertFile(ScriptFile scriptFile ,String location){
        File file = convertToFile(scriptFile);
        bucketHandler.putObjectInBucket(file);
        return new S3PersistedScriptFile(scriptFile, location);
    }

    public ScriptFile getFullyRealizedScriptFile(ScriptFile scriptFile){
        ScriptFile ret = scriptFile;
        if(scriptFile instanceof S3PersistedScriptFile){
            S3PersistedScriptFile s3PersistedScriptFile = (S3PersistedScriptFile) scriptFile;
            String loc = s3PersistedScriptFile.getS3Location();
            s3PersistedScriptFile.setBinaryFile(this.getContents(loc));
            ret = s3PersistedScriptFile;
        }
        return ret;
    }

    public S3PersistedScriptFile persistInS3(ScriptFile scriptFile, String location){
        S3PersistedScriptFile ret = null;
        if(scriptFile instanceof S3PersistedScriptFile){
            insertFile(scriptFile, location);
        } else {
            if(this.existsInS3(scriptFile)){
                ret = this.updateFile(scriptFile, location);
            } else {
                ret = this.insertFile(scriptFile,location);
            }
        }
        return ret;
    }

    public List<File> pullByteData(List<System> sysList){
        List<File> fileList = new ArrayList<>();

        //Iterates through each individual System from the given list
        for(int i = 0, len = sysList.size(); i < len; i++){
            ArrayList<byte[]> bytes = new ArrayList<>();
            //Iterates through each toaster registered to the System
            for(Toaster toaster : sysList.get(i).getToasters()){

                bytes.add(toaster
                        .getPackerScript()
                        .getScriptFile()
                        .getBinaryFile()
                );

                bytes.add(toaster
                        .getTerraformScript()
                        .getVariableScript()
                        .getBinaryFile()
                );

                bytes.add(toaster
                        .getTerraformScript()
                        .getMainScript()
                        .getBinaryFile()
                );

                bytes.add(toaster
                        .getTerraformScript()
                        .getDataSourcesScript()
                        .getBinaryFile()
                );

                bytes.add(toaster
                        .getTerraformScript()
                        .getProviderScript()
                        .getBinaryFile()
                );
                //takes each set of toasters and writes a new file for each Script File
                for(int lcv = 0, al = bytes.size(); lcv < al; lcv++){
                    try{
                        File file = new File(sysList.get(i).getOwner()  + sysList.get(i).getName());
                        FileOutputStream outputStream = new FileOutputStream(file);

                        outputStream.write(bytes.get(lcv));
                        fileList.add(file);
                    }
                    catch(Exception e){
                        e.printStackTrace();

                    }
                }
                bytes.removeAll(bytes);

            }


        }
        return fileList;

    }
}
