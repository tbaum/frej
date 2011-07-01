package frej;


class Optional extends Elem {
    
    
    private Elem child;
    
    
    Optional(Regex owner, Elem elem) {
        super(owner);
        child = elem;
    } // FuzzyRegexOptional
    
    @Override
    double matchAt(int i) {
        double res;
        
        matchStart = i;
        
        res = child.matchAt(i);
        
        matchLen = child.getMatchLen();
        matchReplacement = child.getReplacement();
        
        saveGroup();
        
        return res;
    } // matchAt
    
    
    @Override
    public String toString() {
        return "(?" + child.toString() + ")" + super.toString();
    } // toString
    
    
} // class FuzzyRegexOptional
