package package_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class Modified {
	static String query;
	static String passage;
	static String user_answer;
	static String sentences[];
	static String pronoun_list[]={"I","you","she","he","it","we","they"};
	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {
		String serializedClassifier = "C:/Users/Nitin/Downloads/Compressed/stanford-ner-2014-06-16/stanford-ner-2014-06-16/classifiers/english.all.3class.distsim.crf.ser.gz";
		String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
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
		String question_words[] = {"Who","Which","Where","How"},question_word = null,d_words[]={"Do","Did","Does"};
		String[] words = query.split("\\s+");
		int flag=0;
		for(String word: words)
		{
			for(String s : question_words)
			{
				if(word.equalsIgnoreCase(s))
				{
					question_word=s;
				}
			}
			for(String s : d_words)
			{
				if(s.equalsIgnoreCase(word))
				{
					flag=1;
				}
			}
		}
		String answer_phrase="";
		if(question_word.equalsIgnoreCase("how"))
		{
			Tree tree = parser.parse(answer);
			TregexPattern tregexPattern = TregexPattern.compile("@NP < (CD..NNS) | @NP < QP");
			TregexMatcher tregexMatcher = tregexPattern.matcher(tree);
			while(tregexMatcher.find())
			{
				Tree np=tregexMatcher.getMatch();
				for(Tree s : np.getLeaves())
				{
					answer_phrase+=s.toString() + " ";
				}
				break;
			}
			System.out.println(answer_phrase);
		}
}
}