package com.iblue.path;

import java.util.LinkedList;

import com.iblue.model.IntersectionInterface;

public interface AlgorithmInterface {
	
	public void setGraph(GraphInterface graph);
	public LinkedList<IntersectionInterface> getPath(IntersectionInterface from, IntersectionInterface to);
	
}
