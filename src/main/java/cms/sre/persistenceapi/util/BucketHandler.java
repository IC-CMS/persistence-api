package cms.sre.persistenceapi.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;

public class BucketHandler {

    private String bucketName;
    private String regionName;
    //unsure if this should be an instance variable
    private Logger logger = LoggerFactory.getLogger(BucketHandler.class);

    @Autowired
    AmazonS3 s3;

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

    public void putObjectInBucket(File file){
        if(!s3.doesBucketExistV2(this.bucketName)){
            logger.info("Upload Unsuccessful: Bucket does not exist.");
        }
        else{
            try{
                s3.putObject(this.bucketName, "Key", file);
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }



    }





}
