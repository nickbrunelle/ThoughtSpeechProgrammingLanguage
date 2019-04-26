/*
	Nicholas A. Brunelle
	CS403 Designer Programming Language
	Lexer.java for my ThoughtSpeech Programming Language
	$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	The first phase of implementing a programming language is known as lexical analysis.

	The source code of the ThoughtSpeech programming language is stored as a file of 
	characters. These characters are then either processed as tokens or they are classified
	as unimportant characters and are ignored. The subsystem that is used during this reading
	process is responsible for distinguishing between important and unimportant characters. 
	This subsystem is called lexical analysis. 

	Lexical analysis is the process of identifying tokens in a file of characters. The code that
	performs this analysis must also categorize each token according to its respective type.
	The following java file is responsible for ThoughtSpeech's lexical analysis, and by extension,
	its token recognition and classification methods.
*/

import java.io.IOException;
import java.io.PushbackReader;

public class Lexer
{
	//global variable definitions
	private PushbackReader reader;
	int lineNumber;

	//implementation of global input stream reader functionality
	public Lexer(PushbackReader reader)
	{
		this.reader = reader;
		lineNumber = 1;
	}

	//The lex function reads a portion of the source code and produces the next corresponding lexeme
	public Lexeme lex()
	{
		char ch;
		try
		{
			int r = reader.read();
			r = passWhiteSpace(r);
			if(r == -1)
			{
				reader.close();
				return new Lexeme("FINAL_THOUGHT"); 
			}

			ch = (char) r;
			switch (ch)
			{
				case '$':								
					return passComment();
				case '(':
					return new Lexeme("START_THOUGHT"); 
				case ')':
					return new Lexeme("END_THOUGHT"); 
				case ',':
					return new Lexeme("COUPLER"); 
				case '.':
					return new Lexeme("POINT");         
				case '+':
				case '*':
				case '-':
				case '/':
				case '>':
				case '<':
				case '=':
				case '!':   
				case '&':
				case '|':
					return lexMindChange(readMindChange(ch));
				case '{':
					return new Lexeme("OPEN_BUBBLE"); 
				case '}':
					return new Lexeme("CLOSE_BUBBLE"); 
				case '[':
					return new Lexeme("OPEN_MAP"); 
				case ']':
					return new Lexeme("CLOSE_MAP"); 
				case ';':
					return new Lexeme("SEMICOLON"); 
				default: 
					if(Character.isDigit(ch)) {                       
						return lexNumeric(readMoreNumeric(ch));
					} else if (Character.isAlphabetic(ch)) {
						/*
							The rules for identifying language keywords must be checked before the rules for variable definition.
							Otherwise, the ThoughtSpeech analyser will not be able to differentiate between the two types of tokens
						*/
						return lexKey(readMoreKey(ch));
					} else if (ch == '\"'){
						return new Lexeme("ALPHA", readMoreAlpha(ch)); 
					}
			}
		}

		catch (IOException e) { e.printStackTrace(); }
		return new Lexeme("BAD_IDEA");                  
	}

	public Lexeme passComment() throws IOException
	{
		int r = reader.read();
		char ch = (char) r;

		//loop to search for the end of the comment in the provided source code input stream using ThoughtSpeech's commenting syntax definitions
		while (ch != '$')
		{
			if (r == -1) 
			{
				//This conditional statement tests for correct ThoughtSpeech comment structure. 
				System.out.println("TS: It looks like there is a missing $ for a block comment around here: " + lineNumber);
				System.exit(0);	
			}

			r = reader.read();
			ch = (char) r;
		}

		return new Lexeme("INNER_THOUGHT");
	}

	/*
		The passWhiteSpace function utilizes the global variable PushbackReader to put previously read characters back on the input stream to be read again
		ThoughtSpeech is a free format language. This means that, in most cases, a user can place as many spaces, tabs, and newlines as they wish
		between the tokens making up the source code of ThoughtSpeech. These three instances are collectively referred to as whitespace. Therefore,
		this function is used to get past the arbitrary amounts of whitespace in the source code so that the next character read from the input stream
		is the start of the next token in the source code. 
	*/
	private int passWhiteSpace(int r) throws IOException
	{
		if (r == 32) {
			return passWhiteSpace(reader.read());
		} else if (r == 10) {
			lineNumber += 1;
			return passWhiteSpace(reader.read());
		} else return r;
	}

	/*
		The readMindChange function reads ThoughtSpeech operator characters from the input stream, and assigns the read characters to an operator string to construct 
		the complete operator token. In ThoughtSpeech, operators are considered mindChanges, and are referred to as such within this code. 
	*/ 
	private String readMindChange(char ch) throws IOException
	{
		String changeString = "";
		changeString += ch;
		char newCh = (char) reader.read();
		if (newCh == ch || newCh == '=') {
			changeString += newCh;
			return changeString;
		} else {
			reader.unread(newCh);
			return changeString;
		}
	}

