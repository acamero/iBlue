package com.iblue.queue;

import com.iblue.model.SpotInterface;

public interface SpotQueueInterface {

	public boolean send(SpotInterface spot);
	
	public SpotInterface receive();
	
}
