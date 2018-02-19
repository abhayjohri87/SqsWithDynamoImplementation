/**
 * 
 */
package com.eureka.discovery.client.config;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.retry.RetryPolicy.BackoffStrategy;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;

@Configuration
public class SQSConfiguration {

	/** The properties cache config. */
	@Autowired
	private PropertiesCacheConfig propertiesCacheConfig;

	@Bean
	public AmazonSQSAsync amazonSQSClient() {
		// Create Client Configuration
		ClientConfiguration clientConfig = new ClientConfiguration()
			.withMaxErrorRetry(5)
			.withConnectionTTL(10_000L)
			.withTcpKeepAlive(true)
			.withRetryPolicy(new RetryPolicy(
					null, 
				new BackoffStrategy() {					
					@Override
					public long delayBeforeNextRetry(AmazonWebServiceRequest req, 
							AmazonClientException exception, int retries) {
						// Delay between retries is 10s unless it is UnknownHostException 
						// for which retry is 60s
						return exception.getCause() instanceof UnknownHostException ? 60_000L : 10_000L;
					}
				}, 10, true));
		// Create Amazon client
		AmazonSQSAsync asyncSqsClient = null;
		if (propertiesCacheConfig.isIamUser()) {//uing I am role
			asyncSqsClient = new AmazonSQSAsyncClient(new InstanceProfileCredentialsProvider(true), clientConfig);
		} else {//using access key and secret key
			asyncSqsClient = new AmazonSQSAsyncClient(
					new BasicAWSCredentials("acesskey", "secretkey"));
		}
		final Regions regions = Regions.fromName(propertiesCacheConfig.getRegionName());
		asyncSqsClient.setRegion(Region.getRegion(regions));
		asyncSqsClient.setEndpoint(propertiesCacheConfig.getEndPoint());
		
		// Buffer for request batching
		final QueueBufferConfig bufferConfig = new QueueBufferConfig();
		// Ensure visibility timeout is maintained
		bufferConfig.setVisibilityTimeoutSeconds(20);
		// Enable long polling
		bufferConfig.setLongPoll(true);
		// Set batch parameters
//		bufferConfig.setMaxBatchOpenMs(500);
		// Set to receive messages only on demand
//		bufferConfig.setMaxDoneReceiveBatches(0);
//		bufferConfig.setMaxInflightReceiveBatches(0);
		
		return new AmazonSQSBufferedAsyncClient(asyncSqsClient, bufferConfig);
	}
	
	@Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

	
}
