package test;


import java.util.*;

import frej.*;


/**
 * This class uses frej library in the way similar to "grep" tool:
 * you pass regexp pattern as a command line argument (you'd better use quotes)
 * and then enter test lines. For each of lines program attempts to find a
 * matching region according to pattern and if succeeds, return the string
 * with the region changed by "regex.replacement". Try the example presented
 * when you specify no patterns at all. 
 * 
 * @author Rodion Gorkovenko
 */
public class Main {
    

    private static boolean debug = System.getProperties().getProperty("DEBUG") != null;    
    

    private Main() {}
    
    
    /** it is the main (and only) method of a class */
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
        
        try {

            while (!(line = in.nextLine()).isEmpty()) {
                if (regex.match(line)) {
                    System.out.println(regex.prefix() + regex.getReplacement() + regex.suffix());
                } // if
                if (debug) {
                    System.out.println(regex.getMatchResult());
                } // if
            } // while
        } catch (Exception e) {}
    } // main
    

} // class Main
