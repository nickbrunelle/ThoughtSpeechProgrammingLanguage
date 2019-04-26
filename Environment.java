/*
	Nicholas A. Brunelle
	CS403 Designer Programming Language
	Environment.java for my ThoughtSpeech Programming Language
	$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	The purpose of an environment is two-fold. An environment must hold the bindings between
	variables and their current values, and an environment must also implement scope. There are five 
	basic operations on environments: create, lookup, update, insert, and extend. One option that a 
	programmer can consider to implement an environment is to store the variables and values in a 
	particular environment as two parallel lists, as parallel arrays allow for the efficient 
	implementation of function calls. 

	This file implements environments using the defined lexeme data structure. Because environments 
	need to point to other environments, the ThoughtSpeech programming language models the environment 
	structure as a list of parallel lists. For example, the first two lists in an environment structure 
	correspond to the variables and values in the innermost scope, and the next two lists will be the 
	variables and values in the nearest outer scope, and so on.
*/

public class Environment
{
	//function to construct a new lexeme with the first argument giving the type of the lexeme, and
	//the second argument giving the left pointer and the third argument giving the right pointer
	public Lexeme cons(String type, Lexeme carVal, Lexeme cdrVal)
	{
		return new Lexeme(type, carVal, cdrVal);
	}

	//support function to return cell.left using given parameter
	public Lexeme car(Lexeme cell)
	{
		return cell.left;
	}

	//support function to return cell.right using given parameter
	public Lexeme cdr(Lexeme cell)
	{
		return cell.right;
	}

	//support function to set the value of cell.left using given new value parameter
	public Lexeme setCar(Lexeme cell, Lexeme newVal)
	{
		cell.left = newVal;
		return cell;
	}

	//support function to set the value of cell.right using given new value parameter
	public Lexeme setCdr(Lexeme cell, Lexeme newVal)
	{
		cell.right = newVal;
		return cell;
	}	

	//create environment function that utilizes the environment extension support function and supplies it with three null type parameters
	//note: environment creation can be written in terms of environment extension, as is seen in the function below
	public Lexeme createEnv()
	{
		return extendEnv(null, null, null);
	}

	//environment extension function that accepts three lexeme parameters and utilizes the makeTable support function
	//Extension means that a new environment is created, populated with the local parameters and values, and finally pointed
	//to the defining environment
	public Lexeme extendEnv(Lexeme env, Lexeme variables, Lexeme values)
	{
		return cons("ENV", makeTable(variables, values), env);
	}

	//function that uses two given lexeme object parameters to create a table
	public Lexeme makeTable(Lexeme variables, Lexeme values)
	{
		return cons("TABLE", variables, values);
	}

	//function checking for same variable (mutable) values in two given parameters
	private boolean sameVar(Lexeme var1, Lexeme var2)
	{
		return var1.toString().equals(var2.toString());
	}

	//support function to look up information stored within a given env parameter
	public Lexeme lookupEnv(Lexeme variable, Lexeme env)
	{
		while(env != null)
		{
			Lexeme table = car(env);
			Lexeme vars = car(table);
			Lexeme vals = cdr(table);
			while(vars != null)
			{
				if(sameVar(variable, car(vars)))
				{
					return car(vals);
				}

				vars = cdr(vars);
				vals = cdr(vals);
			}

			env = cdr(env);
		}

		System.out.println("TS: MUTABLE: " + variable + " is undefined");
		System.exit(0);
		return null;
	}

	//function that serves to update a given environment paramter using the given variable and newVal parameters
	public Lexeme updateEnv(Lexeme variable, Lexeme newVal, Lexeme env)
	{
		while(env != null)
		{
			Lexeme table = car(env);
			Lexeme vars = car(table);
			Lexeme vals = cdr(table);
			while(vars != null)
			{
				if(sameVar(variable, car(vars)))
				{
					return setCar(vals, newVal);
				}

				vars = cdr(vars);
				vals = cdr(vals);
			}

			env = cdr(env);
		}

		System.out.println("TS: MUTABLE: " + variable + " is undefined");
		System.exit(0);
		return null;
	}

	//function to insert provided variable and value parameters into a given environment parameter
	public Lexeme insert(Lexeme variable, Lexeme value, Lexeme env)
	{
		Lexeme table = car(env);
		setCar(table, cons("GLUE", variable, car(table)));
		setCdr(table, cons("GLUE", value, cdr(table)));
		return value;
	}

	//support function that returns variables in a given environment parameter 
	public Lexeme getVars(Lexeme env)
	{
		Lexeme table = car(env);
		return car(table);
	}

	//support function that returns values in a given environment parameter
	public Lexeme getVals(Lexeme env)
	{
		Lexeme table = car(env);
		return cdr(table);
	}
}