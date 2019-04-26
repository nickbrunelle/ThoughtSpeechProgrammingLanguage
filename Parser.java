/*
	Nicholas A. Brunelle
	CS403 Designer Programming Language
	Parser.java for my ThoughtSpeech Programming Language
	$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	This is the recognizer/parser module for the ThoughtSpeech programming language.
	A recognizer is similar to an advanced scanner. Just like a scanner, the recognizer
	repeatedly calls the lex function to serve up lexemes. In addition, the recognizer 
	checks each provided lexeme against the ThoughtSpeech programming language grammar.
	This module's main functionality is to determine whether the entire source code of a 
	program written in ThoughtSpeech is syntactically correct.

	A parser is a recognizer that also builds an abstract representation of the text being 
	scanned and is necessary for creating a functioning language interpreter. This file 
	is the result of using a technique known as recursive descent parsing to first construct 
	a recognizer, which was then converted into the parser file contained herein.	
*/

import java.io.PushbackReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Parser  
{
	private Lexeme lexeme;
	private Lexer lexer;

	//Parser constructor that takes accepts a given fileName parameter
	public Parser(String fileName)		
	{
		PushbackReader br = null;
		try {
			br = new PushbackReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		lexer = new Lexer(br);
	}

	//parse functionality utilizing recursive descent parsing support function advance()
	public Lexeme parse()
	{
		advance();
		return statements();
	}

	//recursive descent parsing support function to check whether or not the current lexeme is of the given type
	private boolean check(String type)
	{
		return lexeme.type.equals(type);
	}

	//recursive descent parsing support function to move to the next lexeme in the input stream
	private Lexeme advance()
	{
		Lexeme prevLexeme = lexeme;
		lexeme = lexer.lex();
		return prevLexeme;
	}

	//recursive descent parsing support function that works similarily to advance() but it forces the current lexeme to be matched
	private Lexeme match(String type)
	{
		matchNoAdvance(type);
		return advance();
	}

	//support function for lexical helper function match()
	private void matchNoAdvance(String type)
	{
		if(!check(type))
		{
			System.out.println("TS: SYNTAX ERROR: Line " + lexer.lineNumber);
			if(type.equals("SEMICOLON"))
			{
				System.out.println("TS: You are missing a semicolon");
			} else {
				System.out.println("TS: Got " + lexeme.type + " You" + "Needed " + type);
			}
			System.exit(0);
		}
	}

	//support function to define multiple statement behavior in ThoughtSpeech
	private Lexeme statements()
	{
		Lexeme tree = new Lexeme("STATEMENTS");
		if(statementPending())
		{
			tree.left = statement();
			tree.right = statements();
		}

		return tree;
	}

	//support function for statements() defining single statement formats
	private Lexeme statement()
	{
		Lexeme tree = new Lexeme("STATEMENT");
		if(check("INNER_THOUGHT"))
		{
			tree.left = match("INNER_THOUGHT");
		} else if (varDefPending()) {
			tree.left = varDef();
			match("SEMICOLON");
		} else if (funcDefPending()) {
			tree.left = functionDef();
		} else if (ifPending()) {
			tree.left = ifStatement();
		} else if (varPending()) {
			Lexeme temp = match("MUTABLE");
			if(check("START_THOUGHT")) {
				tree.left = funcCall(temp);
				match("SEMICOLON");
			} else if (check("POINT")) {
				tree.left = dot(temp);
				match("SEMICOLON");
			} else {
				tree.left = varAssign(temp);
				match("SEMICOLON");
			}
		} else if (whilePending()) {
			tree.left = whileLoop();
		} else if (returnPending()) {
			tree.left = returnStatement();
			match("SEMICOLON");
		}

		return tree;
	}

	//function to utilize check() functionality to determine if a statement definition is pending in the lexical stream
	private boolean statementPending()
	{
		return expressionPending() ||
			   varDefPending() ||
			   check("INNER_THOUGHT") ||
			   ifPending() ||
			   whilePending() ||
			   funcDefPending() ||
			   returnPending();
	}

	//function that allows for the definition of ThoughtSpeech mutables, objects known as variables in other languages
	private Lexeme varDef()
	{
		Lexeme tree = match("ALLOW");
		tree.left = match("MUTABLE");
		match("REMEMBER");
		if(arrayPending()) {
			tree.right = buildArray();
		} else {
			tree.right = expression();
		}

		return tree;
	}

	//function to utilize check() functionality to determine if a mutable definition is pending in the lexical stream
	private boolean varDefPending() 
	{
		return check("ALLOW");
	}

	//block format recognizer using previously defined match() functionality 
	private Lexeme block()
	{
		match("OPEN_BUBBLE");
		Lexeme tree = statements();
		match("CLOSE_BUBBLE");
		return tree;
	}

	//function utilizing check() functionality to determine if an if statement is pending in the lexical stream
	private boolean ifPending()
	{
		return check("IF");
	}

	//if statement format recognizer
	private Lexeme ifStatement()
	{
		Lexeme tree = match("IF");
		match("START_THOUGHT");
		tree.left = new Lexeme("GLUE");
		tree.left.left = expression();
		match("END_THOUGHT");
		tree.left.right = block();
		if(elsePending()) {
			tree.right = elseStatement();
		}
		return tree;
	}

	//function utilizing check() functionality to determine if an else statement is pending in the lexical stream
	private boolean elsePending()
	{
		return check("ELSE");
	}

	//else statement format recognizer
	private Lexeme elseStatement()
	{
		match("ELSE");
		Lexeme tree;
		if(ifPending()) {
			tree = ifStatement();
		} else {
			tree = block();
		}
		return tree;
	}

	//function utilizing check() functionality to determine if a while loop definition is pending in the lexical stream
	private boolean whilePending()
	{
		return check("WHILE");
	}

	//while loop format recognizer
	private Lexeme whileLoop()
	{
		Lexeme tree = match("WHILE");
		match("START_THOUGHT");
		tree.left = expression();
		match("END_THOUGHT");
		tree.right = block();
		return tree;
	}

	//function that determines if a mutable definition is pending in the lexical stream
	private boolean varPending()
	{
		return check("MUTABLE");
	}

	//mutable assignment format definition
	private Lexeme varAssign(Lexeme temp)
	{
		Lexeme tree;
		if(check("POINT"))
		{
			Lexeme dotTree = match("POINT");
			dotTree.left = temp;
			dotTree.right = match("MUTABLE");
			tree = match("REMEMBER");
			tree.left = dotTree;
			tree.right = expression();
		} else {
			tree = match("REMEMBER");
			tree.left = temp;
			if(check("OPEN_MAP")) {
				tree.right = buildArray();
			} else tree.right = expression();
		}

		return tree;
	}	

	//function to recognize point format 
	private Lexeme dot(Lexeme var)
	{
		Lexeme tree = match("POINT");
		tree.left = var;
		tree.right = expression();
		if(check("REMEMBER"))
		{
			Lexeme dotTree = match("REMEMBER");
			dotTree.left = tree;
			dotTree.right = expression();
			return dotTree;
		} else {
			return tree;
		}
	}

	//function call format recognition code
	private Lexeme funcCall(Lexeme temp)
	{
		Lexeme tree = new Lexeme("TP_CALL");    
		tree.left = temp;
		match("START_THOUGHT");
		tree.right = optArgList();
		match("END_THOUGHT");
		return tree;
	}

	//support function for funcCall()
	private Lexeme optArgList()
	{
		if(argListPending())
		{
			return argList();
		} else return null;
	}

	//support function for optArgList()
	private Lexeme argList()
	{
		Lexeme tree = new Lexeme("GLUE");  
		tree.left = expression();
		if(check("COUPLER"))
		{
			match("COUPLER");
			tree.right = argList();
		}

		return tree;
	}

	//function that determines if an argument list is pending in the lexical stream
	private boolean argListPending()
	{
		return unaryPending();
	}

	//function that determines if a return statement is pending in the lexical stream
	private boolean returnPending()
	{
		return check("GIVE");
	}

	//return statement format recognizer 
	private Lexeme returnStatement()
	{
		Lexeme tree = match("GIVE");
		tree.left = expression();
		return tree;
	}

	//unary format definition containing integer, string, boolean, null, and variable types. 
	//In the ThoughtSpeech language, these are known as numerics, alphas, booleans, empty_thoughts, and mutables
	private Lexeme unary()
	{
		Lexeme tree;
		if(check("NUMERIC")) {
			tree = match("NUMERIC");
		} else if(check("ALPHA")) {
			tree = match("ALPHA");
		} else if(check("BOOLEAN")) {
			tree = match("BOOLEAN");
		} else if(check("EMPTY_THOUGHT")) {
			tree = match("EMPTY_THOUGHT");
		} else {
			tree = match("MUTABLE");
		}

		return tree;
	}

	//function that determines if a unary definition is pending in the lexical stream
	private boolean unaryPending()
	{
		return check("NUMERIC") ||
			   check("ALPHA") ||
			   check("MUTABLE") ||
			   check("BOOLEAN") ||
			   check("EMPTY_THOUGHT");
	}

	//function that calls recursive descent parsing support function advance()
	private Lexeme operator()
	{
		return advance();
	}

	//function that determines if an operator definition is pending in the lexical stream
	private boolean operatorPending()
	{
		return check("PLUS") ||
			   check("SUBTRACT") ||
			   check("TIMES") ||
			   check("DIVIDE") ||
			   check("EQUAL") ||
			   check("UNEQUAL") ||
			   check("LARGER") ||
			   check("LARGEOREQ") ||
			   check("SMALLER") ||
			   check("SMALLOREQ") ||
			   check("BOTH") ||
			   check("EITHER") ||
			   check("POWER");
	}

	//add comment
	private Lexeme arrayAccess()
	{
		Lexeme tree = new Lexeme("TM_ACCESS"); 
		match("OPEN_MAP");
		tree.right = unary();
		match("CLOSE_MAP");
		return tree;
	}

	//expression format recognition code 
	private Lexeme expression()
	{
		Lexeme tree;
		if(funcDefPending())
		{
			tree = functionDef();
			return tree;
		}

		tree = unary();
		while(check("POINT"))
		{
			Lexeme temp = match("POINT");
			temp.left = tree;
			Lexeme temp2 = match("MUTABLE");
			if(check("START_THOUGHT")) temp.right = funcCall(temp2);
			else temp.right = temp2;
			tree = temp;
		}

		if(check("OPEN_MAP"))
		{
			Lexeme temp = arrayAccess();
			temp.left = tree;
			tree = temp;
		} else if(check("START_THOUGHT")) {
			tree = funcCall(tree);
		}

		while(operatorPending())
		{
			Lexeme temp = operator();
			temp.left = tree;
			temp.right = expression();
			tree = temp;
		}

		return tree;
	}

	//function to determine if an expression statement is pending in the lexical stream
	private boolean expressionPending()
	{
		return unaryPending();
	}

	//function definition format recognizer
	private Lexeme functionDef()
	{
		Lexeme tree = match("THOUGHT_PROCESS");
		if(check("MUTABLE"))
		{
			tree.left = match("MUTABLE");
		}

		match("START_THOUGHT");
		tree.right = new Lexeme("GLUE");      
		tree.right.left = optParameterList();
		match("END_THOUGHT");
		tree.right.right = block();
		return tree;
	}

	//function to determine if a function definition is pending in the lexical stream
	private boolean funcDefPending()
	{
		return check("THOUGHT_PROCESS");
	}

	//support function for functionDef()
	private Lexeme optParameterList()
	{
		if(parameterListPending())
		{
			return parameterList();
		} else return null;
	}

	//support function for optParameterList()
	private Lexeme parameterList()
	{
		Lexeme tree = new Lexeme("GLUE");  
		tree.left = match("MUTABLE");
		if(check("COUPLER"))
		{
			match("COUPLER");
			tree.right = parameterList();
		}

		return tree;
	}

	//function to determine if a parameter list is pending in the lexical stream
	private boolean parameterListPending()
	{
		return check("MUTABLE");
	}

	//function to determine if an array definition is pending in the lexical stream
	private boolean arrayPending()
	{
		return check("OPEN_MAP");
	}

	//array construction format recognizer
	private Lexeme buildArray()
	{
		match("OPEN_MAP");
		ArrayList<Lexeme> array = new ArrayList<Lexeme>();
		while(true)
		{
			if(unaryPending()) array.add(unary());
			if(!check("COUPLER"))
			{
				match("CLOSE_MAP");
				return new Lexeme ("THOUGHT_MAP", array);
			}

			match("COUPLER");
		}
	}
}