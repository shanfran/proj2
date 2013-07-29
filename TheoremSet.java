public class TheoremSet {
	
	    private final HashMap<String, Expression> theorems; //Creates HashMap with objects

        public TheoremSet ( ) {
        	theorems = new HashMap<String, Expression >( ); //Map to store values
        }
        
        public boolean contains(final String s) { //return true if contains string for key
        	return this.theorems.containsKey(s);
        }
	    

       // public Expression put (String s, Expression e) {
                //return null;
        //}
        
        public void put (final String s, Expression e) { //Store string value
	    	this.theorems.put(s, e);
	    	
        }
        
        public String getAsString(final String key) { //returns the key corresponding to the string
            final Object retrieved = this.theorems.get(key);
            return retrieved instanceof String ? (String) retrieved : null;
        }
        
        public final HashMap<String, Expression> transfer( ) { //transfer strings
        	return theorems;
        }
        	
        }
         
        
        
        

