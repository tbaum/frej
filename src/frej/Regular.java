package frej;


public class Regular extends Elem {

    
    String pattern;
    
    
    public Regular(Regex owner, String pattern) {
        super(owner, TYPE_REGULAR);
        this.pattern = pattern;
    } // Regular
    
    
    @Override
    public double matchAt(int i) {
        matchStart = i;
        matchLen = 0;
        
        if (i >= owner.tokens.length || !owner.tokens[i].matches(pattern)) {
            return Double.POSITIVE_INFINITY;
        } // if
        
        matchLen = 1;
        
        saveGroup();
        
        return 0;
    } // matchAt
    
    
    @Override
    public String toString() {
        return "(!" + pattern + ")" + super.toString();
    } // toString
    
    
} // Regular
