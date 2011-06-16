package frej;


class Follow extends Elem {
    
    
    public Follow(Regex owner, Elem... elems) {
        super(owner, TYPE_FOLLOW);
        children = elems;
    } // FuzzyRegexFollow
    
    
    @Override
    public double matchAt(int i) {
        
        class PartMatcher {
            public int len;
            public double res;
            public StringBuilder s = new StringBuilder();
            
            protected double matchAtFrom(int i, int j) {
                len = 0;
                res = 1;
                s.setLength(0);
                
                while(j < children.length) {
                    double cur;
                    cur = children[j].matchAt(i + len);
                    if (children[j] instanceof Optional) {
                        
                    } // if
                    res *= 1 - Math.min(cur, 1);
                    len += children[j].getMatchLen();
                    s.append(children[j].getReplacement());
                } // while
                
                return res;
            } // matchAtFrom
            
        } // class PartMatcher
        
        
        
        
        // ==================================
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
