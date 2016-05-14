package util;

import java.util.List;

import entity.Corpus;
import entity.Parameter;
import entity.Tree;

public abstract class LossFunction {
	
	abstract public double setLossFunctionValue(Tree t, Corpus corpus);	

	abstract public void minLossFunction(Tree t, List<Integer> featureSampleList,
			Corpus corpus, Parameter p);
	
}
