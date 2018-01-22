package com.iblue.optim;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

@SuppressWarnings("serial")
public class DefaultSolution extends DefaultDoubleSolution {

	public DefaultSolution(DoubleProblem problem) {
		super(problem);
		initializeVars();
	}
	
	public DefaultSolution(DoubleProblem problem, double[] initialSolution) {
		super(problem);
		for(int i=0;i<problem.getNumberOfVariables();i++) {
			setVariableValue(i,initialSolution[i]);
		}
	}

	public DefaultSolution(DefaultDoubleSolution solution) {
		super(solution);
	}

	
	private void initializeVars() {
		int tempPart = randomGenerator.nextInt(0, problem.getNumberOfVariables()/2);
		for (int i = 0; i < tempPart; i++) {
			double value = randomGenerator.nextDouble(getLowerBound(i), getUpperBound(i));
			setVariableValue(i, value);
		}
		for( int i=tempPart; i < problem.getNumberOfVariables()/2; i++) {
			setVariableValue(i, 0.0d);
		}
		tempPart = randomGenerator.nextInt(problem.getNumberOfVariables()/2, problem.getNumberOfVariables());
		for (int i = problem.getNumberOfVariables()/2; i < tempPart; i++) {
			double value = randomGenerator.nextDouble(getLowerBound(i), getUpperBound(i));
			setVariableValue(i, value);
		}
		for( int i=tempPart; i < problem.getNumberOfVariables(); i++) {
			setVariableValue(i, 0.0d);
		}
	}
}