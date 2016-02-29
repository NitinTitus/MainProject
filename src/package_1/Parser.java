package package_1;

import java.io.IOException;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.tagger.maxent.TTags;
import edu.stanford.nlp.util.IntPair;
import package_1.NER;

public class Parser {

	public int contains(String question_word,String sentence)
	{
		Boolean b = sentence.contains(question_word);
		if(b==true)
			return 1;
		else
			return 0;
	}
	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException 
	{
		MaxentTagger tagger = new MaxentTagger("C:/Users/Nitin/Downloads/Compressed/stanford-postagger-2015-04-20/models/english-left3words-distsim.tagger");
		String sample = "Sam is a nice guy";
		 
		// The tagged string
		System.out.println(tagger.getTagIndex("prp"));
		System.out.println(tagger.getTag(2));
		//TTags t = tagger.getTags();
		//System.out.println(t.getTag(0));
		 
		// Output the result
		 
		//System.out.println(tagged);
		NER tr = new NER();
		IntPair p = tr.ner("Tokyo is the bleakest place in the world.","Which");
		System.out.println(p.get(0) + " "  + p.get(1));
	}

}
