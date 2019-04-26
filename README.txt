ThoughtSpeech Programming Language
Author: Nicholas Brunelle
Index:
	1.0 Running Code
		Executing 'make' or 'make all' will compile all ThoughtSpeech source files and allow you to run source code.
		A script has been provided that will run the ThoughtSpeech language. 

		The following code can be used to run ThoughtSpeech programs:
			java PrettyPrinter yourcode.brun
			./run.sh pretty.brun

			.brun is the file extension for the ThoughtSpeech programming language, in reference to its creator's last name
	
	2.0 Comments
		2.1 Comments are declared with '$' and ended with a '$'
			$ This is an example of a ThoughtSpeech comment $

	3.0 Types
		There are three different types in the ThoughtSpeech programming language
			1. A string type, referred to as AlphaSpeech or alpha for short 
			2. An integer type, referred to as NumericSpeech or numeric for short
			3. A boolean type, referred to as Boolean
	
	4.0 Variables
		4.1 Assignment
			In ThoughtSpeech, variables are referred to as mutables. 
			Mutables are typeless and defined using the "mut" keyword
				mut mutExample = "brunelle";
				mut mutExample2 = 95;

		4.2 Reassignment
			Mutables can be assigned new values by using their name:
				mutExample2 = mutExample2 + 1;

	5.0 Functions
		5.1 General Referencing
			Functions are first class objects in ThoughtSpeech, and are also referred to as thought processes. They can be assigned to variables and manipulated accordingly.
			The following code is an example of a function definition:
				learn newThoughtProcess(){
					shareIdea("Example speech");
				}

		5.2 
			Using this function as a variable:
				mut brunelle = fibOf(3);
				brunelle();
				mut brunelle2 = fibOf(brunelle());

	6.0 Anonymous
		learn useLambda() {
			give learn () {
				shareIdea("lambda");
			}
		}

	7.0 Built-In Functionality within ThoughtSpeech
		share() - prints a value on the current line
		shareIdea() - prints a value on the current line and then adds a new line
		length(object) - gives the length of a string or array object
		add(array, item) - add an object to the back of an array
		takeaway(array, index) - remove an item from the given index of array
		read() - reads information from parameter

	8.0 Arrays
		Arrays, referred to as ThoughtMaps in the ThoughtSpeech programming language, are defined as follows:
			mut thoughtmap = ["this", "is", "an", "example", "of", "an", "array"];
			mut ten = 10;
			mut brain = ["the", "human", "mind", "is", "capable of", "learning", ten, "new", "things every day."];
		ThoughtSpeech arrays can hold different types.

	9.0 Conditionals
		The following is an example of conditional block syntax:
			maybe(30 > 80) {
				shareIdea("Incorrect");
			} maybeNot maybe (5 < 8) {
				shareIdea("Also wrong");
			} maybeNot {
				shareIdea("Hopefully this statement is shared")
			}

	10.0 While Loops
		thinkAbout(t <= 80){
			share(t);
			t = t + 5;
		}

	11.0 Operators
		11.1 Conditional Operators
			'==' - check for equality
			'!=' - check for non-equality
			'>'  - larger than
			'<'  - smaller than
			'>=' - larger than or equal to
			'<=' - smaller than or equal to
			'&&' - AND
			'||' - OR

		11.2 Normal Operators
			'+' - addition
			'-' - subtraction
			'*' - multiplication
			'/' - division
			'^' - exponential growth