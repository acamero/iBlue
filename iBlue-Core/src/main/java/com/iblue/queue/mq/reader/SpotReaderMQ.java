package com.iblue.queue.mq.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.codehaus.jackson.map.ObjectMapper;

import com.iblue.model.SpotDAOInterface;
import com.iblue.model.SpotInterface;
import com.iblue.model.db.SpotDAO;
import com.iblue.queue.mq.QueueConfiguration;
import com.iblue.queue.mq.QueueConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class SpotReaderMQ {

	private Channel channel = null;
	private List<String> consumerTags = new ArrayList<String>();

	public boolean startReceiving() {

		try {
			channel = QueueConnection.getQueueConnection().getConnection().createChannel();
			channel.queueDeclare(QueueConfiguration.SPOTS_QUEUE_NAME, false, false, false, null);
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					ObjectMapper mapper = new ObjectMapper();
					SpotInterface spot = mapper.readValue(message, SpotJSON.class);
					// System.out.println("SPOT: " + spot.toString());
					
					SpotDAOInterface spotDAO = new SpotDAO();
					if (spot.getStatus() == 0 && spot.getId() > 0) {
						SpotInterface tmp = spotDAO.update(spot);
						if (tmp != null) {
							System.out.println("Spot updated (id=" + tmp.getId() + ")");
						} else {
							System.out.println("Could not find the spot id=" + spot.getId());
						}
					} else {
						SpotInterface tmp = spotDAO.persist(spot);
						System.out.println("Spot created (id=" + tmp.getId() + ")");
					}
				}
			};

			consumerTags.add(channel.basicConsume(QueueConfiguration.SPOTS_QUEUE_NAME, true, consumer));
			System.out.println("Consumer set");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean stopReceiving() {
		if (channel != null) {
			try {
				for (String tag : consumerTags) {
					channel.basicCancel(tag);
				}
				channel.close();
			} catch (IOException | TimeoutException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
