package package_1;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.dcoref.CoNLL2011DocumentReader.NamedEntityAnnotation;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;


public class PronounResolver {

	public static int pronoun(String paragraph,int sentence_no,int word_no,String phrase)
	{
		int flag=0;
		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
		Annotation document = new Annotation(paragraph);
		pipeline.annotate(document);
		
		Map<IntPair, Set<CorefMention>> global=null;
		Map<Integer,CorefChain> coref = document.get(CorefChainAnnotation.class);
		for(Map.Entry<Integer,CorefChain> m : coref.entrySet() )
		{
			Map<IntPair, Set<CorefMention>> test=m.getValue().getMentionMap();
			IntPair i = new IntPair(sentence_no,word_no);
			if(test.get(i)!=null)
			{
				global=test;
				break;
			}
		}
		if(global==null)
			return -1;
		for(Map.Entry<IntPair, Set<CorefMention>> m : global.entrySet())
		{
			for(CorefMention c : m.getValue())
			{
				if(Main.common_words(c.mentionSpan, phrase)==1)
				{
					flag=1;
					break;
				}
			}
		}
		return flag;
	}
	public static void find_pronoun(String sentence)
	{
		
	}
	public static void main(String[] args) 
	{
		int flag=pronoun("Barrack Obama is the president of America. He is a black guy. Nelson Mandela is from Africa. He is an amazing guy", 4, 1, "Nelson Mandela");
		System.out.println(flag);
	}

}
