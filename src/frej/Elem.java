package frej;


class Elem {

    public static final int TYPE_TOKEN = 1;
    public static final int TYPE_BOTH = 2;
    public static final int TYPE_FOLLOW = 3;
    public static final int TYPE_ANY = 4;
    public static final int TYPE_NUM = 5;
    public static final int TYPE_OPTIONAL = 6;
    public static final int TYPE_NUMERIC = 7;
    
    protected int type;
    protected Regex owner;
    protected int matchStart, matchLen;
    protected Elem[] children;
    protected String replacement, matchReplacement;
    protected char group;
    
    
    public Elem(Regex owner, int type) {
        this.owner = owner;
        this.type = type;
    } // FuzzyRegexElem
    
    
    public double matchAt(int i) {
        return Double.POSITIVE_INFINITY;
    } // matchAt
    
    
    public int getMatchLen() {
        return matchLen;
    } // getMatchLen
    
    
    protected String childrenString(String prefix, String suffix) {
        StringBuilder s = new StringBuilder(prefix);
        
        for (int i = 0; i < children.length; i++) {
            s.append(children[i].toString());
            s.append(',');
        } // for
        
        s.deleteCharAt(s.length() - 1);
        s.append(suffix);
        
        return s.toString();
    } // childrenString
    
    
    public void setReplacement(String repl) {
        replacement = repl;
    } // setReplacement
    
    
    public String getReplacement() {
        StringBuilder s = new StringBuilder();
        
        if (replacement == null) {
            
            return getMatchReplacement();
            
        } else {
        
            for (int i = 0; i < replacement.length(); i++) {
                char c = replacement.charAt(i);
                if (c == '$' && i < replacement.length() - 1) {
                    c = replacement.charAt(++i);
                    if (c != '$') {
                        s.append(owner.getGroup(c));
                    } else {
                        s.append(getMatchReplacement());
                    } // else
                    continue;
                } // if
                s.append(c);
            } // for
            
        } // else

        return s.toString();
    } //getReplacement
    
    
    protected String getMatchReplacement() {
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
    
    
    protected void saveGroup() {
        
        if (group == 0) {
            return;
        } // if
        
        owner.setGroup(group, getMatchReplacement());
    } // saveGroup
    
    
    public void setGroup(char g) {
        group = g;
    } // setGroup
    
    
    @Override
    public String toString() {
        return (group != 0 ? "~" + group : "") + (replacement != null ? "|" + replacement : "");
    } // toString
    
    
} // class FuzzyRegexElem
