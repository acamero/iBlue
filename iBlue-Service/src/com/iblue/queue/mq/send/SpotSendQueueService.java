package com.iblue.queue.mq.send;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// import org.codehaus.jackson.map.ObjectMapper;

import com.iblue.model.SpotInterface;
import com.iblue.queue.SpotSendQueueInterface;
import com.iblue.queue.mq.QueueConfiguration;
import com.iblue.queue.mq.QueueConnection;
import com.rabbitmq.client.Channel;


public class SpotSendQueueService implements SpotSendQueueInterface {

	private static final SpotSendQueueService INSTANCE = new SpotSendQueueService();

	public static SpotSendQueueService getInstance() {
		return INSTANCE;
	}

	private SpotSendQueueService() {
	}

	public boolean send(SpotInterface spot) {
		try {
			Channel channel = QueueConnection.getQueueConnection().getConnection().createChannel();
			channel.queueDeclare(QueueConfiguration.SPOTS_QUEUE_NAME, false, true, false, null);
			channel.basicPublish("", QueueConfiguration.SPOTS_QUEUE_NAME, null, spot.toString().getBytes());
			// System.out.println(" [x] Sent '" + message + "'");
			channel.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}



}
