package com.iblue.queue.mq.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.codehaus.jackson.map.ObjectMapper;

import com.iblue.model.SpotDAOInterface;
import com.iblue.model.SpotInterface;
import com.iblue.model.db.Spot;
import com.iblue.model.db.dao.SpotDAO;
import com.iblue.model.msg.SpotJSON;
import com.iblue.queue.mq.QueueConfiguration;
import com.iblue.queue.mq.QueueConnection;
import com.iblue.utils.Log;
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
					Log.info("SPOT: " + spot.toString());

					SpotDAOInterface spotDAO = new SpotDAO();
					if (spot.getStatus() == 1) {
						SpotInterface tmp = spotDAO.persist(spot);
						Log.info("Spot created (id=" + tmp.getId() + ")");
					} else {
						Spot tmp = (Spot)spotDAO.getSpot(spot);
						if (tmp != null) {
							tmp.setStatus(spot.getStatus());
							tmp = (Spot)spotDAO.update(tmp);
							if (tmp != null) {
								Log.info("Spot updated (id=" + tmp.getId() + ")");
							} else {
								Log.warning("Something weird happened");
							}
						} else {
							Log.info("Could not find the spot");
						}
					}
				}
			};

			consumerTags.add(channel.basicConsume(QueueConfiguration.SPOTS_QUEUE_NAME, true, consumer));
			Log.info("Consumer set");
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
