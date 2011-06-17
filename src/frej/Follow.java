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
            
            public PartMatcher() {
                len = 0;
                res = 1;
            } // PartMatcher
            
            public PartMatcher(PartMatcher copy) {
                len = copy.len;
                res = copy.res;
                s.append(copy.s);
            } // PartMatcher
            
            public double matchAtFrom(int i, int j) {
                
                
                while(j < children.length) {
                    double cur;
                    cur = children[j].matchAt(i + len);
                    if (children[j] instanceof Optional) {
                        PartMatcher incl, excl;
                        incl = new PartMatcher(this);
                        incl.res *= 1 - Math.min(cur, 1);
                        incl.len = len + children[j].getMatchLen();
                        incl.s.append(children[j].getReplacement());
                        excl = new PartMatcher(this);
                        incl.matchAtFrom(i, j + 1);
                        excl.matchAtFrom(i, j + 1);
                        if (incl.res <= excl.res) {
                            res = incl.res;
                            len = incl.len;
                            s.replace(0, s.length(), incl.s.toString());
                        } else {
                            res = excl.res;
                            len = excl.len;
                            s.replace(0, s.length(), excl.s.toString());
                        } // else
                        break;
                    } // if
                    res *= 1 - Math.min(cur, 1);
                    len += children[j].getMatchLen();
                    s.append(children[j].getReplacement());
                    j++;
                } // while
                
                if (j >= children.length) {
                    res = 1 - Math.pow(res, 1.0 / len);
                } // if
                
                return res;
            } // matchAtFrom
            
        } // class PartMatcher
        
        PartMatcher pm = new PartMatcher();
        pm.matchAtFrom(i, 0);
        matchStart = i;
        matchLen = pm.len;
        matchReplacement = pm.s.toString();
        saveGroup();
        
        return pm.res;
    } // matchAt
    
    
    @Override
    public String toString() {
        return childrenString("(", ")") + super.toString();
    } // toString
    
    
} // class FuzzyRegexFollow
