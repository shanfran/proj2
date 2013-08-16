import java.util.ArrayList;
import java.util.List;

public class LineNumber {
	private List<Integer> numbers;
	List<String> lines;

	public LineNumber() {
		numbers = new ArrayList<Integer>();
		lines = new ArrayList<String>();
		numbers.add(1);
		lines.add(toString());
	}

	public LineNumber(String str) throws IllegalLineException {
		//assert isLegal(str, ln);
		numbers = new ArrayList<Integer>();
		lines = new ArrayList<String>();
		if (!isLegal(str)){
			throw new IllegalLineException("Line not valid.");
		}
		String[] strpts = str.split(".");
		for (String part : strpts) {
			numbers.add(Integer.parseInt(part));
		}
		lines.add(toString());
	}

	public void add() {
		numbers.add(1);
		lines.add(toString());
	}

	// if an inference is valid we drop the last point and increase the last value by 1 
	public void reset() {
		numbers.remove(numbers.size()-1);
		int last = numbers.get(numbers.size()-1);
		numbers.set(numbers.size()-1, last+1);
		lines.add(toString());
	}

	public void increment() {
		if (numbers.size()==1 && numbers.get(0)==1) {
			numbers.set(0, 2);
		} else {
			int last = numbers.get(numbers.size()-1);
			numbers.set(numbers.size()-1, last+1);
		}
		lines.add(toString());
;	}

	public static boolean isLegal(String str){
		int i = 0; String s;
		while (i < str.length()){
			s = str.substring(i,i+1);
			try {
				Integer.parseInt(s);
				i++;
			} catch (NumberFormatException e){
				if (s.equals(".")){
					i++;
					continue;
				} else {
					return false;
				}
			}
		}

		return true;

	}

	public static boolean isLegal(String str, LineNumber ln){
		if (lineStatus(str) > lineStatus(ln.toString())){
			return false;
		}

		return true;

	}

	public static int lineStatus (String str){
		int count = 0;
		for (int i = 0; i < str.length(); i++){
			if (str.charAt(i) == '.'){
				count++;
			}
		}

		return count;

	}

	public String toString() {
		String rtn = "";
		for (int i : numbers) {
			rtn += i + ".";

		}

		return rtn.substring(0, rtn.length()-1);

	}

}
