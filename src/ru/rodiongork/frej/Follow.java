package ru.rodiongork.frej;


public class Follow extends Elem {
    
    
    public Follow(Regex owner, Elem... elems) {
        super(owner, TYPE_FOLLOW);
        children = elems;
    } // FuzzyRegexFollow
    
    
    @Override
    public double matchAt(int i) {
        StringBuilder s = new StringBuilder();
        double worstResult = 0;
        
        matchStart = i;
        matchLen = 0;
        
        for (int j = 0; j < children.length; j++) {
            double cur = children[j].matchAt(i + matchLen);

            if (cur > worstResult) {
                worstResult = cur;
            } // if
            
            matchLen += children[j].getMatchLen();
            s.append(children[j].getReplacement());
        } // for
        
        matchReplacement = s.toString();
        saveGroup();
        
        return worstResult;
    } // matchAt
    
    
    @Override
    public String toString() {
        return childrenString("(", ")") + super.toString();
    } // toString
    
    
} // class FuzzyRegexFollow
