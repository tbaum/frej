package ru.rodiongork.frej.strutil;


import java.util.*;


public class Fuzzy {
    
    
    public static final int MAX_PATTERN = 64;
    public static final int MAX_SOURCE = 256;
    
    private static final int BIG_VALUE = 1000000;
    public static int resultStart, resultEnd, resultIndex;
    public static String matchedPattern;
    public static double result, threshold = 0.34;
    private static int[][] e = new int[MAX_PATTERN + 1][MAX_SOURCE + 1];
    private static WayType[][] w = new WayType[MAX_PATTERN + 1][MAX_SOURCE + 1];
    
    
    private static enum WayType {
        TRANSIT, INSERT, DELETE, SUBST, SWAP
    } // enum WayType

    
    public static int substrStart(CharSequence source, CharSequence pattern) {
        
        if (containability(source, pattern) < threshold)
            return resultStart;
        
        return -1;
    } // find
    

    public static int substrEnd(CharSequence source, CharSequence pattern) {
        
        if (containability(source, pattern) < threshold)
            return resultEnd;
        
        return -1;
    } // find
    

    public static boolean equals(CharSequence source, CharSequence pattern) {
        return similarity(source, pattern) < threshold;
    } // compare
    
    
    public static boolean containsOneOf(CharSequence source, CharSequence... patterns) {
        
        for (CharSequence p : patterns) {
            if (containability(source, p) < threshold) {
                return true;
            } // if
        } // for
        
        return false;
    } // containsOneOf
    
    
    public static double containability(CharSequence source, CharSequence pattern) {
        int m = pattern.length() + 1;
        int n = source.length() + 1;
        int best, start;
        char p, s, p1, s1;

        for (int x = 0; x < n; x++) {
            e[0][x] = 0;
        } // for

        p = 0;
        
        for (int y = 1; y < m; y++) {
            e[y][0] = y;
            w[y][0] = WayType.DELETE; 

            p1 = p;
            p = Character.toUpperCase(pattern.charAt(y - 1));
            s = 0;
            
            for (int x = 1; x < n; x++) {
                int cost;
                int val, temp;

                s1 = s;
                s = Character.toUpperCase(source.charAt(x - 1));
                
                cost =  (p == s) ? 0 : 1;
                
                val = e[y - 1][x - 1] + cost;
                w[y][x] = WayType.SUBST;

                temp = e[y - 1][x] + 1;
                if (val > temp) {
                    val = temp;
                    w[y][x] = WayType.DELETE;
                } // if
                
                temp = e[y][x - 1] + 1;
                if (val > temp) {
                    val = temp;
                    w[y][x] = WayType.INSERT;
                } // if

                if (p1 == s && p == s1) {
                    temp = e[y - 2][x - 2] + cost;
                    if (val > temp) {
                        val = temp;
                        w[y][x] = WayType.SWAP;
                    } // if
                } // if
                
                e[y][x] = val;
            } // for

        } // for

        best = n - 1;
        for (int x = 0; x < n; x++) {
            if (e[m - 1][x] < e[m - 1][best]) {
                best = x;
            } // if
        } // for

        start = best;
        for (int y = m - 1; y > 0;) {
            switch (w[y][start]) {
            case INSERT:
                start--;
                break;
            case DELETE:
                y--;
                break;
            case SWAP:
                y-=2;
                start-=2;
                break;
            default:
                start--;
                y--;
                break;
            } // switch
        } // for

        resultStart = start + 1;
        resultEnd = best;

        return (result = e[m - 1][best] / (double) pattern.length());
    } // containability

    
    public static double bestEqual(String string, Object patterns, boolean equality) {
        String[] array;
        double value = Double.POSITIVE_INFINITY;
        
        if (patterns instanceof String[]) {
            array = (String[]) patterns;
        } else if (patterns instanceof Collection) {
            Collection<?> c = (Collection<?>) patterns; 
            array = c.toArray(new String[c.size()]);
        } else {
            throw new IllegalArgumentException();
        } // else
        
        resultIndex = -1;
        
        for (int i = 0; i < array.length; i++) {
            double cur = equality ? similarity(string, array[i]) : containability(string, array[i]);
            if (cur < value) {
                value = cur;
                resultIndex = i;
                matchedPattern = array[i];
            } // if
        } // for
        
        return value;
    } // bestEqual
    
    
    public static double similarity(CharSequence source, CharSequence pattern) {
        int m = pattern.length() + 1;
        int n = source.length() + 1;
        char s, p, s1, p1;
        
        for (int x = 0; x < n; x++) {
            e[0][x] = x; 
        } // for
        
        p = 0;
        
        for (int y = 1; y < m; y++) {
            e[y][0] = y;
            
            p1 = p;
            p = Character.toUpperCase(pattern.charAt(y - 1));
            s = 0;

            for (int x = 1; x < n; x++) {
                int val = BIG_VALUE;
                int cost;
                
                s1 = s;
                s = Character.toUpperCase(source.charAt(x - 1));
                
                cost = (s == p) ? 0 : 1;

                val = e[y - 1][x - 1] + cost;
                val = Math.min(e[y][x - 1] + 1, val);
                val = Math.min(e[y - 1][x] + 1, val);
                
                if (s == p1 && p == s1) {
                    val = Math.min(e[y - 2][x - 2] + cost, val);
                } // if

                e[y][x] = val;
                
            } // for
        } // for
        
        return (result = 2 * e[m - 1][n - 1] / (m + n - 2.0));
    } // similarity


} // Strings
