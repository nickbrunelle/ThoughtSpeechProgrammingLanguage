/*
	Nicholas A. Brunelle
	CS403 Designer Programming Language
	Lexeme.java for my ThoughtSpeech Programming Language
	$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	A lexeme is a data structure that holds, at minimu, two pieces of information: the type of the processed token, 
	and, in some cases, the actual token itself, as is the case when defining variables in a unique programming language.
	This file stores the primitive types of the ThoughtSpeech language's lexeme object, and is used in conjunction with the
	lexer.java file to lexically analyze the language's source code. 
*/

import java.util.ArrayList;

public class Lexeme
{
	//each field corresponds to each of the primitive types in the ThoughtSpeech language
	String strVal;
	int intVal;
	boolean boolVal;
	ArrayList<Lexeme> arrayVal;
	String varVal;

	String type;
	Lexeme left = null;
	Lexeme right = null;

	public Lexeme(String type)
	{
		this.type = type;
	}

	public Lexeme(String type, String varOrStringVal)
	{
		this.type = type;
		if(varOrStringVal.startsWith("\"")) this.strVal = varOrStringVal.replace("\"", "");
		else this.varVal = varOrStringVal;
	}

	public Lexeme(String type, int intVal)
	{
		this.type = type;
		this.intVal = intVal;
	}

	public Lexeme(String type, boolean boolVal)
	{
		this.type = type;
		this.boolVal = boolVal;
	}

	public Lexeme(String type, ArrayList<Lexeme> arrayVal)
	{
		this.type = type;
		this.arrayVal = arrayVal;
	}

	public Lexeme(String type, Lexeme left, Lexeme right)
	{
		this.type = type;
		this.left = left;
		this.right = right;
	}

	boolean isInt()
	{
		return this.type.equals("NUMERIC"); 
	}

	boolean isString()
	{
		return this.type.equals("ALPHA");   
	}

	@Override
	public String toString()
	{
		switch(this.type)
		{
			case "ALPHA":
				return this.strVal;
			case "NUMERIC":
				return Integer.toString(this.intVal);
			case "BOOLEAN":
				if(this.boolVal) return "true";
				else return "false";
			case "THOUGHT_MAP":
				return this.arrayVal.toString();
			case "MUTABLE":
				return this.varVal;
			default:
				return this.type.toUpperCase();
		}
	}
}