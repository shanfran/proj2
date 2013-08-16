import java.util.*;

public class Proof {
	
	LinkedList<String> commands;
	LinkedList<Expression> steps;
	LinkedList<LineNumber> lineNumbers;
	ArrayList<String> history;
	Stack<Expression> subproofs;
	TheoremSet myTheorems;
	boolean finished_subproof;
	boolean started_subproof;
	
	public Proof (TheoremSet theorems) {
		commands = new LinkedList<String>(); 
		steps = new LinkedList<Expression>();
		lineNumbers = new LinkedList<LineNumber>();
		history = new ArrayList<String>();
		subproofs = new Stack<Expression>();
		myTheorems = theorems;
		started_subproof = false;
		finished_subproof = false;
	}

	public LineNumber nextLineNumber() {
		if (lineNumbers.isEmpty()) {
			LineNumber l = new LineNumber(1);
			l.setDigit(0, 1);
			lineNumbers.add(l);
			return l;
		}
		if (lineNumbers.size() == 1) {
			LineNumber l = new LineNumber(1);
			l.setDigit(0, 2);
			lineNumbers.add(l);
			return l;
		}
		LineNumber previous = lineNumbers.getLast();
		int lineLength = previous.length();
		if (started_subproof) {
			started_subproof = false;
			lineLength += 1;
		} else if (finished_subproof) {
			finished_subproof = false;
			lineLength -= 1;
		}
		LineNumber next = new LineNumber(lineLength);
		for (int i = 0; i < Math.min(previous.length(), lineLength); i++)
			next.setDigit(i, previous.getDigit(i));
		next.increment();
		lineNumbers.add(next);
		return next;
	}

	public void extendProof (String x) throws IllegalLineException, IllegalInferenceException { //x is the input the user types in
		if (x == null) {
			throw new IllegalLineException("Null input.");
		}
		String[] inputLine = x.split(" ");
		if (inputLine.length == 0)
			throw new IllegalLineException("No command found.");

		String reason = inputLine[0];
		String rest = "";
		
		for(int i = 1; i < inputLine.length; i++) {
			rest += inputLine[i];
			if (i < inputLine.length - 1)
				rest += " ";
		}
		
		if(reason.equals("show")) {
			if (inputLine.length < 2)
				throw new IllegalLineException("Nothing to show.");
			show(new Expression(rest));
		} else if (reason.equals("assume")) {
			if (inputLine.length < 2)
				throw new IllegalLineException("Nothing to assume.");
			assume(new Expression(rest));
		} else if (reason.equals("mp") || reason.equals("mt") || reason.equals("co")) {
			String l1, l2;
			try {
				l1 = inputLine[1];
				l2 = inputLine[2];
			} catch (Exception e) {
				throw new IllegalLineException("Not enough line numbers.");
			}
			rest = "";
			for(int i = 3; i < inputLine.length; i++) {
				rest += inputLine[i];
				if (i < inputLine.length - 1)
					rest += " ";
			}
			infer(reason, new LineNumber(l1), new LineNumber(l2), new Expression(rest));
		} else if (reason.equals("repeat") || reason.equals("ic")) {
			String line;
			try {
				line = inputLine[1];
			} catch (Exception e) {
				throw new IllegalLineException("Not enough line numbers.");	
			}
			
			rest = "";
			for(int i = 2; i < inputLine.length; i++) {
				rest += inputLine[i];
				if (i < inputLine.length - 1)
					rest += " ";
			}
			
			if (reason.equals("ic"))
				ic(new LineNumber(line), new Expression(rest));
			else
				repeat(new LineNumber(line), new Expression(rest));
		} else if (myTheorems.contains(reason)) {
			
		} else
			throw new IllegalLineException("Unknown reason.");
		commands.add(reason);
		history.add(x);
		if (!subproofs.isEmpty() && subproofs.peek().equals(steps.getLast()) && !commands.getLast().equals("show")) {				
			subproofs.pop();
			finished_subproof = true;
		}
	}

	public String toString ( ) {
		String rtn = "";
		for(int i = 0; i < history.size(); i++) {
			rtn += lineNumbers.get(i) + "\t" + history.get(i) + "\n";
		}
		return rtn;
	}

	public boolean isComplete ( ) {
		if (steps.size() <= 1)
			return false;
		return steps.getFirst().equals(steps.getLast()) && subproofs.isEmpty();
	}

	public void show (Expression e) {
		if(!commands.contains("show")) {
			steps.add(e);
		}
		else {
			subproofs.push(e);
			steps.add(e);
			started_subproof = true;
		}
	}
	
	public void assume (Expression e) throws IllegalLineException{
		if (!commands.getLast().equals("show"))
			throw new IllegalLineException("*** can only assume immediately after a show");
		steps.add(e);
	}
	
	public void infer(String reason, LineNumber line1, LineNumber line2, Expression exp) throws IllegalLineException, IllegalInferenceException{
		Expression e1, e2;
		try {
			e1 = steps.get(lineNumbers.indexOf(line1));
		}
		catch (Exception e) {
			throw new IllegalLineException("*** inaccessible line: "+line1);
		}
		try {
			e2 = steps.get(lineNumbers.indexOf(line2));
		}
		catch (Exception e) {
			throw new IllegalLineException("*** inaccessible line: "+line2);
		}
		if (reason.equals("mp")) {
			if (e1.myOperator.equals("=>") && e1.leftOperand.equals(e2) && e1.rightOperand.equals(exp))
				steps.add(exp);
			else if (e2.myOperator.equals("=>") && e2.leftOperand.equals(e1) && e2.rightOperand.equals(exp))
				steps.add(exp);
			else
				throw new IllegalInferenceException("*** bad inference");
		} else if (reason.equals("mt")) {
			if (e1.myOperator.equals("=>") && e2.myOperator.equals("~") && exp.equals(new Expression("~", e1.leftOperand)))
				steps.add(exp);
			else if (e2.myOperator.equals("=>") && e1.myOperator.equals("~") && exp.equals(new Expression("~", e2.leftOperand)))
				steps.add(exp);
			else
				throw new IllegalInferenceException("*** bad inference");
		} else if (reason.equals("co")) {
			if (e1.myOperator.equals("~") && e1.leftOperand.equals(e2))
				steps.add(exp);
			else if (e2.myOperator.equals("~") && e2.leftOperand.equals(e1))
				steps.add(exp);
			else
				throw new IllegalInferenceException("*** bad inference");
		} else
			throw new IllegalInferenceException("*** bad inference");
	}
	
	public void ic(LineNumber line, Expression exp) throws IllegalLineException, IllegalInferenceException {
		Expression exp2;
		try	{
			exp2 = steps.get(lineNumbers.indexOf(line));
		}
		catch (Exception e) {
			throw new IllegalLineException("*** inaccessible line: "+line);
		}	
		if (!exp.myOperator.equals("=>") || !exp.rightOperand.equals(exp2))
			throw new IllegalInferenceException("*** bad inference");
		steps.add(exp);
	}
	
	public void repeat(LineNumber line, Expression exp) {
		
		} else {
				throw new IllegalInferenceException("Illegal Repeat Statement");
		}
		
	}
}
