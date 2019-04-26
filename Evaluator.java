/*
    Nicholas A. Brunelle
    CS403 Designer Programming Language
    Evaluator.java for my ThoughtSpeech Programming Language
    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    
    This file consists of ThoughtSpeech's background evaluation code. This code dictates the behavior 
    and assignment techniques of ThoughtSpeech's built-in commands, as well as additional keyword 
    functionality. This functionality ranges from variable assignment methods, information reading 
    behavior, and also dictates how ThoughtSpeech treats each of its multiple operator symbols.
*/

import java.io.IOException;
import java.util.ArrayList;

public class Evaluator {

    private Environment e;

    public Evaluator(Environment e) {
        this.e = e;
    }

    public Lexeme eval(Lexeme tree, Lexeme env) {
        if (tree == null) {
            return null;
        }
        switch (tree.type) {
            case "STATEMENTS": return evalStatements(tree, env);  
            case "STATEMENT": return eval(tree.left, env);        
            case "INNER_THOUGHT": return null;
            case "NUMERIC":
       		case "ALPHA":
            case "BOOLEAN":
            case "EMPTY_THOUGHT":
            case "THOUGHT_MAP": return tree;                          
            case "TM_ACCESS": return evalArray(tree, env);     
            case "MUTABLE": return e.lookupEnv(tree, env);
            case "PLUS":
            case "SUBTRACT":
            case "TIMES":
            case "DIVIDE":
            case "LARGER":
            case "SMALLER":
            case "LARGEOREQ":
            case "SMALLOREQ":
            case "NOTEQUAL":
            case "POWER":
            case "EQUAL": return evalOperator(tree, env);
            case "REMEMBER": return evalVarAssign(tree, env);
            case "ALLOW": return evalVarDef(tree, env);
            case "THOUGHT_PROCESS": return evalFuncDef(tree, env);
            case "IF": return evalIf(tree, env);
            case "WHILE": return evalWhile(tree, env);
            case "TP_CALL": return evalFuncCall(tree, env);    
            case "GIVE": return evalReturn(tree, env);
            case "POINT": return evalDot(tree, env);
            default:
                System.out.println("BAD IDEA!"); 
                System.exit(0);
        }
        return null;
    }

    //support function to implement add command
    private Lexeme evalAdd(Lexeme tree) {
        Lexeme array = tree.left;
        Lexeme newItem = tree.right.left;
        array.arrayVal.add(newItem);
        return null;
    }

    //support function to implement takeaway/remove command
    private Lexeme evalRemove(Lexeme tree) {
        Lexeme array = tree.left;
        Lexeme index = tree.right.left;
        array.arrayVal.remove(index.intVal);
        return null;
    }

    //support function to implement the ThoughtSpeech length command
    private Lexeme evalLength(Lexeme eargs) {
        switch (eargs.left.type) {
            case "ALPHA":
                return new Lexeme("NUMERIC", eargs.left.strVal.length());
            case "THOUGHT_MAP":
                return new Lexeme("NUMERIC", eargs.left.arrayVal.size());
            default:
                System.out.println("TS: TRYING TO TAKE LENGTH OF TYPE " + eargs.left.type + " DOES NOT WORK!");
                System.exit(0);
                return null;
        }
    }

    //support function to implement neg command 
    private Lexeme evalNeg(Lexeme eargs) {
        return new Lexeme("NUMERIC", -eargs.left.intVal);
    }

    //multiple statements evaluator support function
    private Lexeme evalStatements(Lexeme tree, Lexeme env) {
        Lexeme statements = tree;
        Lexeme currentStatement = tree.left;
        Lexeme result = null;
        while (currentStatement != null) {
            result = eval(currentStatement, env);
            if (result != null) {
                return result;
            }
            statements = statements.right;
            currentStatement = statements.left;
        }
        return result;
    }

    //support function to implement return/give command background functionality
    private Lexeme evalReturn(Lexeme tree, Lexeme env) {
        return eval(tree.left, env);
    }

    //array object evaluator function
    private Lexeme evalArray(Lexeme tree, Lexeme env) {
        int index = eval(tree.right, env).intVal;
        ArrayList array = (eval(tree.left, env).arrayVal);
        if (index >= array.size()) {
            System.out.println("TS: Index out of bounds!");
            System.exit(0);
        }
        return (Lexeme) array.get(index);
    }

