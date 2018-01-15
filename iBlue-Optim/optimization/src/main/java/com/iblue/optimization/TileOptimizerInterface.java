package com.iblue.optimization;

import java.util.List;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.PointValuePair;

public interface TileOptimizerInterface {

	public PointValuePair optimize(int maxEval, InitialGuess initialGuess);

	public PointValuePair optimize(int maxEval);
	
	public List<String> getHistory();

}
