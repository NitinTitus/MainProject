package package_1;

import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.Parser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class Demo 
{
	static String pronoun_list[]={"I","you","she","he","it","we","they"};
	public static String check_similarity(String candidate_answer,String candidate_answers[])
	{
		int words,flag=0;
		candidate_answer=candidate_answer.trim();
		String final_answer=null;
		String s1[]=candidate_answer.split(" ");
		for(int i=0;i<candidate_answers.length;i++)
		{
			words=0;
			String s2[]=candidate_answers[i].split(" ");
			for(String s: s1)
			{
				for(String p: s2)
				{
					if(p.equalsIgnoreCase(s))
					{
						words++;
					}
				}
			}
			if(words>=(s2.length/2) && s2.length>1)
			{
				flag=1;
				final_answer=candidate_answers[i];
				break;
			}
			else if(s2.length==1)
			{
				if(words==1)
				{
					flag=1;
					final_answer=candidate_answers[i];
					break;
				}
			}
		}
		return final_answer;
	}
	public static int find_match(String value,String verb)
	{
		int common=0;
		for(int i=0;i<value.length() && i<verb.length();i++)
		{
			if(value.charAt(i)==verb.charAt(i))
			{
				common++;
			}
		}
		return common;
	}
	public static String init(String answer,int sentence_no,String user_answer,String candidate_answers[],String query,String passage) 
	{
		 String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz",final_answer=null;
		 LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);
		 String label = null,value = null,candidate_answer="",pronoun=null;
		 int flag=0,similarity;
	     Tree tree = parser.parse(answer); 
	     Tree tr = parser.parse(query);
	     System.out.println(tree.toString());
	     String[] words = query.split("\\s+"),d_words={"Do","Did","Does"};
	     for(String word: words)
		{
			for(String s : d_words)
			{
				if(s.equalsIgnoreCase(word))
				{
					flag=1;
				}
			}
		}
	     /*for(Label l : tree.taggedLabeledYield())
	     {
	    	 System.out.println(l.toString());
	     }*/
	     TregexPattern tregexPattern = TregexPattern.compile("@VBD | @VBG | @VBN | @VBP | @VBZ | @VB");
	     TregexMatcher tregexMatcher = tregexPattern.matcher(tree);
	     TregexMatcher tregexMatcher2 = tregexPattern.matcher(tr);
	     String main_verb = null;
	     int match=0,temp;
	     while(tregexMatcher2.find())
	     {
	    	 Tree vp=tregexMatcher2.getMatch();
	    	 label=vp.label().toString();
	    	 value=vp.getChild(0).toString();
	    	 if(value.equalsIgnoreCase("Do") || value.equalsIgnoreCase("Did") || value.equalsIgnoreCase("Does"))
	    		 continue;
	    	 else
	    		 break;
	     }
	     while(tregexMatcher.find())
	     {
	    	 Tree np=tregexMatcher.getMatch();
	    	 String verb=np.getChild(0).toString();
	    	 if((temp=find_match(value,verb))>=match)
	    	 {
	    		 main_verb=verb;
	    		 label=np.label().toString();
	    		 match=temp;
	    	 }
	     }
	     if(flag==0)
	     {
	    	 TregexPattern tregexPattern2 = TregexPattern.compile("NP .. " + label +"= vb");
	    	 tregexMatcher=tregexPattern2.matcher(tree);
	    	 flag=0;
	    	 while(tregexMatcher.find())
	    	 {
	    		 Tree np=tregexMatcher.getMatch();
	    		 Tree vb=tregexMatcher.getNode("vb");
	    		 String verb=vb.getChild(0).toString();
	    		 if(!verb.equalsIgnoreCase(main_verb))
	    			 continue;
	    		 tregexPattern = TregexPattern.compile("NP < NNP | NN");
	    		 tregexMatcher2=tregexPattern.matcher(np);
	    		 while(tregexMatcher2.find())
	    		 {
	    			 List<Tree> list = np.getChildrenAsList();
	    			 for(Tree t: list)
	    			 {
	    				 if(t.label().toString().equalsIgnoreCase("NNP") || t.label().toString().equalsIgnoreCase("NN"))
	    				 {
	    					 candidate_answer+=t.getChild(0).toString() + " ";
	    				 }
	    			 }
	    			 final_answer=check_similarity(candidate_answer,candidate_answers);
	    			 if(final_answer!=null)
	    			 {
	    				 flag=1;
	    				 break;
	    			 }
	    		 }
	    		 if(flag==1)
	    			 break;
	    	 }
	    	 int count=0;
	    	 /*if(final_answer==null)
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
	 			similarity=PronounResolver.pronoun(passage, sentence_no + 1, count - 1, user_answer);
	    	 }*/
	    	 return final_answer;
	     }
	     else
	     {
	    	 flag=0;
	    	 tregexPattern= TregexPattern.compile(label+ "=vb .. NP=n");
	    	 tregexMatcher=tregexPattern.matcher(tree);
	    	 while(tregexMatcher.find())
	    	 {
	    		 Tree np=tregexMatcher.getNode("n");
	    		 Tree vb=tregexMatcher.getNode("vb");
	    		 String verb=vb.getChild(0).toString();
	    		 if(!verb.equalsIgnoreCase(main_verb))
	    			 continue;
	    		 tregexPattern=TregexPattern.compile("NP < NNP | NN");
	    		 tregexMatcher2=tregexPattern.matcher(np);
	    		 while(tregexMatcher2.find())
	    		 {
	    			 candidate_answer="";
	    			 Tree vp=tregexMatcher2.getMatch();
	    			 System.out.println(vp.toString());
	    			 List<Tree> list = np.getChildrenAsList();
	    			 for(Tree t: list)
	    			 {
	    				 if(t.label().toString().equalsIgnoreCase("NNP") || t.label().toString().equalsIgnoreCase("NN"))
	    				 {
	    					 candidate_answer+=t.getChild(0).toString()+" ";
	    				 }
	    			 }
	    			 final_answer=check_similarity(candidate_answer,candidate_answers);
    				 if(final_answer!=null)
    				 {
    					 flag=1;
    					 break;
    				 }
	    		 }
	    		 if(flag==1)
    				 break;
	    	 }
	    	 return final_answer;
	     }
	}
}


