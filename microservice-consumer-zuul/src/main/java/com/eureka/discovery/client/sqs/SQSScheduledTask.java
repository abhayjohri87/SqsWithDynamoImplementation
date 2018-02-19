package com.eureka.discovery.client.sqs;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.eureka.discovery.client.config.PropertiesCacheConfig;
import com.eureka.discovery.client.dynamodb.repositories.EmployeeRepository;
import com.eureka.discovery.client.vo.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class TSCDataSenderScheduledTask.
 * 
 * Sends the aggregated Vehicle data to TSC in batches
 */
@EnableScheduling
@Component("sqsScheduledTask")
@DependsOn({ "propertiesCacheConfig", "amazonSQSClient" })
public class SQSScheduledTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(SQSScheduledTask.class);
	@Autowired
	private PropertiesCacheConfig propertiesCacheConfig;
	@Autowired
	private AmazonSQSAsync amazonSQSClient;
	@Autowired
	private EmployeeRepository employeeRepository;
	/**
	 * Timer Task that will run after specific interval of time Majorly
	 * responsible for sending the data in batches to TSC.
	 */
	private String queueUrl;
	private final ObjectMapper mapper = new ObjectMapper();

	@PostConstruct
	public void initialize() throws Exception {
		LOGGER.info("SQS-Publisher", "Publisher initializing for queue " + propertiesCacheConfig.getSQSQueueName(),
				"Publisher initializing for queue " + propertiesCacheConfig.getSQSQueueName());
		// Get queue URL
		final GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName(propertiesCacheConfig.getSQSQueueName());
		final GetQueueUrlResult response = amazonSQSClient.getQueueUrl(request);
		queueUrl = response.getQueueUrl();

		LOGGER.info("SQS-Publisher", "Publisher initialized for queue " + propertiesCacheConfig.getSQSQueueName(),
				"Publisher initialized for queue " + propertiesCacheConfig.getSQSQueueName() + ", URL = " + queueUrl);
	}

	@Scheduled(fixedDelayString = "${sqs.consumer.delay}")
	public void timerTask() {

		final ReceiveMessageResult receiveResult = getMessagesFromSQS();
		String messageBody = null;
		if (receiveResult != null && receiveResult.getMessages() != null && !receiveResult.getMessages().isEmpty()) {
			for (Message message : receiveResult.getMessages()) {
				try {
					messageBody = message.getBody();
					String messageReceiptHandle = message.getReceiptHandle();
					Employee employee = mapper.readValue(messageBody, Employee.class);
					processMessage(employee, messageReceiptHandle);
				} catch (Exception e) {
					LOGGER.error("Exception while processing SQS message : {}", messageBody);
					// Message is not deleted on SQS and will be processed again
					// after visibility timeout
				}
			}
		}
	}

	public void processMessage(Employee employee,String messageReceiptHandle) throws InterruptedException {
		//delete the sqs message as the processing is completed
		employeeRepository.save(employee);
		amazonSQSClient.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
	}

	private ReceiveMessageResult getMessagesFromSQS() {
		try {
			// Create new request and fetch data from Amazon SQS queue
			final ReceiveMessageResult receiveResult = amazonSQSClient
					.receiveMessage(new ReceiveMessageRequest().withMaxNumberOfMessages(1).withQueueUrl(queueUrl));
			return receiveResult;
		} catch (Exception e) {
			LOGGER.error("Error while fetching data from SQS", e);
		}
		return null;
	}

}