	/*
		The readMoreNumeric function reads ThoughtSpeech numeric characters from the input stream, and assigns the read characters to a numeric string to construct
		the complete numeric token. In ThoughtSpeech, integer values are considered numeric values, and are referred to as such within this code.
	*/
	private String readMoreNumeric(char ch) throws IOException
	{
		String numString = "";
		while (Character.isDigit(ch))
		{
			numString += ch;
			ch = (char) reader.read();
		}

		reader.unread(ch);
		return numString;
	}

	/*
		The readMoreKey function reads ThoughtSpeech alpha characters from the input stream, and assigns the read characters to a keyword string to construct 
		the complete keyword token. 
	*/
	private String readMoreKey(char ch) throws IOException
	{
		String keyString = "";
		while(Character.isAlphabetic(ch))
		{
			keyString += ch;
			ch = (char) reader.read();
		}

		reader.unread(ch);
		return keyString;
	}

	/*
		The readMoreAlpha function reads ThoughtSpeech alpha characters from the input stream, and assigns the read characters to an alpha string to construct
		the complete alpha token.
	*/
	private String readMoreAlpha(char ch) throws IOException
	{
		String newString = "";
		newString += ch;
		int r = reader.read();
		ch = (char) r;

		while(ch != '\"')
		{
			if(r == -1)
			{
				System.out.println("TS: It looks like you are missing an endquote in this line: " + lineNumber); 
				System.exit(0);
			}

			newString += ch;
			r = reader.read();
			ch = (char) r;
		}

		newString += ch;
		return newString;
	}

	//Private function that returns ThoughtSpeech mindChange lexeme read from input stream using readMindChange function
	private Lexeme lexMindChange(String mc)
	{
		switch(mc) 
		{
			case "=": 
				return new Lexeme("REMEMBER"); 
			case "==":
				return new Lexeme("EQUAL"); 
			case "!=":
				return new Lexeme("UNEQUAL");  
			case "+":
				return new Lexeme("PLUS");  
			case "-":
				return new Lexeme("SUBTRACT"); 
			case "*":
				return new Lexeme("TIMES"); 
			case "^":
				return new Lexeme("POWER"); 
			case "/":
				return new Lexeme("DIVIDE"); 
			case ">":
				return new Lexeme("LARGER"); 
			case ">=": 
				return new Lexeme("LARGEOREQ"); 
			case "<":
				return new Lexeme("SMALLER");  
			case "<=":
				return new Lexeme("SMALLOREQ"); 
			case "&&":
				return new Lexeme("BOTH"); 
			case "||":
				return new Lexeme("EITHER"); 
			default:
				return new Lexeme("BAD_IDEA");
		}
	}

	//Private function that parses multiple numeric values read from the input stream into a single value and returns that value as a NUMERIC type lexeme object
	private Lexeme lexNumeric(String num)
	{
		return new Lexeme("NUMERIC", Integer.parseInt(num));
	}

	/*
		Private function that returns corresponding lexeme objects using keyword tokens read in from the source code input stream using readMoreKey function. 
		Note that the default case returns a lexeme of type MUTABLE. This means that any sequence of alpha characters processed as a token that does not match
		any existing keywords in the ThoughtSpeech language will be processed as a variable type lexeme using the string of read characters as the variable name.
		In the ThoughtSpeech language, variables are considered mutables, and are referred to as such within the code. 
	*/
	private Lexeme lexKey(String key)
	{
		switch (key) 
		{
			case "mut":
				return new Lexeme("ALLOW");
			case "learn":
				return new Lexeme("THOUGHT_PROCESS");
			case "maybe":
				return new Lexeme("IF");
			case "maybeNot":
				return new Lexeme("ELSE");
			case "thinkAbout":
				return new Lexeme("WHILE");
			case "for":                              
				return new Lexeme("FOR");
			case "true":
				return new Lexeme("BOOLEAN", true);
			case "false":
				return new Lexeme("BOOLEAN", false);
			case "give":
				return new Lexeme("GIVE");
			case "empty":
				return new Lexeme("EMPTY_THOUGHT");
			default:
				return new Lexeme("MUTABLE", key);
		}
	}
}

/*
	This was the brief program layout that repeatedly called the lex function and displayed the resulting lexemes.
	A program such as this is referred to as a scanner, and this definition was used during the testing and ongoing 
	developement of the ThoughtSpeech language.

	function scanner(filename)
	{
		var token;
		var i = new lexer(filename);

		token = i.lex();
		while(token.type !+ ENDofINPUT)
		{
			Lexeme.display(token);
			token = i.lex();
		}
	}
*/