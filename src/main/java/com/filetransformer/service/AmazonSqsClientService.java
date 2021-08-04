package com.filetransformer.service;

public interface AmazonSqsClientService {
    void sendMessageToFileTransformerSqs(String bucketName, String fileName);
}
