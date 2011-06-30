package test;


import java.util.*;
import java.io.*;
import java.nio.charset.*;

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
        int matchMode = 0;
        
        if (args.length < 1) {
            System.err.println("Pattern should be specified. Example:");
            System.err.println("    java -jar frej.jar \"(give,(#)~A,(^doll*,buck*,usd))|got_$A_dollars\"");
            System.err.println("        give 5 dollars");
            System.err.println("        giv 70 bucks");
            System.err.println("        gave 1000 usd");
            System.err.println();
            System.err.println("Pattern also could be specified in text file:");
            System.err.println("    java -jar frej.jar --pattern=<filename> [--charset=<charset-name>]");
            System.err.println();
            System.err.println("Matching mode could be selected (default='exact'):");
            System.err.println("    java -jar frej.jar --mode=(exact|start|substr)");
            return;
        } // if
        
        if (fetchParameter(args, "mode") != null) {
            String mode = fetchParameter(args, "mode");
            if (mode.equals("exact")) {
                matchMode = 0;
            } else if (mode.equals("start")) {
                matchMode = 1;
            } else if (mode.equals("substr")) {
                matchMode = 2;
            } else {
                System.err.println("Error in mode (" + mode +")");
                return;
            } // else
        } // if
        
        if (fetchParameter(args, "pattern") != null) {
            String pattern = loadPattern(fetchParameter(args, "pattern"), fetchParameter(args, "charset"));
            if (pattern == null) {
                System.err.println("Problem with loading pattern from file");
                System.exit(1);
            } // if
            regex = new Regex(pattern);
        } else {
            regex = new Regex(args[0]);
        } // else
        
        try {

            while (!(line = in.nextLine()).isEmpty()) {
                boolean b = false;
                switch (matchMode) {
                case 0:
                    b = regex.match(line);
                    break;
                case 1:
                    b = regex.matchFromStart(line);
                    break;
                case 2:
                    b = regex.presentInSequence(line) >= 0;
                } // switch
                if (b) {
                    System.out.println(regex.getReplacement());
                } // if
                if (debug) {
                    System.out.println(regex.getMatchResult());
                } // if
            } // while
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {}
    } // main
    
    
    private static String fetchParameter(String[] cmdLineArgs, String name) {
        
        name = "--" + name + "=";
        
        for (String s : cmdLineArgs) {
            if (s.startsWith(name)) {
                return s.substring(name.length());
            } // if
        } // for
        
        return null;
    } // fetchParameter
    
    
    private static String loadPattern(String fileName, String charsetName) {
        BufferedReader in;
        StringBuilder b;
        
        if (fileName == null) {
            return null;
        } // if
        
        if (charsetName == null) {
            charsetName = Charset.defaultCharset().name();
        } // if
        
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName(charsetName)));
        } catch (FileNotFoundException e) {
            return null;
        } // catch
        
        b = new StringBuilder();
        
        while(true) {
            String s;
            
            try {
                s = in.readLine();
            } catch (IOException e) {
                return null;
            } // catch
            
            if (s == null) {
                if (b.length() > 0) {
                    b.deleteCharAt(b.length() - 1);
                } // if
                break;
            } // if
            
            b.append(s);
            b.append('\r');
        } // while
        
        return b.toString();
    } // loadPattern
    

} // class Main
