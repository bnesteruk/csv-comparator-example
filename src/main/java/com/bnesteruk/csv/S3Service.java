package com.bnesteruk.csv;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.IOException;

public class S3Service {

    private final AmazonS3 s3Client;

    /**
     * See `src/main/resources/config.properties`
     * @param accessKey the key of IAM user having access to the S3 bucket
     * @param secretKey the secret part
     * @param region - bucket region
     */
    public S3Service(String accessKey, String secretKey, Regions region) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }

    /*
     * Method to get S3 object as an InputStream
     */
    public S3ObjectInputStream getS3ObjectInputStream(String bucketName, String key) {
        S3Object s3Object = s3Client.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }

    public void closeInputStream(S3ObjectInputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                System.err.println("Error closing input stream: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
