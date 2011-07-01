package frej;


public class Memory extends Token {

    
    protected char groupLetter;
    
    
    public Memory(Regex owner, String groupName) {
        super(owner, TYPE_MEMORY);
        groupLetter = groupName.charAt(0);
    } // Memory
    
    
    @Override
    public double matchAt(int i) {
        token = owner.getGroup(groupLetter);
        return super.matchAt(i);
    } // matchAt
    

    @Override
    public String toString() {
        return "($" + groupLetter + ")" + super.toString();
    } // toString
    
    
} // Memory
