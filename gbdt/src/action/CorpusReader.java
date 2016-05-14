package action;

import java.io.IOException;

import util.IOReader;
import entity.Corpus;

public class CorpusReader {
	
	
	public static Corpus readCorpus(String file) throws IOException {
		IOReader reader = new IOReader(file);
		Corpus corpus = new Corpus();
		String line = null;
		while ((line = reader.readLine()) != null) {
			corpus.addInstance(InstanceFactory.getInstance(line));
		}
		corpus.setFeatureSize(corpus.getInstance(0).getFeatureSize());
		return corpus;
	}
}
