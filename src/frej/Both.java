package frej;


class Both extends Elem {
    
    protected Elem expr;
    
    public Both(Regex owner, Elem... elems) {
        super(owner, TYPE_BOTH);
        
        if (elems.length != 2) {
            throw new RuntimeException("Incorrect subexpressions number for BOTH element");
        } // if
        
        expr = new Any(owner, new Follow(owner, elems[0], elems[1]), new Follow(owner, elems[1], elems[0]));
        
        children = elems;
    } // FuzzyRegexBoth
    
    
    @Override
    public double matchAt(int i) {
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
