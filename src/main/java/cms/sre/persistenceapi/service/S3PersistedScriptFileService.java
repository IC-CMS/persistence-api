package cms.sre.persistenceapi.service;

import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;
import cms.sre.dna_common_data_model.system.System;
import cms.sre.dna_common_data_model.system.Toaster;
import cms.sre.persistenceapi.model.MongoPersistedSystem;
import cms.sre.persistenceapi.model.S3PersistedScriptFile;
import cms.sre.persistenceapi.util.BucketHandler;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The service that handles most of the interaction of files being moved in and out of the S3 instance. This class contains logic
 * for stripping the system of its script files, the saving that data to a file, which is then sent to directories based
 * on the System's name and owner. Since S3 uses a bucket storage medium, the pathname variables of the file act as its
 * storage locaiton in S3.
 */
@Service
public class S3PersistedScriptFileService {

    @Autowired
    private BucketHandler bucketHandler;

    private Logger logger = LoggerFactory.getLogger(S3PersistedScriptFileService.class);

    /**
     * Internal method that returns the byte[] data that is stored in a file at a given location in S3
     * @param s3Location the location of the persisted file
     * @return the binary representation of the file that was stored
     */
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

    /**
     * The logic in this method revolves around finding the files from the given system. A location is generated from the
     * system's fields and then each script field from within the system is filled with the data stored in s3
     * @param persistedSystem the system that has been called by the GET methods to be returned with data
     * @throws AmazonS3Exception called if a file cannot be found at a location, indicating a storage issue
     */
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

    /**
     * Checks to see if the file exists in S3 at the given location, and returns it, otherwise returns null
     * @param s3Location the location of the file to be found
     * @return the script file with all relevant data or null
     */
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

    /**
     * method that handles the pushing of script files from S3 back to the system meant to be hydrated. The structure is
     * meant to mimic how the scripts were inserted into the API and pulled from the system to begin with
     * (feel free to provide an alternate solution)
     * @param persistedSystem the system meant to be hydrated
     * @param scriptFiles the list of files from the system that need to be inserted into the hydrated data structure
     */
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
     * @return the list of files that have been uploaded into S3
     */
    public boolean persistScriptsInS3(System system) {

        boolean ret = false;

        List<File> fileList = pullByteData(system);

        for (File file : fileList) {
            ret = bucketHandler.putObjectInBucket(file, file.getPath());
        }

        return ret;
    }

    /**
     * method meant to take the byte[] data from the System that needs to be persisted and converts it to a file
     * AmazonS3 works better in this case, as we are not storing objects but rather primitive data types. Although not the
     * preferred means of storage, we write this byte[] to a file
     * @param scriptFile the script file that needs to have it byte[] data removed
     * @param fileName the path name of the file so we can push the dat to S3
     * @return
     */
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

    /**
     * method that searches through a list of scripts provided by {@link #getScriptsFromSystem(System)} and pulls
     * this byte[] data fields
     * @param sys the system to be searched
     * @return the list of files that have been converted
     */
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

    /**
     * method intended to grab a list of script files from a system and add them to a list
     * @param system the system to be parsed through
     * @return the list of scripts the system contained
     */
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
