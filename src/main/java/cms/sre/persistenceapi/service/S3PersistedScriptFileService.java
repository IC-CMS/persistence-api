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

    private byte[] getContents(String s3Location) {
        return bucketHandler.getFileFromBucket(s3Location);
    }

    private boolean existsInS3(ScriptFile scriptFile) {
        return bucketHandler.doesFileExist(scriptFile);
    }

    //UPDATE FILES

    //HYDRATION METHOD
    public ScriptFile getFullyRealizedScriptFile(ScriptFile scriptFile) {
        ScriptFile ret = scriptFile;
        if (scriptFile instanceof S3PersistedScriptFile) {
            S3PersistedScriptFile s3PersistedScriptFile = (S3PersistedScriptFile) scriptFile;
            String loc = s3PersistedScriptFile.getS3Location();
            s3PersistedScriptFile.setBinaryFile(this.getContents(loc));
            ret = s3PersistedScriptFile;
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

    /**
     * Gateway method
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


    private List<File> pullByteData(System sys) {

        List<File> fileList = new ArrayList<>();

        ArrayList<ScriptFile> scripts = new ArrayList<>();
        //Iterates through each toaster registered to the System
        for (Toaster toaster : sys.getToasters()) {

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

            //takes each set of toasters and writes a new file for each Script File
            for (ScriptFile scriptFile : scripts) {
                fileList.add(convertToFile(scriptFile, sys.getName() + sys.getOwner()));
            }
        }
        return fileList;
    }


    /*private List<File> pullByteData(List<System> sysList){

        List<File> fileList = new ArrayList<>();

        //Iterates through each individual System from the given list
        for(int i = 0, len = sysList.size(); i < len; i++){
            ArrayList<ScriptFile> scripts = new ArrayList<>();
            //Iterates through each toaster registered to the System
            for(Toaster toaster : sysList.get(i).getToasters()){

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

                //takes each set of toasters and writes a new file for each Script File
                for(int lcv = 0, al = scripts.size(); lcv < al; lcv++){
                    fileList.add( convertToFile(scripts.get(lcv), sysList.get(i).getName() + sysList.get(i).getOwner()) );

                }
                scripts.clear();
            }
        }
        return fileList;

    }
    */
}
