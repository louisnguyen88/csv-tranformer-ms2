package com.filetransformer;

import com.filetransformer.model.S3StorageMessage;
import com.filetransformer.service.FileTransformerService;
import com.filetransformer.service.impl.AmazonSqsClientServiceImpl;
import com.filetransformer.service.impl.AmazonSqsConsumerService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@SpringBootTest
public class AmazonSqsConsumerServiceTest {
    private FileTransformerService fileTransformerService;

    private AmazonSqsConsumerService amazonSqsConsumerService;

    @Before
    public void setUp() {
        fileTransformerService = mock(FileTransformerService.class);
        amazonSqsConsumerService = new AmazonSqsConsumerService(fileTransformerService);
    }

    @Test
    public void shouldCallFileTransformerWhenReceiveMessageFromSqs(){
        String bucketName = "bucket-name";
        S3StorageMessage s3StorageMessage = new S3StorageMessage();
        s3StorageMessage.setBucketName(bucketName);
        s3StorageMessage.setFileName("file-name");
        amazonSqsConsumerService.getMessageFromSqs(s3StorageMessage, "message");

        verify(fileTransformerService, times(1)).transformFile(eq(bucketName), eq("file-name"));
    }
}
