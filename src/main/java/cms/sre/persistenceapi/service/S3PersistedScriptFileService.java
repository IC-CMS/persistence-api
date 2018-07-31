package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;
import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.model.MongoPersistedSystem;
import cms.sre.persistenceapi.model.S3PersistedScriptFile;
import cms.sre.persistenceapi.util.BucketHandler;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3PersistedScriptFileService {

    @Autowired
    private BucketHandler bucketHandler;

    private Logger logger = LoggerFactory.getLogger(S3PersistedScriptFileService.class);

    private byte[] getContents(String s3Location){
        return bucketHandler.getFileFromBucket(s3Location);
    }

    //UPDATE FILES
    /*
    /**
     * This Method Hydrates our scripts
     * @param scriptFile
     * @return
     */
    /*private ScriptFile getFullyRealizedScriptFile(ScriptFile scriptFile) {
        ScriptFile ret = scriptFile;
        if (scriptFile instanceof S3PersistedScriptFile) {

            S3PersistedScriptFile s3PersistedScriptFile = (S3PersistedScriptFile) scriptFile;

            String loc = s3PersistedScriptFile.getS3Location();

            s3PersistedScriptFile.setBinaryFile(this.getContents(loc));

            ret = s3PersistedScriptFile;

        }
        return ret;
    }
    */
    //------------------------------------------------ PULLING FROM S3--------------------------------------------------
    public void hydrateScripts(MongoPersistedSystem persistedSystem){
        String sysName = persistedSystem.getName();
        String sysOwner = persistedSystem.getOwner();
        String searchLoc = sysName + sysOwner + "/";

        List<ScriptFile> scripts = getScriptsFromSystem(persistedSystem);
        //Grabs all the scripts of the System and then updates their binary content.
        for (ScriptFile scriptFile : scripts){
            String location = searchLoc + scriptFile.getFilename();
            try{
                if(bucketHandler.doesFileExist(searchLoc + scriptFile.getFilename())){

                    S3PersistedScriptFile persistedEquivalent = convertFromFileinS3(location);

                    scriptFile.setBinaryFile(persistedEquivalent.getBinaryFile());
                }
                else{
                    throw new AmazonS3Exception("Could not find File:"  + scriptFile.getFilename() + "at location:" + location);
                }
            }
            catch(AmazonS3Exception e){
                e.printStackTrace();
            }


        }
        putScriptsInSystem(persistedSystem, scripts);

    }


    private S3PersistedScriptFile convertFromFileinS3(String s3Location) {

        S3PersistedScriptFile persistedScriptFile = new S3PersistedScriptFile();

        String[] splitLocation = s3Location.split("/");
        int lastElement = splitLocation.length - 1 ;

        if(bucketHandler.doesFileExist(s3Location)){
           persistedScriptFile
                   .setS3Location(s3Location)
                   .setBinaryFile(bucketHandler.getFileFromBucket(s3Location))
                   .setFilename(splitLocation[lastElement]);
            return persistedScriptFile;
        }
        else{
            return null;
        }
    }

    private void putScriptsInSystem(MongoPersistedSystem persistedSystem, List<ScriptFile> scriptFiles){

        int count = 0;
        for(Toaster toaster : persistedSystem.getToasters()){

            toaster.getPackerScript().setScriptFile(scriptFiles.get(count));
            count++;
            toaster.getTerraformScript().setVariableScript(scriptFiles.get(count));
            count++;
            toaster.getTerraformScript().setMainScript(scriptFiles.get(count));
            count++;
            toaster.getTerraformScript().setDataSourcesScript(scriptFiles.get(count));
            count++;
            toaster.getTerraformScript().setProviderScript(scriptFiles.get(count));
            count++;
        }

    }
    //--------------------------------------------PUSHING TO S3---------------------------------------------------------
    /**
     * Method for Inputting to S3
     *
     * @return
     */
    public boolean persistScriptsInS3(System system) {

        boolean ret = false;

        List<File> fileList = pullByteData(system);

        for (File file : fileList) {
            ret = bucketHandler.putObjectInBucket(file, file.getPath());
        }

        return ret;
    }

    private File convertToFile(ScriptFile scriptFile, String fileName) {
        File file = new File(fileName + "/" + scriptFile.getFilename());
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(scriptFile.getBinaryFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private List<File> pullByteData(System sys) {

        List<File> fileList = new ArrayList<>();


        //Iterates through each toaster registered to the System
        List<ScriptFile> scripts = getScriptsFromSystem(sys);

            //takes each set of toasters and writes a new file for each Script File
            for (ScriptFile scriptFile : scripts) {
                fileList.add(convertToFile(scriptFile, sys.getName() + sys.getOwner()));
            }

        return fileList;
    }

    private List<ScriptFile> getScriptsFromSystem(System system){

        List<ScriptFile> scripts = new ArrayList<>();
        for (Toaster toaster : system.getToasters()) {

            scripts.add(toaster
                    .getPackerScript()
                    .getScriptFile()
            );

            scripts.add(toaster
                    .getTerraformScript()
                    .getVariableScript()
            );

            scripts.add(toaster
                    .getTerraformScript()
                    .getMainScript()
            );

            scripts.add(toaster
                    .getTerraformScript()
                    .getDataSourcesScript()
            );

            scripts.add(toaster
                    .getTerraformScript()
                    .getProviderScript()
            );
        }
        return scripts;
    }


}
