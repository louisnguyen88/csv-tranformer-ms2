package com.filetransformer.service.impl;

import com.filetransformer.model.S3StorageMessage;
import com.filetransformer.service.AmazonSqsClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AmazonSqsClientServiceImpl implements AmazonSqsClientService {

    public static final String QUEUE = "ms-file-transformer";
    private static final Logger log = LoggerFactory.getLogger(AmazonSqsClientServiceImpl.class);

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public AmazonSqsClientServiceImpl(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @Override
    public void sendMessageToFileTransformerSqs(String bucketName, String jsonFileName) {
        S3StorageMessage s3StorageMessage = new S3StorageMessage(UUID.randomUUID().toString(),bucketName, jsonFileName);
        log.info("Sending the message {} to the Amazon sqs {}.", s3StorageMessage, QUEUE);
        queueMessagingTemplate.convertAndSend(QUEUE, s3StorageMessage);
        log.info("Message {} sent successfully to the Amazon sqs {}.", s3StorageMessage, QUEUE);
    }
}
