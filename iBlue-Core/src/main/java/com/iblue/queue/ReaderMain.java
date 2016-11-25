package com.iblue.queue;

import com.iblue.queue.mq.reader.SpotReaderMQ;

public class ReaderMain {

	public static void main(String[] args) {
		SpotReaderMQ reader = new SpotReaderMQ();
		
		reader.startReceiving();
		System.out.println("Start");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reader.stopReceiving();
		System.out.println("Stop");
	}
}
