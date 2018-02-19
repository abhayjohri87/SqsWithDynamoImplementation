package com.eureka.discovery.client.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;

/**
 * DynamoDBConfig class used to load the Dynamo DB config beans in spring container.
 */
@EnableDynamoDBRepositories(basePackages = "com.eureka.discovery.client.dynamodb.repositories", dynamoDBOperationsRef = "dynamoDBTemplate")
@Configuration
public class DynamoDBConfig {

	private static final Logger logger = LoggerFactory.getLogger(DynamoDBConfig.class);
	/** The dynamo db access key. */
	@Value("${dynamodb.accesskey:}")
	private String dynamoDbAccessKey;
	/** The dynamo db secret key. */
	@Value("${dynamodb.secretkey:}")
	private String dynamoDbSecretKey;
	/** The region name. */
	@NotEmpty
	@Value("${dynamodb.regionname}")
	private String regionName;
	/** The default capacity. */
	@Value("${dynamodb.table.capacity:50}")
	private long defaultCapacity;
	@Autowired
	private PropertiesCacheConfig propertiesCacheConfig;

	/**
	 * Method responsible to create amazon dynamodb client.
	 *
	 * @return AmazonDynamoDB
	 */
	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDBClient client;
		if (propertiesCacheConfig.isIamUser()) {
			client = new AmazonDynamoDBClient();
		} else {
			client = new AmazonDynamoDBClient(new BasicAWSCredentials(dynamoDbAccessKey, dynamoDbSecretKey));
		}
		client.withRegion(Regions.fromName(regionName));
		return client;
	}

	/**
	 * Method responsible to create DynamoDBOperations
	 *
	 * @return DynamoDBOperations
	 */
	@Bean
	public DynamoDBTemplate dynamoDBTemplate() {
		return new DynamoDBTemplate(amazonDynamoDB(), dynamoDBMapperConfig());
	}

	/**
	 * Creates Dynamo DB mapper config Bean.
	 *
	 * @return the dynamo DB mapper config
	 */
	@Bean
	public DynamoDBMapperConfig dynamoDBMapperConfig() {
		return new DynamoDBMapperConfig.Builder().withTableNameOverride(TableNameOverride.withTableNamePrefix("")).build();
	}

	/**
	 * Creates Dynamo DB mapper Bean.
	 *
	 * @return the dynamo DB mapper
	 */
	@Bean
	public DynamoDBMapper dynamoDBMapper() {
		return new DynamoDBMapper(amazonDynamoDB(), dynamoDBMapperConfig());
	}
}