    //support function to implement input reading command functionality
    private Lexeme evalReadInput(Lexeme eargs) {
        if (eargs != null) {
            System.out.println("TS: ERROR: Does not take any arguments.");
            System.exit(0);
        }
        ArrayList<Lexeme> inputArray = new ArrayList<>();
        int ch;
        try {
            while ((ch = System.in.read()) != -1) {
                if (ch != '\n' && ch != '\r') {
                    switch (ch) {
                        case '^':
                            inputArray.add(new Lexeme("POWER"));
                            break;
                        case '+':
                            inputArray.add(new Lexeme("PLUS"));
                            break;
                        case '*':
                            inputArray.add(new Lexeme("TIMES"));
                            break;
                        case '-':
                            inputArray.add(new Lexeme("SUBTRACT"));
                            break;
                        case '/':
                            inputArray.add(new Lexeme("DIVIDE"));
                            break;
                        default:
                            if (Character.isDigit(ch)) {
                                String num = "";
                                while (Character.isDigit(ch) && ch != -1) {
                                    num += (char) ch;
                                    ch = System.in.read();
                                }
                                inputArray.add(new Lexeme("NUMERIC", Integer.parseInt(num)));
                            }
                            break;
                    }
                }
            }
            return new Lexeme("THOUGHT_MAP", inputArray);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    //support function to implement println functionality, i. e. printing the parameter and adding a new line
    private Lexeme evalPrintln(Lexeme eargs) {
        if (eargs == null) {
            System.out.println();
        }
        Lexeme arg;
        while (eargs != null) {
            arg = eargs.left;
            System.out.println(arg);
            eargs = eargs.right;
        }
        return null;
    }

    //support function to implement basic character print command 
    private Lexeme evalPrint(Lexeme eargs) {
        Lexeme arg;
        while (eargs != null) {
            arg = eargs.left;
            System.out.print(arg + " ");
            eargs = eargs.right;
        }
        return null;
    }

    //Operator evaluation background implementation
    private Lexeme evalOperator(Lexeme tree, Lexeme env) {
        Lexeme left = eval(tree.left, env);
        Lexeme right = eval(tree.right, env);
        switch (tree.type) {
            case "PLUS":
                if (left.isInt()) return new Lexeme("NUMERIC", left.intVal + right.intVal);
            case "SUBTRACT":
                if (left.isInt()) return new Lexeme("NUMERIC", left.intVal - right.intVal);
            case "TIMES":
                if (left.isInt()) return new Lexeme("NUMERIC", left.intVal * right.intVal);
            case "POWER":
                if (left.isInt()) return new Lexeme("NUMERIC", (int) Math.pow(left.intVal, right.intVal));
            case "DIVIDE":
                if (left.isInt()) return new Lexeme("NUMERIC", left.intVal / right.intVal);
            case "LARGEOREQ":
                if (left.isInt()) return new Lexeme("BOOLEAN", left.intVal >= right.intVal);
            case "LARGER":
                if (left.isInt())
                    return new Lexeme("BOOLEAN", left.intVal > right.intVal);
                if (left.isString())
                    return new Lexeme("BOOLEAN", left.strVal.compareTo(right.strVal) < 0);
            case "SMALLER":
                if (left.isInt())
                    return new Lexeme("BOOLEAN", left.intVal < right.intVal);
                if (left.isString())
                    return new Lexeme("BOOLEAN", left.strVal.compareTo(right.strVal) > 0);
            case "SMALLOREQ":
                if (left.isInt()) return new Lexeme("BOOLEAN", left.intVal <= right.intVal);
            case "EQUAL":
             	if (left.isInt()) {
                    return new Lexeme("BOOLEAN", left.intVal == right.intVal);
                } else if (left.isString()) {
                    return new Lexeme("BOOLEAN", left.strVal.equals(right.strVal));
                } else {
                    if (left.type.equals("EMPTY_THOUGHT") && right.type.equals("EMPTY_THOUGHT")) return new Lexeme("BOOLEAN", true);
                    return new Lexeme("BOOLEAN", left == right);
                }
            case "UNEQUAL":
                if (left.isInt()) {
                    return new Lexeme("BOOLEAN", left.intVal != right.intVal);
                } else {
                    if (left.type.equals("EMPTY_THOUGHT") && right.type.equals("EMPTY_THOUGHT")) return new Lexeme("BOOLEAN", false);
                    return new Lexeme("BOOLEAN", left != right);
                }

            default:
                System.out.println("TS: Defaulted when attempting to evaluate mindChange...");
                System.exit(0);
                return null;
        }
    }

    //support function to evaluate variable assignment in ThoughtSpeech
    private Lexeme evalVarAssign(Lexeme tree, Lexeme env) {
        Lexeme result = eval(tree.right, env);
        if (tree.left.type.equals("MUTABLE")) {
            e.updateEnv(tree.left, result, env);
        } else if (tree.left.type.equals("POINT")) {
            Lexeme object = eval(tree.left.left, env);
            e.updateEnv(tree.left.right, result, object);
        } else {
            System.out.println("TS: This assignment is broken");
            System.exit(0);
        }
        return null;
    }

    //support function to implement while loop behavior
    private Lexeme evalWhile(Lexeme tree, Lexeme env) {
        Lexeme whileExpression = tree.left;
        Lexeme whileBody = tree.right;
        Lexeme local = e.extendEnv(env, e.getVars(env), e.getVals(env));
        while (eval(whileExpression, local).boolVal) {
            eval(whileBody, local);
        }
        return null;
    }

    //support function to implement if statement behavior
    private Lexeme evalIf(Lexeme tree, Lexeme env) {
        Lexeme ifExpression = tree.left.left;
        Lexeme ifBody = tree.left.right;
        Lexeme elseStatement = tree.right;
        Lexeme local = e.extendEnv(env, e.getVars(env), e.getVals(env));
        if (eval(ifExpression, local).boolVal) {
            return eval(ifBody, local);
        } else {
            return eval(elseStatement, local);
        }
    }

    //support function to implement function definition functionality
    private Lexeme evalFuncDef(Lexeme tree, Lexeme env) {
        if (tree.left == null) {
            return evalLambda(tree, env);
        }
        Lexeme closure = e.cons("CLOSURE", env, tree);  
        e.insert(tree.left, closure, env);
        return null;
    }

    //lambda functionality passing evaluation code
    private Lexeme evalLambda(Lexeme tree, Lexeme env) {
        Lexeme args = e.getVals(env);
        Lexeme params = tree.right.left;
        Lexeme body = tree.right.right;
        Lexeme local = e.extendEnv(env, params, args);
        return eval(body, local);
    }

    //argument evaluation behavior
    private Lexeme evalArgs(Lexeme args, Lexeme env) {
        if (args == null) return null;
        return e.cons("GLUE", eval(args.left, env), evalArgs(args.right, env));
    }

    //built-in ThoughtSpeech functionality evaluator
    private Lexeme evalFuncCall(Lexeme tree, Lexeme env) {
        String funcName = "";
        if (tree.left != null) funcName = tree.left.varVal;
        Lexeme args = tree.right;
        Lexeme eargs = evalArgs(args, env);
        switch (funcName) {
            case "neg":                                  
                return evalNeg(eargs);
            case "share":
                return evalPrint(eargs);
            case "shareIdea":
                return evalPrintln(eargs);
            case "add":
                return evalAdd(eargs);
            case "takeaway":
                return evalRemove(eargs);
            case "length":
                return evalLength(eargs);
            case "read":
                return evalReadInput(eargs);
            case "type":
                return evalType(eargs);
        }
        Lexeme closure = eval(tree.left, env);

        Lexeme params = closure.right.right.left;
        Lexeme body = closure.right.right.right;
        Lexeme senv = closure.left;

        Lexeme xenv = e.extendEnv(senv, params, eargs);
        e.insert(new Lexeme("MUTABLE", "this"), xenv, xenv); 
        return eval(body, xenv);
    }

    //type evaluation behavior
    private Lexeme evalType(Lexeme eargs) {
        return new Lexeme("ALPHA", "\"" + eargs.left.type + "\"");
    }

    //variable definition behavior
    private Lexeme evalVarDef(Lexeme tree, Lexeme env) {
        Lexeme val = eval(tree.right, env);
        e.insert(tree.left, val, env);
        return null;
    }

    //decimal point background behavior 
    private Lexeme evalDot(Lexeme tree, Lexeme env) {
        Lexeme object = eval(tree.left, env);
        Lexeme local = e.extendEnv(object, e.getVars(env), e.getVals(env));
        return eval(tree.right, local);
    }
}