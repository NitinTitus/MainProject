package package_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.Triple;
import package_1.Summarization;

public class Main {
	static String query;
	static String passage;
	static String user_answer;
	static String sentences[];
	static String pronoun_list[]={"I","you","she","he","it","we","they"};
	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException 
	{
		String serializedClassifier1 = "C:/Users/Nitin/Downloads/Compressed/stanford-ner-2014-06-16/stanford-ner-2014-06-16/classifiers/english.muc.7class.distsim.crf.ser.gz";
		String serializedClassifier2 = "C:/Users/Nitin/Downloads/Compressed/stanford-ner-2014-06-16/stanford-ner-2014-06-16/classifiers/english.all.3class.distsim.crf.ser.gz";
		AbstractSequenceClassifier<CoreLabel> classifier1 = CRFClassifier.getClassifier(serializedClassifier1),classifier;
		AbstractSequenceClassifier<CoreLabel> classifier2 = CRFClassifier.getClassifier(serializedClassifier2);
		BufferedReader bufferedReader = new BufferedReader(new FileReader("file.txt"));
		query=bufferedReader.readLine();
		passage=bufferedReader.readLine();
		user_answer=bufferedReader.readLine();
		query=query.replaceAll("\\?", "");
		sentences = passage.split("\\.");
		for(int i=0;i<sentences.length;i++)
		{
			sentences[i]=sentences[i].replaceAll(",|;", "");
		}
		int sentence_no=Summarization.main(query, passage, sentences);
		String answer=sentences[sentence_no];
		String question_words[] = {"Who","Which","Where","How","When"},question_word = null;
		String[] words = query.split("\\s+");
		for(String word: words)
		{
			for(String s : question_words)
			{
				if(word.equalsIgnoreCase(s))
				{
					question_word=s;
					break;
				}
			}
		}
		List<IntPair> p;
		if(question_word.equalsIgnoreCase("Who") || question_word.equalsIgnoreCase("Which") || question_word.equalsIgnoreCase("Where"))
		{
			p = NER.ner(answer, question_word,classifier2);
			classifier=classifier2;
		}
		else
		{
			p = NER.ner(answer, question_word,classifier1);
			classifier=classifier1;
		}
		//IntPair p1=NER.ner(user_answer, question_word,classifier);
		String pronoun=null;
		int count=0;
		float similarity=0;
		if(p.size()==0)
		{
			words=answer.split("\\s+");
			for(String word: words)
			{
				count++;
				for(String pr: pronoun_list)
				{
					if(pr.equalsIgnoreCase(word))
					{
						pronoun=pr;
						break;
					}
				}
				if(pronoun!=null)
					break;
			}
			//String user_answer_phrase = user_answer.substring(p1.get(0), p1.get(1));
			similarity=PronounResolver.pronoun(passage, sentence_no + 1, count - 1, user_answer);
			if(similarity==-1 || similarity==0)
			{
				similarity=common_words(find_noun(sentences, sentence_no, count-1, classifier2),user_answer);
			}
		}
		else
		{
			String candidate_answers[]=new String[p.size()],final_answer,answer_phrase;
			if(candidate_answers.length==1)
			{
				answer_phrase=answer.substring(p.get(0).get(0), p.get(0).get(1));
			}
			else
			{
				for(int i=0;i<p.size();i++)
				{
					candidate_answers[i] = answer.substring(p.get(i).get(0), p.get(i).get(1));
				}
				String candidates[]=eliminate_candidates(query, candidate_answers, question_word, classifier);
				if(candidates.length==1)
				{
					answer_phrase=candidates[0];
				}
				else
				{
					answer_phrase = Demo.init(answer, sentence_no, answer, candidates,query,passage);
				}
			}
			similarity=common_words(answer_phrase, user_answer);
		}
		System.out.println(similarity);
		if(similarity==1)
			System.out.println("Correct answer");
		else
			System.out.println("Wrong answer");
	}
	public static float find_similarity(String a,String b)
	{
		String words1[]=a.split("\\s+");
		String words2[]=b.split("\\s+");
		float common_words=0;
		for(String p: words1)
		{
			for(String q: words2)
			{
				if(p.equalsIgnoreCase(q))
					common_words++;
			}
		}
		return ((2*common_words)/(words1.length+words2.length));
	}
	public static String find_noun(String sentences[], int sentence_no, int word_no, AbstractSequenceClassifier<CoreLabel> classifier)
	{
		String sentence = sentences[sentence_no];
		String words[]=sentence.split("\\s+");
		String pronoun=words[word_no];
		IntPair j=null;
		for(int i = sentence_no-1;i>=0;i--)
		{
			List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(sentences[i]);
			for(Triple<String,Integer,Integer> triple : triples)
			{
				if(pronoun.equalsIgnoreCase("he")||pronoun.equalsIgnoreCase("she")||pronoun.equalsIgnoreCase("you")||pronoun.equalsIgnoreCase("I"))
				{
					if(triple.first.equalsIgnoreCase("person"))
					{
						j=new IntPair(triple.second,triple.third);
						sentence_no=i;
						break;
					}
				}
				else if(pronoun.equalsIgnoreCase("it"))
				{
					if(triple.first.equalsIgnoreCase("location")||triple.first.equalsIgnoreCase("organization"))
					{
						j=new IntPair(triple.second,triple.third);
						sentence_no=i;
						break;
					}
				}
			}
			if(j!=null)
				break;
		}
		return sentences[sentence_no].substring(j.get(0),j.get(1));
	}
	public static int common_words(String a, String b)
	{
		String words1[]=a.split("\\s+");
		String words2[]=b.split("\\s+");
		int common_word_cnt=0;
		for(String p : words1)
		{
			for(String q: words2)
			{
				if(p.equalsIgnoreCase(q))
				{
					common_word_cnt++;
					break;
				}
			}
		}
		if(words1.length==1)
		{
			if(common_word_cnt==1)
				return 1;
			else 
				return 0;
		}
		if(common_word_cnt>=(words1.length/2))
			return 1;
		else
			return 0;
	}
	public static String[] eliminate_candidates(String query, String candidate_answers[],String question_word, AbstractSequenceClassifier<CoreLabel> classifier) throws ClassCastException, ClassNotFoundException, IOException
	{
		List<IntPair> p;
		int flag;
		p=NER.ner(query, question_word, classifier);
		String candidate_answer;
		ArrayList<String> newlist = new ArrayList<String>();
		for(int i=0;i<candidate_answers.length;i++)
		{
			flag=0;
			for(int j=0;j<p.size();j++)
			{
				candidate_answer=query.substring(p.get(j).get(0),p.get(j).get(1));
				if(candidate_answer.equalsIgnoreCase(candidate_answers[i]))
				{
					flag=1;
					break;
				}
			}
			if(flag==0)
			{
				newlist.add(candidate_answers[i]);
			}
		}
		String new_candidate_list[]= new String[newlist.size()];
		for(int i=0;i<newlist.size();i++)
		{
			new_candidate_list[i]=newlist.get(i);
		}
		return new_candidate_list;
	}
}
