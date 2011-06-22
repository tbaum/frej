package frej;


import java.util.*;


class Any extends Elem {

    
    public Any(Regex owner, Elem... elems) {
        super(owner, TYPE_ANY);
        children = elems;
    } // FuzzyRegexAny
    
    
    @Override
    public double matchAt(int i) {
        double bestResult = Double.POSITIVE_INFINITY;
        Map<Character,String> tempGroups = null;
        Map<Character,String> oldGroups = new HashMap<Character,String>(owner.groups);
        int bestNum = -1;
        
        matchStart = i;
        matchLen = 0;
        
        for (int j = 0; j < children.length; j++) {
            double cur;
            owner.groups = new HashMap<Character,String>(oldGroups);
            cur = children[j].matchAt(i); 
            if (cur < bestResult) {
                bestNum = j;
                bestResult = cur;
                matchLen = children[j].getMatchLen();
                tempGroups = owner.groups;
            } // if
        } // for

        if (tempGroups != null) {
            owner.groups = tempGroups;
        } // if
        
        matchReplacement = (bestNum >= 0) ? children[bestNum].getReplacement() : null;
        saveGroup();
        
        return bestResult;
    } // matchAt
    
    
    @Override
    public String toString() {
        return childrenString("(^", ")") + super.toString();
    } // toString
    
    
} // class FuzzyRegexAny
