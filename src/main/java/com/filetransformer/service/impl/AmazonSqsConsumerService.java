package com.filetransformer.service.impl;

import com.filetransformer.model.S3StorageMessage;
import com.filetransformer.service.FileTransformerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class AmazonSqsConsumerService {
    public static final String QUEUE = "ms-uploader";
    private static final Logger log = LoggerFactory.getLogger(AmazonSqsConsumerService.class);


    private final FileTransformerService fileTransformerService;

    @Autowired
    public AmazonSqsConsumerService(FileTransformerService fileTransformerService) {
        this.fileTransformerService = fileTransformerService;
    }

    // @SqsListener listens the message from the specified queue.
    // Here in this example we are printing the message on the console and message will be deleted from the queue once it is sucessfully delivered.
    @SqsListener(value = QUEUE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void getMessageFromSqs(S3StorageMessage message, @Header("MessageId") String messageId) {
        log.info("Received message= {} with messageId= {}", message, messageId);
        fileTransformerService.transformFile(message.getBucketName(), message.getFileName());
        log.info("Processed message= {} with messageId= {}", message, messageId);
    }
}
