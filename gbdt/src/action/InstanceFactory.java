package action;

import java.util.ArrayList;
import java.util.List;

import entity.Instance;

import util.Sample;

public class InstanceFactory {

	public static List<Integer> sampleFeature(int instanceNumber, double rate) {
		return Sample.sampling(instanceNumber, rate);
	}
	
	public static Instance getInstance(String line) {
		String[] args = line.split(" ");
		List<Double> features = new ArrayList<Double>();
		for (int i = 1; i < args.length; ++i) {
			String[] split = args[i].split(":");
			if (split.length > 0) {
				features.add(Double.parseDouble(split[1]));
			}
			else {
				features.add(Double.parseDouble(split[0]));
			}
		}
		Instance ins = new Instance(features, Double.parseDouble(args[0]));
		return ins;
	}
}
