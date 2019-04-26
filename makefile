all: Lexeme.class Lexer.class Parser.class Environment.class Evaluator.class PrettyPrinter.class Main.class
	chmod 755 run.sh

Lexeme.class: Lexeme.java
	javac Lexeme.java

Lexer.class: Lexer.java
	javac Lexer.java

Parser.class: Parser.java
	javac Parser.java

Environment.class: Environment.java
	javac Environment.java

Evaluator.class: Evaluator.java
	javac Evaluator.java

PrettyPrinter.class: PrettyPrinter.java
	javac PrettyPrinter.java

Main.class: Main.java
	javac Main.java

clean:
	rm -f *.class

pretty:
	cat pretty.brun

error1:
	cat test/error1.brun
error1x:
	java PrettyPrinter test/error1.brun
	cat pretty.brun
	./run.sh pretty.brun

error2:
	cat test/error2.brun
error2x:
	java PrettyPrinter test/error2.brun
	cat pretty.brun
	./run.sh pretty.brun

error3:
	cat test/error3.brun
error3x:
	java PrettyPrinter test/error3.brun
	cat pretty.brun
	./run.sh pretty.brun

error4:
	cat test/error4.brun
error4x:
	java PrettyPrinter test/error4.brun
	cat pretty.brun
	./run.sh pretty.brun

error5:                         
	cat test/error5.brun
error5x:
	java PrettyPrinter test/error5.brun
	cat pretty.brun
	./run.sh pretty.brun

arrays:
	cat test/arrays.brun
arraysx:
	java PrettyPrinter test/arrays.brun
	cat pretty.brun
	./run.sh pretty.brun

conditionals:
	cat test/conditionals.brun
conditionalsx:
	java PrettyPrinter test/conditionals.brun
	cat pretty.brun
	./run.sh pretty.brun

recursion:
	cat test/recursion.brun
recursionx:
	java PrettyPrinter test/recursion.brun
	cat pretty.brun
	./run.sh pretty.brun

iteration:
	cat test/iteration.brun
iterationx:
	java PrettyPrinter test/iteration.brun
	cat pretty.brun
	./run.sh pretty.brun

functions:
	cat test/functions.brun
functionsx:
	java PrettyPrinter test/functions.brun
	cat pretty.brun
	./run.sh pretty.brun

lambda:
	cat test/lambda.brun
lambdax:
	java PrettyPrinter test/lambda.brun
	cat pretty.brun
	./run.sh pretty.brun

objects:
	cat test/objects.brun
objectsx:
	java PrettyPrinter test/objects.brun
	cat pretty.brun
	./run.sh pretty.brun

problem:
	cat test/problem.brun
problemx:
	java PrettyPrinter test/problem.brun
	cat pretty.brun
	./run.sh pretty.brun

testlang:
	cat test/test.brun
testlangx:
	java PrettyPrinter test/test.brun
	cat pretty.brun
	./run.sh pretty.brun