package frej;


public class Special extends Elem {

    
    public Special(Regex owner, String extra) {
        super(owner);
    } // Special
    
    
    @Override
    double matchAt(int i) {
        matchLen = 0;
        matchStart = i;
        
        if (i == 0 || i == owner.tokens.length) {
            return 0;
        } // if
        
        return Double.POSITIVE_INFINITY;
    } // matchAt

} // Special
