package frej;

import frej.fuzzy.Fuzzy;


final class Token extends Elem {

    
    private String token;
    private boolean partial;
    
    
    Token(Regex owner, String token) {
        super(owner);
        changePattern(token);
    } // FuzzyRegexToken

    
    @Override
    double matchAt(int i) {
        
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
        
        matchLen = 1;

        saveGroup();
        
        return Fuzzy.result;
    } // matchAt
    
    
    @Override
    String getReplacement() {
        
        if (replacement == null) {
            
            return partial ? getMatchReplacement() : token;
            
        } // if
        
        return super.getReplacement();
    } //getReplacement
    
    
    @Override
    public String toString() {
        return token + super.toString();
    } // toString


    void changePattern(String pattern) {
        if (pattern == null || pattern.charAt(pattern.length() - 1) != '*') {
            token = pattern;
            partial = false;
        } else {
            this.token = pattern.substring(0, pattern.length() - 1);
            partial = true;
        } // else
    } // changePattern
    
    
} // class FuzzyRegexToken
