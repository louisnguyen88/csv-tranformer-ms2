package com.filetransformer.service;

import com.amazonaws.services.s3.model.S3Object;

import java.io.File;

public interface AmazonS3ClientService
{
	void uploadFileToS3Bucket(String fileName, File file);

	S3Object getData(String bucketName, String fileName);
}
