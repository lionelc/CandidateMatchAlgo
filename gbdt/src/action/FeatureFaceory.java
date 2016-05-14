package action;

import java.util.List;

import util.Sample;

public class FeatureFaceory {

	public static List<Integer> sampleFeature(int featureNumber, double rate) {
		return Sample.sampling(featureNumber, rate);
	}
}
