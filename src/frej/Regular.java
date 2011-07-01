package frej;


class Regular extends Elem {

    
    private String pattern;
    
    
    Regular(Regex owner, String pattern) {
        super(owner);
        this.pattern = pattern;
    } // Regular
    
    
    @Override
    double matchAt(int i) {
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
