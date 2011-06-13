package frej;


class Both extends Elem {

    
    public Both(Regex owner, Elem... elems) {
        super(owner, TYPE_BOTH);
        
        if (elems.length != 2) {
            throw new RuntimeException("Incorrect subexpressions number for BOTH element");
        } // if
        
        children = elems;
    } // FuzzyRegexBoth
    
    
    @Override
    public double matchAt(int i) {
        double res1, res2, v;
        int len1, len2;
        
        matchStart = i;
        
        v = 1 - Math.min(children[0].matchAt(i), 1);
        v *= 1 - Math.min(children[1].matchAt(i + children[0].getMatchLen()), 1);

        res1 = 1 - Math.sqrt(v);
        len1 = children[0].getMatchLen() + children[1].getMatchLen();

        v = 1 - Math.min(children[1].matchAt(i), 1);
        v *= 1 - Math.min(children[0].matchAt(i + children[1].getMatchLen()), 1);

        res2 = 1 - Math.sqrt(v);
        len2 = children[0].getMatchLen() + children[1].getMatchLen();

        if (res1 < res2 || (res1 == res2 && len1 > len2)) {
            len2 = len1;
            res2 = res1;
        } // if
        
        matchLen = len2;

        saveGroup();
        
        return res2;
    } // matchAt


    @Override
    public String toString() {
        return childrenString("(=", ")") + super.toString();
    } // toString
    
    
} // class FuzzyRegexBoth
