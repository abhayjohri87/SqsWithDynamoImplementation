package com.eureka.discovery.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesCacheConfig {

	
	/** The is iam user. */
	@Value("${iam.user}")
	private boolean isIamUser;
	@Value("${sqs.regionname}")
	private String regionName;
	
	@Value("${sqs.endpoint}")
	private String endPoint;
	
	@Value("${sqs.queuename}")
	private String sqsQueueName;

	/**
	 * Checks if is iam user.
	 *
	 * @return true, if is iam user
	 */
	public boolean isIamUser() {
		return isIamUser;
	}

	
	public String getRegionName() {
		return regionName;
	}
	
	public String getEndPoint() {
		return endPoint;
	}
	
	public String getSQSQueueName() {
		return sqsQueueName;
	}
}
