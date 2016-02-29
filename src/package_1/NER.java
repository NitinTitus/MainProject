package package_1;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.Triple;

public class NER 
{
	public static IntPair ner(String answer, String question_word, AbstractSequenceClassifier<CoreLabel> classifier) throws ClassCastException, ClassNotFoundException, IOException
	{
	        List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(answer);
	        for (Triple<String,Integer,Integer> trip : triples) 
	        {
	        	if(question_word.equalsIgnoreCase("who"))
	        	{
	        		if(trip.first.equalsIgnoreCase("person"))
	        			return new IntPair(trip.second,trip.third);
	        	}
	        	else if(question_word.equalsIgnoreCase("where"))
	        	{
	        		if(trip.first.equalsIgnoreCase("location"))
	        			return new IntPair(trip.second,trip.third);
	        	}
	        	else if(question_word.equalsIgnoreCase("which"))
	        	{
	        		if(trip.first.equalsIgnoreCase("organization"))
	        			return new IntPair(trip.second,trip.third);
	        	}
	        }
	       /* for (Triple<String,Integer,Integer> trip : triples) 
	        {
	        	if(question_word.equalsIgnoreCase("where"))
	        	{
	        		if(trip.first.equalsIgnoreCase("organization"))
	        			return new IntPair(trip.second,trip.third);
	        	}
	        	else if(question_word.equalsIgnoreCase("which"))
	        	{
	        		if(trip.first.equalsIgnoreCase("location"))
	        			return new IntPair(trip.second,trip.third);
	        	}
	        }*/
	        return null;
	}
	public static void main(String args[]) throws ClassCastException, ClassNotFoundException, IOException
	{
		IntPair p = ner("Tokyo is the bleakest place in the world.","Which");
		System.out.println(p.get(0) + " " + p.get(1));
		
	}
}
