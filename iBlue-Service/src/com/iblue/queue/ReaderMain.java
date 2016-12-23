package com.iblue.queue;

import com.iblue.queue.mq.reader.SpotReaderMQ;
import com.iblue.utils.Log;

public class ReaderMain {

	public static void main(String[] args) {
		boolean test = false;
		
		if(test) {
			test();
		} else {
			deploy();
		}
	}

	private static void test() {
		SpotReaderMQ reader = new SpotReaderMQ();

		reader.startReceiving();
		Log.info("Start");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Log.error(e.getMessage());
		}
		reader.stopReceiving();
		Log.info("Stop");
		
	}
	 
	private static void deploy() {
		SpotReaderMQ reader = new SpotReaderMQ();

		Log.info("Start reading queue");
		reader.startReceiving();		
		
	}
	

}
