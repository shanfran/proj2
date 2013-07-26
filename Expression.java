import java.util.*;
public class Expression {
	
	public Expression leftOperand;
	public Expression rightOperand;
	public String myOperator;
	public Character myVar;
	public boolean isVar = false;
	
	public Expression (String s) throws IllegalLineException {
		
		if (s == null)
			throw new IllegalLineException("Null string.");
		if (s.length() == 0)
			throw new IllegalLineException("Missing expression.");
		char firstChar = s.charAt(0);
		
		//set variables
		if (s.length() == 1) {
			if (!Character.isLetter(firstChar))
				throw new IllegalLineException("Variables must be letters.");
			myVar = firstChar;
			isVar = true;
		}
		
		//handle tildas
		else if (firstChar == '~') {
			myOperator = "~";
			leftOperand = new Expression(s.substring(1,s.length()));
		}
		
		else if (firstChar == '(') {
			if (s.charAt(s.length()-1) != ')')
				throw new IllegalLineException("Missing closing parenthesis.");
			int depth = 1;
			boolean setOperator = false;
			for (int i = 1; i < s.length()-1; i++) {
				char c = s.charAt(i);
				switch(c) {
					case '(' : depth++; break;
					case ')' : depth--; break;
				}
				if (depth == 0)
					throw new IllegalLineException("Extra stuff after expression.");
				if (depth < 0)
					throw new IllegalLineException("Extra closing parentheses.");
				if (depth == 1 && (c == '&' || c == '|' || c == '=')) {
					leftOperand = new Expression(s.substring(1, i));
					setOperator = true;
					switch(c) {
						case '&' : myOperator = "&"; break;
						case '|' : myOperator = "|"; break;
						case '=' : 
							try {
								i++;
								c = s.charAt(i);
								if (c != '>')
									throw new IllegalLineException("Found '=' with no following '>'. Use '=>' for 'implies'.");
							} catch (Exception e) {
								
							}
							myOperator = "=>";
							break;
					}
					//System.out.println(s.substring(i+1, s.length()));
					rightOperand = new Expression(s.substring(i+1, s.length()-1));
				}
			}
			if (!setOperator)
				throw new IllegalLineException("Parentheses invoked, but no operator found.");
		}
		else
			throw new IllegalLineException("Invalid character detected.");
	}
	
	public static void print(Expression e) {
		if (e.isVar)
			System.out.println(e.myVar);
		else {
			System.out.println(e.myOperator);
			printHelper(e.leftOperand, 1);
			printHelper(e.rightOperand, 1);
		}
	}
	
	private static void printHelper(Expression e, int numTabs) {
		if(e == null)
			return;
		for (int i = 0; i < numTabs; i ++)
			System.out.print("\t");
		if (e.isVar)
			System.out.println(e.myVar);
		else {
			System.out.println(e.myOperator);
			printHelper(e.leftOperand, numTabs + 1);
			if (e.myOperator != "~")
				printHelper(e.rightOperand, numTabs + 1);
		}
	}
}