package frej;


import java.util.*;

import frej.fuzzy.*;


/*
 * Pattern rules:
 * 
 * before matching string is cleared of any punctuation characters and split to tokens, each consisting of
 * sequence of characters of similar class  
 * 
 * "literal" - should approximately match one token...  
 * (^regex1,regex2,...,regexN) - should match any of subexpressions
 * (=regexA,regexB) - should match two consecutive expressions in any order
 * (>regex1,regex2,...,regexN) - should match all subexpressions in specified order
 * (*regex) - may match subexpression (return true in any case)
 * (#MIN:MAX) - should match token with number in specified range (also (#MAX) or simply (#)) 
 */

/**
 * Class represents fuzzy regular expression at whole.
 * 
 * Pattern of fuzzy regexp is passed as string to constructor.
 * 
 * Then any string could be checked against this regexp with the
 * help of match, matchFromStart or presentInSequence methods.
 * 
 * After matching it is possible to receive replacement for matched
 * region via getReplacement method.
 * 
 * Few more auxiliary methods provided for handling parts of original
 * string and result.
 * 
 * @author Rodion Gorkovenko
 */
public class Regex {
    
    
    protected static final int CHAR_CLASS_OTHER = 0;
    protected static final int CHAR_CLASS_DIGIT = 1;
    protected static final int CHAR_CLASS_LETTER = 2;
    protected static final int CHAR_CLASS_PUNCT = 3;
    
