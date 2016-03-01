package package_1;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.tartarus.snowball.ext.PorterStemmer;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;




public class Summarization {
	static int flag;
	static float score1,score2,score3,final_score;
	static String str;
	public static int main(String query,String passage,String sentences[])
	{
		Scanner input = new Scanner(System.in);
		sentences = passage.split("\\.");
		ArrayList<String> queryList = stop_word_elimination(tokenizer(query));
		ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
		ArrayList<Float> score = new ArrayList<Float>();
		float max_score,temp;
		int index=0;
		for(String s : sentences)
		{
			arrayList.add((stop_word_elimination(tokenizer(s))));
		}
		for(int i=0;i<arrayList.size();i++)
		{
			score1=word_count_similarity(queryList, arrayList.get(i));
			score2=n_gram_similarity(queryList, arrayList.get(i));
			score3=semantic_similarity(queryList, arrayList.get(i));
			/*final_score= (float) (0.4 * score1 + 0.4* score2 + 0.2 * score3);*/
			final_score=score1+score2+score3;
			System.out.println(final_score);
			score.add(final_score);
		}
		max_score=score.get(0);
		Iterator<Float> iterator = score.iterator();
		int i=0;
		while(iterator.hasNext())
		{
			if((temp=iterator.next()) > max_score)
			{
				max_score = temp;
				index=i;
			}
			i++;
		}
		iterator=score.iterator();
		i=0;
		int min_length=sentences[index].length();
		while(iterator.hasNext())
		{
			if((temp=iterator.next())==max_score)
			{
				if(sentences[i].length()<min_length)
				{
					min_length=sentences[i].length();
					index=i;
				}
			}
			i++;
		}
		return index;
		
	}
	public static StringTokenizer tokenizer(String str)
	{
		StringTokenizer stringTokenizer =  new StringTokenizer(str);
		return stringTokenizer;
	}
	public static ArrayList<String> stop_word_elimination(StringTokenizer stringTokenizer)
	{
		String StopWords[] = {"but", "be", "with", "such", "then", "for", "no", "will", "are", "and", "their", "if", "this", "on", "into", "a", "or", "there", "in", "that", "they", "was", "is", "it", "an", "the", "as", "at", "these", "by", "to", "of", "an"};
		ArrayList<String> arrayList = new ArrayList<String>();
		while(stringTokenizer.hasMoreTokens())
		{
			str= stringTokenizer.nextToken();
			flag=0;
			for(String s : StopWords)
			{
				if(s.equalsIgnoreCase(str))
				{
					flag=1;
					break;
				}
			}
			if(flag==0)
			{
				arrayList.add(str);
			}
		}
		return arrayList;
	}
	public static ArrayList<String> stemmer(ArrayList<String> arrayList)
	{
		PorterStemmer porterStemmer = new PorterStemmer();
		for(int i=0;i<arrayList.size();i++)
		{
			porterStemmer.setCurrent(arrayList.get(i));
			porterStemmer.stem();
			arrayList.set(i, porterStemmer.getCurrent());
		}
		return arrayList;
	}
	public static float word_count_similarity(ArrayList<String> queryList, ArrayList<String> arrayList)
	{
		float common_words=0;
		float common_words_length=0,query_length=0,sentence_length=0;
		for(String q : queryList)
		{
			query_length+=q.length();
		}
		for(String a : arrayList)
		{
			sentence_length+=a.length();
		}
		for(String q : queryList)
		{
			for(String a : arrayList)
			{
				if(q.equalsIgnoreCase(a))
				{
					common_words++;
					common_words_length+=q.length();
					break;
				}
			}
		}
		if(common_words==0)
		{
			return 0;
		}
		else
		{
			//float res=((2*(common_words/common_words_length))/(((queryList.size()/query_length)+(arrayList.size()/sentence_length))));
			return common_words;
		}
	}
	public static float n_gram_similarity(ArrayList<String> queryList, ArrayList<String> arrayList)
	{
		ArrayList<String> dummy1 = new ArrayList<String>();
		ArrayList<String> dummy2 = new ArrayList<String>();
		float dummy1_length=0,dummy2_length=0,dummy_int_length=0;
		for(String q : queryList)
		{
			for(int i=0;i<q.length()-1;i++)
			{
				dummy1.add(q.substring(i, i+2).toLowerCase());
			}
		}
		dummy1_length=dummy1.size()*2;
		for(String s : arrayList)
		{
			for(int i=0;i<s.length()-1;i++)
			{
				dummy2.add(s.substring(i, i+2).toLowerCase());
			}
		}
		//dummy2_length=dummy2.size()*2;
		//float size=dummy1.size();
		dummy1.retainAll(dummy2);
		//dummy_int_length=dummy1.size()*2;
		/*if(dummy1.size()==0)
			return 0;
		else
		{
			return (2*(dummy1.size()/dummy_int_length)/((size/dummy1_length) + (dummy2.size()/dummy2_length)));
		}*/
		return dummy1.size();
	}
	public static float semantic_similarity(ArrayList<String> queryList, ArrayList<String> arrayList) //queryList and arrayList should not be stemmed,only included first level synonyms from WordNet
	{
		System.setProperty("wordnet.database.dir", "C:/Program Files (x86)/WordNet/2.1/dict");
		NounSynset nounSynset;
		float dummy1_length=0,arraylist_length=0,dummy_int_length=0;
		ArrayList<String> dummy1= new ArrayList<String>();
		WordNetDatabase wordNetDatabase = WordNetDatabase.getFileInstance();
		Set<String> set = new HashSet<String>();
		for(String q : queryList)
		{
			Synset[] synsets = wordNetDatabase.getSynsets(q);
			for(int i=0;i<synsets.length;i++)
			{
				String wordforms[] = synsets[i].getWordForms();
				for(int j=0;j<wordforms.length;j++)
				{
					set.add(wordforms[j]);
				}
			}
		}
		dummy1.addAll(set);
		/*for(String d : dummy1)
		{
			dummy1_length+=d.length();
		}
		for(String a : arrayList)
		{
			arraylist_length+=a.length();
		}*/
		//int size = dummy1.size();
		dummy1.retainAll(arrayList);
		/*for(String d :dummy1)
		{
			dummy_int_length+=d.length();
		}*/
		/*if(dummy1.size()==0)
			return 0;
		else
		{
			return ((2*(dummy1.size()/dummy_int_length))/((size/dummy1_length) + (arrayList.size()/arraylist_length)));
		}*/
		return dummy1.size();
	}
}
