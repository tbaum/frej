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
    protected Map<Character,String> groups = new HashMap<Character,String>();
    protected String allowedPunct = "/-";
    protected double threshold = Fuzzy.threshold;
    
    
    /**
     * Creates new regular expression (builds it as a tree of elements) from
     * presented pattern. Behavior is undefined if pattern is incorrect.
     */
    public Regex(String pattern) {
        pattern = fixPattern(pattern);
        root = parse(pattern);
    } // FuzzyRegex
    
    
    protected String fixPattern(String pattern) {
        StringBuilder b = new StringBuilder();
        Stack<Character> brackets = new Stack<Character>();
        int slashes = 0;
        boolean comment = false;
        char prevChar = 0;
        int lineCount = 1, posCount = 0;
        
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            posCount++;
            
            if (comment) {
                if (c == '\r' || c == '\n') {
                    comment = false;
                } else {
                    continue;
                } // else
            } else {
                if (prevChar == '/' && c == '/') {
                    b.deleteCharAt(b.length() - 1);
                    comment = true;
                    continue;
                } // if
            } // else
            
            prevChar = c;
            b.append(c);
            
            if (c == '\\') {
                slashes++;
                continue;
            } // if
            
            if ((slashes & 1) == 0) {
                if (Character.isWhitespace(c)) {
                    if (c == '\r' || c == '\n') {
                        lineCount++;
                        posCount = 0;
                    } // if
                    b.deleteCharAt(b.length() - 1);
                } else if ("({[".indexOf(c) >= 0) {
                    brackets.push(Character.valueOf(c));
                    b.replace(b.length() - 1, b.length(), "(");
                } else if (")}]".indexOf(c) >= 0) {
                    if (brackets.empty() || brackets.peek() != "({[".charAt(")}]".indexOf(c))) {
                        throw new IllegalArgumentException("Mismatched bracket at " + lineCount + ":" + posCount);
                    } // if
                    brackets.pop();
                    b.replace(b.length() - 1, b.length(), ")");
                } // if
            } else {
                // opening and closing round brackets should be presented as \o and \c
                switch (c) {
                    case '(':
                        b.replace(b.length() - 1, b.length(), "o");
                        break;
                    case ')':
                        b.replace(b.length() - 1, b.length(), "c");
                        break;
                } // switch
            } // else
            
            slashes = 0;
        } // for
        
        return b.toString();
    } // fixPattern
    
    
    protected String eliminateEscapes(String s) {
        StringBuilder b = new StringBuilder();
        boolean prevSlash = false;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (!prevSlash) {
                if (c == '\\') {
                    prevSlash = true;
                } else if (c == '_') {
                    b.append(' ');
                } else {
                    b.append(c);
                } // else
            } else {
                prevSlash = false;
                if (c == 'r') {
                    b.append('\r');
                } else if (c == 'n') {
                    b.append('\n');
                } else if (c == 'o') {
                    b.append('(');
                } else if (c == 'c') {
                    b.append(')');
                } else {
                    b.append(c);
                } // else
            } // else
        } // for
        
        return b.toString();
    } // eliminateEscapes
    
    
    protected Elem parse(String pattern) {
        Elem retVal;
        String[] parts;
        char g = 0;
        
        parts = pattern.split("\\|(?!.*[^\\\\]\\))", 2); // split by "pipe" symbol not enclosed in brackets
        
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
                
            case '!':
                retVal = new Regular(this, eliminateEscapes(expr));
                break;
                
            case '#':
                retVal = new Numeric(this, expr);
                break;
                
            default:
                retVal = new Follow(this, parseList(pattern.toString().substring(1, pattern.length() - 1)));
                break;
                
            } // switch
            
        } else {
            retVal = new Token(this, eliminateEscapes(pattern)); 
        } // else
        
        if (parts.length > 1) {
            retVal.setReplacement(eliminateEscapes(parts[1]));
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
    } // parseList
    
    
    /**
     * Checks whether this regexp matches to any subsequence in presented string.
     * @return number of token from which best match starts or (-1) if all matches
     * are bad enough.
     */
    public int presentInSequence(String seq) {
        double bestResult = Double.POSITIVE_INFINITY;
        int bestPos = -1;
        int bestLen = -1;
        
        groups.clear();
        splitTokens(seq);
        
        for (int i = 0; i < tokens.length; i++) {
            double cur = root.matchAt(i);
            if (cur < bestResult) {
                bestResult = cur;
                bestPos = i;
                bestLen = root.getMatchLen();
            } // if
        } // for
        
        if (bestResult > threshold) {
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
        groups.clear();
        splitTokens(seq);
        matchResult = root.matchAt(0);
        if (matchResult > threshold || root.getMatchLen() != tokens.length) {
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
        groups.clear();
        splitTokens(seq);
        matchResult = root.matchAt(0);
        if (matchResult > threshold) {
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

    
    /**
     * Returns value of threshold used in matching methods to decide whether matching result
     * signifies match or mismatch. By default equals to frej.Fuzzy.threshold.
     */
    public double getThreshold() {
        return threshold;
    } // getThreshold
    
    
    /**
     * Sets value of threshold used in matching methods to decide whether matching result
     * signifies match or mismatch. By default equals to frej.Fuzzy.threshold.
     */
    public void setThreshold(double t) {
        threshold = t;
    } // setThreshold
    
    
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


    protected String getGroup(char g) {
        String s = groups.get(Character.valueOf(g));
        if (s == null) {
            return "";
        } // if
        return s;
    } // getGroup


    protected void setGroup(char g, String s) {
        groups.put(Character.valueOf(g), s);
    } // setGroup


} // class FuzzyRegex
