package com.filetransformer.service.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.filetransformer.service.AmazonS3ClientService;
import com.filetransformer.service.AmazonSqsClientService;
import com.filetransformer.service.FileTransformerService;
import com.filetransformer.utility.CSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This service will be transformed data from csv to json, push json file to S3 and push message to SQS queue
 */
@Service
public class FileTransformerServiceImpl implements FileTransformerService {
    private static final Logger log = LoggerFactory.getLogger(FileTransformerServiceImpl.class);

    private final AmazonS3ClientService amazonS3ClientService;
    private final AmazonSqsClientService amazonSqsClientService;
    private final String awsS3Bucket;
    @Autowired
    public FileTransformerServiceImpl(AmazonS3ClientService amazonS3ClientService, AmazonSqsClientService amazonSqsClientService, @Value("${aws.s3.audio.bucket}") String awsS3Bucket) {
        this.amazonS3ClientService = amazonS3ClientService;
        this.amazonSqsClientService = amazonSqsClientService;
        this.awsS3Bucket = awsS3Bucket;
    }

    /**
     * transform file from csv to json and push json file to s3
     * @param bucketName
     * @param fileName
     */
    @Override
    public void transformFile(String bucketName, String fileName) {
        try (S3Object s3Object = amazonS3ClientService.getData(bucketName, fileName); InputStream fis = s3Object.getObjectContent()) {
            String jsonFileName = fileName.replace(".csv", ".json");
            File root = new File("temp");
            if(!root.exists()){
                root.mkdirs();
            }
            File jsonFile = new File("temp/"+ jsonFileName);
            CSV csv = new CSV(true, ',', fis );
            List< String > fieldNames = null;
            if (csv.hasNext()) fieldNames = new ArrayList< >(csv.next());
            List <Map< String, String >> list = new ArrayList < > ();
            while (csv.hasNext()) {
                List < String > x = csv.next();
                Map < String, String > obj = new LinkedHashMap< >();
                for (int i = 0; i < fieldNames.size(); i++) {
                    obj.put(fieldNames.get(i), x.get(i));
                }
                list.add(obj);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(jsonFile, list);
            amazonS3ClientService.uploadFileToS3Bucket(jsonFileName, jsonFile);
            amazonSqsClientService.sendMessageToFileTransformerSqs(awsS3Bucket, jsonFileName);
        } catch (Exception e){
            log.error("Can't get file with bucketName {}, file name {}", bucketName, fileName, e);
        }
    }
}
