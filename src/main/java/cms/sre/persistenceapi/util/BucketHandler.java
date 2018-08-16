package cms.sre.persistenceapi.util;

import cms.sre.persistenceapi.service.SystemPersistenceService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

/**
 * A Handler class created to simplify the logic behind Amazon S3's bucket storage
 */
public class BucketHandler {

    private String bucketName;
    private String regionName;
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
     * Creates the Bucket corresponding to the parameters of the autowired Handler
     */
    public void createBucket(){

            try{
                if(!this.s3.doesBucketExistV2(this.bucketName)){
                    this.s3.createBucket(this.bucketName);
                    logger.info("Bucket creation successful.");
                }
            }
            catch(Exception e){
                logger.error("Bucket creation unsuccessful.");
                e.printStackTrace();
            }
    }

    /**
     * method responsible for placing the file in the default bucket
     * @param file the file ot be uploaded
     * @param location the location to be placed at
     * @return boolean confirmation that the operation succeeded
     */
    public boolean putObjectInBucket(File file, String location){

        boolean ret = false;
        //Write the Script File's Byte data to a file
        //assign the file a location in the bucket
        //put the file in new location

        if(!this.s3.doesBucketExistV2(this.bucketName)){
            logger.info("Upload Unsuccessful: Bucket does not exist.");
        }
        else{
            try{
                this.s3.putObject(this.bucketName, location, file);
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
     * Method in charge of 
     * @param location The location of the File
     * @return A copy of the file
     */
    public byte[] getFileFromBucket(String location){

        byte[] bytes = null;
        S3Object object = this.s3.getObject(bucketName,  location);

        try{
            bytes = IOUtils.toByteArray(object.getObjectContent());
        }
        catch(IOException e){
                e.printStackTrace();
        }

        return bytes;
    }

    /**
     * Indexes the Entire Bucket to see if the request file exists
     * @param location- location of the file
     * @return boolean confirmation if the file exists and the given location
     */
    public boolean doesFileExist(String location){
        boolean ret = false;


            if(this.s3.doesObjectExist(bucketName,  location)){
                ret = true;
            }


        return ret;
    }

    /**
     * Deletes the file from the Bucket at the given location
     * @param location- the location of the file to be deleted
     */
    public void deleteFile(String location){

        this.s3.deleteObject(bucketName, location);

    }

}
