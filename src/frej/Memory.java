package frej;


final class Memory extends Elem {

    
    private char groupLetter;
    private Token token;
    
    Memory(Regex owner, String groupName) {
        super(owner);
        token = new Token(owner, null);
        groupLetter = groupName.charAt(0);
    } // Memory
    
    
    @Override
    double matchAt(int i) {
        double retVal;
        token.changePattern(owner.getGroup(groupLetter));
        retVal = token.matchAt(i);
        matchLen = token.getMatchLen();
        return retVal;
    } // matchAt
    
    
    @Override
    String getReplacement() {
        return token.getReplacement();
    } // getReplacement
    
    @Override
    String getMatchReplacement() {
        return token.getMatchReplacement();
    } // getMatchReplacement

    @Override
    public String toString() {
        return "($" + groupLetter + ")" + super.toString();
    } // toString
    
    
    @Override
    void setGroup(char g) {
        token.setGroup(g);
    } // setGroup
    
    
} // Memory
