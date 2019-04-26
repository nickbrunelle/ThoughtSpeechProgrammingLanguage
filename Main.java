/*
    Nicholas A. Brunelle
    CS403 Designer Programming Language
    Main.java for my ThoughtSpeech Programming Language
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	This file contains the driver code for the ThoughtSpeech programming language. 
*/

public class Main
{
	public static void main(String[] args) 
	{
		Parser t = new Parser(args[0]); 
		Lexeme parseTree = t.parse();   
		Environment env = new Environment();
		Evaluator s = new Evaluator(env);
		Lexeme global = env.createEnv();
		s.eval(parseTree, global);
	}
}