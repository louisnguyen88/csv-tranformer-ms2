package com.filetransformer.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AmazonSqsConfig {

	@Value("${aws.region}")
	private String region;

	@Value("${aws.access.key.id}")
	private String awsAccessKey;

	@Value("${aws.access.key.secret}")
	private String awsSecretKey;

	// @Bean annotation tells that a method produces a bean which is to be managed by the spring container.
	@Bean
	public QueueMessagingTemplate queueMessagingTemplate() {
		return new QueueMessagingTemplate(amazonSQSAsync());
	}

	@Bean
	@Primary
	// AmazonSQSAsync is an interface for accessing the SQS asynchronously. 
	// Each asynchronous method will return a Java Future object representing the asynchronous operation.
	public AmazonSQSAsync amazonSQSAsync() {
		return AmazonSQSAsyncClientBuilder
				.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
				.build();
	}
}
