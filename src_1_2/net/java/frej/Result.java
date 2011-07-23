package net.java.frej;

class Result {
    
    private static final Result[] emptyResultSet = new Result[]{ new Result() };
    
    double res = Double.POSITIVE_INFINITY;
    int len = 0;
    
    static Result[] emptyArray() {
        return emptyResultSet;
    } // emptyArray
    
} // class Result
