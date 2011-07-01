package frej;


final class Both extends Elem {
    
    private Elem expr;
    
    Both(Regex owner, Elem... elems) {
        super(owner);
        
        if (elems.length != 2) {
            throw new RuntimeException("Incorrect subexpressions number for BOTH element");
        } // if
        
        expr = new Any(owner, new Follow(owner, elems[0], elems[1]), new Follow(owner, elems[1], elems[0]));
        
        children = elems;
    } // FuzzyRegexBoth
    
    
    @Override
    double matchAt(int i) {
        double res;
        
        matchStart = i;
        res = expr.matchAt(i);
        matchLen = expr.getMatchLen();
        matchReplacement = expr.getReplacement();
        
        saveGroup();
        
        return res;
    } // matchAt


    @Override
    public String toString() {
        return childrenString("(=", ")") + super.toString();
    } // toString
    
    
} // class FuzzyRegexBoth
