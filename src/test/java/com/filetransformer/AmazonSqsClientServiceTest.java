package com.filetransformer;

import com.filetransformer.model.S3StorageMessage;
import com.filetransformer.service.AmazonSqsClientService;
import com.filetransformer.service.impl.AmazonSqsClientServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AmazonSqsClientServiceTest {
    private QueueMessagingTemplate queueMessagingTemplate;

    private AmazonSqsClientService amazonSqsClientService;

    @Before
    public void setUp() {
        queueMessagingTemplate = mock(QueueMessagingTemplate.class);
        amazonSqsClientService = new AmazonSqsClientServiceImpl(queueMessagingTemplate);
    }

    @Test
    public void shouldCallQueueMessagingTemplateToSendToUploadQueueWhenSendMessageToSqs(){
        amazonSqsClientService.sendMessageToFileTransformerSqs("bucket-name", "message");
        String queue = "ms-file-transformer";
        verify(queueMessagingTemplate, times(1)).convertAndSend(eq(queue), any(S3StorageMessage.class));
    }
}
