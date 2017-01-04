package com.iblue.model;

import java.math.BigDecimal;

public interface TileServiceInterface {

	public Tile getTile(BigDecimal latFrom, BigDecimal lonFrom, BigDecimal latTo, BigDecimal lonTo);

	public String updateMap();

}
