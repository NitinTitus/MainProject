package package_1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Demo 
{
	public static void main(String[] args) throws IOException 
	{
		String name="In 1962, he was elected the president. He is a U.S. citizen.";
		String words[]=name.split("[^A-Z]\\.");
		for(String s: words)
		{
			System.out.println(s);
		}
	}
}


