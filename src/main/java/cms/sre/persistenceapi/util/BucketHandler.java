package cms.sre.persistenceapi.util;

import cms.sre.dna_common_data_model.hashicorpFile.ScriptFile;
import cms.sre.dna_common_data_model.system.System;
import cms.sre.persistenceapi.service.SystemPersistenceService;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BucketHandler {

    private String bucketName;
    private String regionName;
    //unsure if this should be an instance variable
    private Logger logger = LoggerFactory.getLogger(BucketHandler.class);

    @Autowired
    private AmazonS3 s3;

    @Autowired
    private SystemPersistenceService service;

    public BucketHandler(String bucketName,String regionName){

        this.bucketName = bucketName;
        this.regionName = regionName;

    }

    public String getBucketName() {
        return bucketName;
    }

    public BucketHandler setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public BucketHandler setRegionName(String regionName) {
        this.regionName = regionName;
        return this;
    }

    public String getRegionName() {
         return regionName;
    }

    /**
     * Creates the Bucket corresponding ot the parameters of the Handler
     */
    public void createBucket(){

            try{
                if(!s3.doesBucketExistV2(this.bucketName)){
                    s3.createBucket(this.bucketName);
                    logger.info("Bucket creation successful.");
                }
            }
            catch(Exception e){
                logger.info("Bucket creation unsuccessful.");
                e.printStackTrace();
            }
    }

    public boolean putObjectInBucket(File file, String location){

        boolean ret = false;
        //Write the Script File's Byte data to a file
        //assign the file a location in the bucket
        //put the file in new location

        if(!s3.doesBucketExistV2(this.bucketName)){
            logger.info("Upload Unsuccessful: Bucket does not exist.");
        }
        else{
            try{
                s3.putObject(this.bucketName, location, file);
                ret = true;
            }
            catch(Exception e){
                e.printStackTrace();
                ret = false;
            }

        }
        return ret;
    }

    /**
     *
     * @param location The location of the File
     * @return A copy of the file
     * @throws Exception
     */
    public byte[] getFileFromBucket(String location){

        byte[] bytes = null;
        S3Object object = s3.getObject(bucketName,  location);

        try{

            bytes = IOUtils.toByteArray(object.getObjectContent());
        }
        catch(IOException e){

        }

        return bytes;
    }

    /**
     * Indexes the Entire Bucket to see if the request file exists
     * @param scriptFile
     * @return
     */
    public boolean doesFileExist(ScriptFile scriptFile){
        boolean ret = false;

        List<System> systems = service.getSystems();

        for(System system : systems){
            if(s3.doesObjectExist(bucketName,  system.getName() + system.getOwner() + "/" + scriptFile.getFilename()) ||
                    scriptFile.getBinaryFile().equals(getFileFromBucket(system.getName() + system.getOwner() + "/" + scriptFile.getFilename()))){
                ret = true;
            }
        }

        return ret;
    }
    public void deleteFile(String location){

        s3.deleteObject(bucketName, location);

    }





}
