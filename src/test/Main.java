package test;


import java.util.*;

import frej.*;


public class Main {
    
    
    private Main() {}
    
    
    public static void main(String... args) {
        Scanner in = new Scanner(System.in);
        String line;
        Regex regex;
        
        if (args.length < 1) {
            System.err.println("Pattern should be specified. Example:");
            System.err.println("    java -jar frej.jar \"(give,(#)~A,(^doll*,buck*,usd))|got $A dollars\"");
            System.err.println("        give 5 dollars");
            System.err.println("        giv 70 bucks");
            System.err.println("        gave 1000 usd");
            return;
        } // if
        
        regex = new Regex(args[0]);
        
        while (!(line = in.nextLine()).isEmpty()) {
            if (regex.match(line)) {
                System.out.println(regex.prefix() + regex.getReplacement() + regex.suffix());
            } // if
        } // while
        
    } // main
    

} // class Main
