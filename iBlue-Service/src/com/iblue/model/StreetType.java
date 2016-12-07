package com.iblue.model;

public enum StreetType {

	WHITE_LINE(0, true), BLUE_LINE(1, false);
	
	private int typeId;
	private boolean free;
	
	private StreetType(int id, boolean free) {
		this.typeId = id;
		this.free = free;
	}
	
	public boolean isFree() {
		return this.free;
	}
	
	public int getTypeId() {
		return typeId;
	}
}