    protected Elem root;
    protected String[] tokens;
    protected int[] tokenPos;
    protected double matchResult;
    protected String original;
    protected int firstMatched, lastMatched;
    protected String[] groups = new String['Z' - 'A' + 1];
    protected String allowedPunct = "/-";
    
    
    /**
     * Creates new regular expression (builds it as a tree of elements) from
     * presented pattern. Behavior is undefined if pattern is incorrect.
     */
    public Regex(String pattern) {
        root = parse(pattern);
    } // FuzzyRegex
    
    
    protected Elem parse(String pattern) {
        Elem retVal;
        String[] parts = pattern.split("\\|(?!.*\\))", 2);
        char g = 0;
        
        pattern = parts[0];
        
        if (pattern.length() > 1 && pattern.charAt(pattern.length() - 2) == '~') {
            g = pattern.charAt(pattern.length() - 1);
            pattern = pattern.substring(0, pattern.length() - 2);
        } // if
        
        if (pattern.charAt(0) == '(') {
            String expr;
            
            if (pattern.length() < 2 || pattern.charAt(pattern.length() - 1) != ')') {
                throw new RuntimeException("Unclosed closure!"); 
            } // if
            
            expr = pattern.toString().substring(2, pattern.length() - 1);
            
            switch (pattern.charAt(1)) {
            case '=':
                retVal = new Both(this, parseList(expr));
                break;
                
            case '^':
                retVal = new Any(this, parseList(expr));
                break;
                
            case '?':
                retVal = new Optional(this, parse(expr));
                break;
                
            case '#':
                retVal = new Numeric(this, expr);
                break;
                
            default:
                retVal = new Follow(this, parseList(pattern.toString().substring(1, pattern.length() - 1)));
                break;
                
            } // switch
            
        } else {
            retVal = new Token(this, pattern.toString()); 
        } // else
        
        if (parts.length > 1) {
            retVal.setReplacement(parts[1]);
        } // if
        
        retVal.setGroup(g);
        
        return retVal;
    } // parse
    
    
    protected Elem[] parseList(String pattern) {
        List<Elem> list = new LinkedList<Elem>();
        StringBuilder s = new StringBuilder(pattern);
        int brackets = 0;
        
        s.append(',');
        
        for (int i = 0; s.length() > 0; i++) {
            char c = s.charAt(i);
            
            if (c == '(') {
                brackets++;
            } else if (c == ')') {
                brackets--;
            } else if (c == ',' && brackets == 0) {
                list.add(parse(s.substring(0, i)));
                s.delete(0, i + 1);
                i = -1;
                continue;
            } // else if
        } // for
        
        return list.toArray(new Elem[list.size()]);
    } // pattern
    
    
    /**
     * Checks whether this regexp matches to any subsequence in presented string.
     * @return number of token from which best match starts or (-1) if all matches
     * are bad enough.
     */
    public int presentInSequence(String seq) {
        double bestResult = Double.POSITIVE_INFINITY;
        int bestPos = -1;
        int bestLen = -1;
        
        splitTokens(seq);
        
        for (int i = 0; i < tokens.length; i++) {
            double cur = root.matchAt(i);
            if (cur < bestResult) {
                bestResult = cur;
                bestPos = i;
                bestLen = root.getMatchLen();
            } // if
        } // for
        
        if (bestResult > Fuzzy.threshold) {
            return -1;
        } // if
        
        matchResult = bestResult;
        firstMatched = bestPos;
        lastMatched = bestPos + bestLen - 1; 
        
        return bestPos;
    } // presentInSequence
    
    
    /**
     * Check whether presented string matches with this regexp with all tokens.
     * @return true or false depending on quality of best matching variant.
     */
    public boolean match(String seq) {
        splitTokens(seq);
        matchResult = root.matchAt(0);
        if (matchResult > Fuzzy.threshold || root.getMatchLen() != tokens.length) {
            return false;
        } // if
        firstMatched = 0;
        lastMatched = tokens.length - 1;
        return true;
    } // match
    
    
    /**
     * Checks whether this regexp matches to beginning of presented sequence.
     * @return true or false depending on quality of best match.
     */
    public boolean matchFromStart(String seq) {
        splitTokens(seq);
        matchResult = root.matchAt(0);
        if (matchResult > Fuzzy.threshold) {
            return false;
        } // if
        firstMatched = 0;
        lastMatched = root.getMatchLen() - 1;
        return true;
    } // match
    
    
    /**
     * Returns result of the last match. Result is strongly linked to "distance"
     * between strings being fuzzy matched, i.e. it is roughly count of
     * dissimilarities divided by length of matched region.
     * 
     * For example "Free" and "Frej" match result is 0.25 while "Bold" and "Frej"
     * gives 1.0.
     * @return measure of dissimilarity, 0 means exact match.
     */
    public double getMatchResult() {
        return matchResult;
    } // getMatchResult
    
    
    /**
     * Gives replacement string which is generated after successful match
     * according to rules specified in regexp pattern.
     * @return replacement as a string.
     */
    public String getReplacement() {
        return root.getReplacement();
    } // getReplacement
    
    
    /**
     * Tells the character position (of string which have been matched) from
     * which the match starts.
     * @return position, as integer from range 0 .. seq.length() - 1
     */
    public int getMatchStart() {
        return tokenPos[firstMatched];
    } // getMatchStart
    
    
    /**
     * Tells the character position (of string which have been matched) where
     * last match ends (i.e. position strictly following last character of matched region).
     * @return position, as integer from range 0 .. seq.length() - 1
     */
    public int getMatchEnd() {
        // returns position immediately following 
        return tokenPos[lastMatched] + tokens[lastMatched].length();
    } // getMatchEnd
    
    
    /**
     * Reconstructs pattern which was used for creation of this regexp.
     * @return string representation of pattern.
     */
    public String pattern() {
        return root.toString();
    } // pattern
    
    
    /**
     * Tells number of tokens in matched region (mostly important when pattern
     * contains optional elements).
     * @return token count.
     */
    public int matchedTokenCount() {
        return root.getMatchLen();
    } // matchedTokenCount
    
    
    /**
     * Returns the part of matched string, which precedes matching region.
     * String is trimmed of spaces since spaces are token delimiters.
     * @return beginning of the seq used in presentInSequence etc.
     */
    public String prefix() {
        return original.substring(0, getMatchStart()).trim();
    } // prefix
    
    
    /**
     * Returns the part of matched string, which follows matching region.
     * String is trimmed of spaces since spaces are token delimiters.
     * @return ending of the seq used in presentInSequence etc.
     */
    public String suffix() {
        return original.substring(getMatchEnd()).trim();
    } // prefix
    
    
    /**
     * Allows to set up which punctuation marks are allowed in the tokens
     * By default only slash and dash i.e. punct = "/-"
     */
    public String setAllowedPunctuationMarks(String punct) {
        return allowedPunct = punct;
    } // setAllowedPunctuationMarks

    
    protected void splitTokens(String expr) {
        List<String> tokenList = new LinkedList<String>();
        List<Integer> posList = new LinkedList<Integer>();
        StringBuilder s = new StringBuilder();
        int prevCharClass, charClass;
        
        original = expr;
        
        prevCharClass = CHAR_CLASS_OTHER;
        for (int i = 0; i <= expr.length(); i++) {
            char c;
            
            try {
                c = expr.charAt(i);
            } catch (IndexOutOfBoundsException e) {
                c = 0;
                charClass = CHAR_CLASS_OTHER;
            } // catch

            charClass = Character.isLetter(c) ? CHAR_CLASS_LETTER :
                    Character.isDigit(c) ? CHAR_CLASS_DIGIT : 
                    allowedPunct.indexOf(c) >= 0 ? CHAR_CLASS_PUNCT : CHAR_CLASS_OTHER;

            if ((prevCharClass != charClass || charClass == CHAR_CLASS_PUNCT) && s.length() > 0) {
                tokenList.add(s.toString());
                posList.add(i - s.length());
                s.setLength(0);
            } // if

            prevCharClass = charClass;
            
            if (charClass != CHAR_CLASS_OTHER) {
                s.append(c);
            } // if
            
        } // for
        
        tokens = tokenList.toArray(new String[tokenList.size()]);
        tokenPos = new int[posList.size()];
        for (int i = 0; i < tokenPos.length; i++) {
            tokenPos[i] = posList.remove(0);
        } // for
    } // splitTokens
    
    
} // class FuzzyRegex
