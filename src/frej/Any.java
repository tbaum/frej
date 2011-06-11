package frej;


class Any extends Elem {

    
    public Any(Regex owner, Elem... elems) {
        super(owner, TYPE_ANY);
        children = elems;
    } // FuzzyRegexAny
    
    
    @Override
    public double matchAt(int i) {
        double bestResult = Double.POSITIVE_INFINITY;
        int bestNum = -1;
        
        matchStart = i;
        matchLen = 0;
        
        for (int j = 0; j < children.length; j++) {
            double cur = children[j].matchAt(i); 
            if (cur < bestResult) {
                bestNum = j;
                bestResult = cur;
                matchLen = children[j].getMatchLen();
            } // if
        } // for
        
        matchReplacement = (bestNum >= 0) ? children[bestNum].getReplacement() : null;
        saveGroup();
        
        return bestResult;
    } // matchAt
    
    
    @Override
    public String toString() {
        return childrenString("(^", ")") + super.toString();
    } // toString
    
    
} // class FuzzyRegexAny
