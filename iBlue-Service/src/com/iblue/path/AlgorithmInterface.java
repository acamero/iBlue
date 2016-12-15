package com.iblue.path;

import java.util.List;

public interface AlgorithmInterface {
	public void setEdges(List<? extends EdgeInterface> edges);
	public List<? extends EdgeInterface> getPath(VertexInterface origin, VertexInterface destination);
}
