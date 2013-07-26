
public class ExpressionTest {
	public static void main(String[] args) {
		try {
			Expression e = new Expression("(~p=>(~q=>(~p&~q)))");
			Expression.print(e);
			e = new Expression("p");
			Expression.print(e);
		} catch (IllegalLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
