package com.filetransformer.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.filetransformer.service.AmazonSqsClientService;
import com.filetransformer.service.AmazonS3ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * service layer to make s3 and sqs calls, validation
 *
 */
@Component
public class AmazonS3ClientServiceImpl implements AmazonS3ClientService {
    private static final Logger logger = LoggerFactory.getLogger(AmazonS3ClientServiceImpl.class);
    private String awsS3AudioBucket;
    private AmazonS3 amazonS3;

    @Autowired
    public AmazonS3ClientServiceImpl(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider, @Value("${aws.s3.audio.bucket}") String awsS3AudioBucket) {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion.getName()).build();
        this.awsS3AudioBucket = awsS3AudioBucket;
    }

    /**
     * Upload file to s3 bucket
     */
    public void uploadFileToS3Bucket(String fileName, File file) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3AudioBucket, fileName, file);
            this.amazonS3.putObject(putObjectRequest);
            file.delete();
        } catch (AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ", ex);
        }
    }

    /**
     * Get Data from S3 Bucket
     * @param bucketName
     * @param fileName
     * @return
     */
    @Override
    public S3Object getData(String bucketName, String fileName) {
        return  amazonS3.getObject(bucketName, fileName);
    }

}
