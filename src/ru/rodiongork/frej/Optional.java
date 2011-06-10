package ru.rodiongork.frej;

import ru.rodiongork.frej.strutil.Fuzzy;


public class Optional extends Elem {
    
    
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
        
        if (res <= Fuzzy.threshold) {
            matchLen = child.getMatchLen();
            matchReplacement = child.getReplacement();
        } else {
            matchLen = 0;
            matchReplacement = null;
            res = 0;
        } // else
        
        saveGroup();
        
        return res;
    } // matchAt
    
    
    @Override
    public String toString() {
        return "(?" + child.toString() + ")" + super.toString();
    } // toString
    
    
} // class FuzzyRegexOptional
