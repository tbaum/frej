package frej;


final class Numeric extends Elem {
    
    
    private int min, max;
    
    
    Numeric(Regex owner, String param) {
        super(owner);
        
        String[] parts = param.split(":");
        
        if (parts.length == 0 || parts[0].isEmpty()) {
            min = Integer.MIN_VALUE;
            max = Integer.MAX_VALUE;
        } else if (parts.length == 1) {
            min = 1;
            max = Integer.parseInt(parts[0]);
        } else {
            min = Integer.parseInt(parts[0]);
            max = Integer.parseInt(parts[1]);
        } // else
        
    } // FuzzyRegexNumeric
    
    
    @Override
    double matchAt(int i) {
        int val;
        
        matchStart = i;
        matchLen = 0;

        if (i >= owner.tokens.length) {
            return Double.POSITIVE_INFINITY;
        } // if
        
        try {
            val = Integer.parseInt(owner.tokens[i]);
        } catch (NumberFormatException e) {
            return Double.POSITIVE_INFINITY;
        } // catch
        
        if (val < min || val > max) {
            return Double.POSITIVE_INFINITY;
        } // if
        
        matchLen = 1;

        saveGroup();
        
        return 0;
    } // matchAt
    
    
    @Override
    public String toString() {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            return "(#)";
        } else if (min == 1) {
            return "(#" + max + ")";
        } // else if
        return "(#" + min + ":" + max + ")" + super.toString(); 
    } // toString
    
    
} // class FuzzyRegexNumeric
