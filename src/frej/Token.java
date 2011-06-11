package frej;

import frej.fuzzy.Fuzzy;


class Token extends Elem {

    
    protected String token;
    protected boolean partial;
    
    
    public Token(Regex owner, String token) {
        super(owner, TYPE_TOKEN);
        if (token.charAt(token.length() - 1) != '*') {
            this.token = token;
            partial = false;
        } else {
            this.token = token.substring(0, token.length() - 1);
            partial = true;
        } // else
    } // FuzzyRegexToken

    
    @Override
    public double matchAt(int i) {
        
        matchStart = i;
        matchLen = 0;

        if (i >= owner.tokens.length) {
            return Double.POSITIVE_INFINITY;
        } // if
        
        if (partial && owner.tokens[i].length() > token.length()) {
            Fuzzy.similarity(owner.tokens[i].substring(0, token.length()), token);
        } else {
            Fuzzy.similarity(owner.tokens[i], token);
        } // else
        
        if (Fuzzy.result <= Fuzzy.threshold) {
            matchLen = 1;
        } // if

        saveGroup();
        
        return Fuzzy.result;
    } // matchAt
    
    
    @Override
    public String toString() {
        return token + super.toString();
    } // toString
    
    
} // class FuzzyRegexToken
