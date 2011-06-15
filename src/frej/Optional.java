package frej;


class Optional extends Elem {
    
    
    protected Elem child;
    
    
    public Optional(Regex owner, Elem elem) {
        super(owner, TYPE_OPTIONAL);
        child = elem;
    } // FuzzyRegexOptional
    
    @Override
    public double matchAt(int i) {
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
