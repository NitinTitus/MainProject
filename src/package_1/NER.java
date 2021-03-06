package package_1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.Triple;

public class NER 
{
	public static List<IntPair> ner(String answer, String question_word, AbstractSequenceClassifier<CoreLabel> classifier) throws ClassCastException, ClassNotFoundException, IOException
	{
	        List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(answer);
	        List<IntPair> intpairs = new ArrayList<IntPair>();
	        for (Triple<String,Integer,Integer> trip : triples) 
	        {
	        	if(question_word.equalsIgnoreCase("who"))
	        	{
	        		if(trip.first.equalsIgnoreCase("person"))
	        			intpairs.add(new IntPair(trip.second,trip.third));
	        	}
	        	else if(question_word.equalsIgnoreCase("where"))
	        	{
	        		if(trip.first.equalsIgnoreCase("location"))
	        			intpairs.add(new IntPair(trip.second,trip.third));
	        	}
	        	else if(question_word.equalsIgnoreCase("which"))
	        	{
	        		if(trip.first.equalsIgnoreCase("organization"))
	        			intpairs.add(new IntPair(trip.second,trip.third));
	        	}
	        	else if(question_word.equalsIgnoreCase("how"))
	        	{
	        		if(trip.first.equalsIgnoreCase("money"))
	        			intpairs.add(new IntPair(trip.second,trip.third));
	        	}
	        	else if(question_word.equalsIgnoreCase("when"))
	        	{
	        		if(trip.first.equalsIgnoreCase("time") || trip.first.equalsIgnoreCase("date"))
	        			intpairs.add(new IntPair(trip.second,trip.third));
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
	        return intpairs;
	}
}
