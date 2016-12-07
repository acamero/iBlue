package com.iblue.queue.mq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueConnection {
	
	private static final QueueConnection INSTANCE = new QueueConnection();
	private Connection connection;

	private QueueConnection() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(QueueConfiguration.SPOTS_HOST);

		try {
			connection = factory.newConnection();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	public static QueueConnection getQueueConnection() {
		return INSTANCE;
	}
	
	public Connection getConnection() {
		return connection;
	}
}
