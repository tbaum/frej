package frej;


abstract class Elem {

    Regex owner;
    int matchStart, matchLen;
    Elem[] children;
    String replacement, matchReplacement;
    char group;
    
    
    Elem(Regex owner) {
        this.owner = owner;
    } // FuzzyRegexElem
    
    
    abstract double matchAt(int i);
    
    
    int getMatchLen() {
        return matchLen;
    } // getMatchLen
    
    
    String childrenString(String prefix, String suffix) {
        StringBuilder s = new StringBuilder(prefix);
        
        for (int i = 0; i < children.length; i++) {
            s.append(children[i].toString());
            s.append(',');
        } // for
        
        s.deleteCharAt(s.length() - 1);
        s.append(suffix);
        
        return s.toString();
    } // childrenString
    
    
    void setReplacement(String repl) {
        replacement = repl;
    } // setReplacement
    
    
    String getReplacement() {
        StringBuilder s = new StringBuilder();
        
        if (replacement == null) {
            
            return getMatchReplacement();
            
        } else {
        
            for (int i = 0; i < replacement.length(); i++) {
                char c = replacement.charAt(i);
                
                if (c == '$' && i < replacement.length() - 1) {
                    c = replacement.charAt(++i);

                    switch (c) {
                    case '$':
                        s.append(getMatchReplacement());
                        break;
                    case '<':
                        s.append(owner.prefix());
                        break;
                    case '>':
                        s.append(owner.suffix());
                        break;
                    default:
                        s.append(owner.getGroup(c));
                        break;
                    } // switch
                    continue;
                } // if
                
                s.append(c);
            } // for
            
        } // else

        return s.toString();
    } //getReplacement
    
    
    String getMatchReplacement() {
        StringBuilder s = new StringBuilder();
        
        if (matchReplacement != null) {
            return matchReplacement;
        } // if
        
        for (int i = 0; i < matchLen; i++) {
            s.append(owner.tokens[matchStart + i]);
            s.append(' ');
        } // for
        if (s.length() > 0) {
            s.deleteCharAt(s.length() - 1);
        } // if
        
        return s.toString();
    } // getMatchReplacement
    
    
    void saveGroup() {
        
        if (group == 0) {
            return;
        } // if
        
        owner.setGroup(group, getMatchReplacement());
    } // saveGroup
    
    
    void setGroup(char g) {
        group = g;
    } // setGroup
    
    
    @Override
    public String toString() {
        return (group != 0 ? "~" + group : "") + (replacement != null ? "|" + replacement : "");
    } // toString
    
    
} // class FuzzyRegexElem
