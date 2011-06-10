package test;

import ru.rodiongork.frej.*;


public class CmdLineTest {

    // for debug purposes
    public static void main(String... args) {
        // (#1:20# > линия) <> (ВО ^ В > О)
        Regex fr = new Regex("(=((#1:20),linia),(^VO,(V,O)))");
        System.out.println(fr.pattern());
        
        System.out.println(fr.match("3 lilia V.O."));
        System.out.println(fr.match("3 lilia VO"));
        System.out.println(fr.match("V O 3linea"));
        System.out.println(fr.match("3V O linea"));
        
        fr = new Regex("(st,(?M),Jeleznaka)");
        System.out.println(fr.pattern());
        
        System.out.println(fr.match("st Jeleznaka"));
        System.out.println(fr.match("st Jilliznika"));
        System.out.println(fr.match("st m.Jileznika"));

        fr = new Regex("(=((#1:13),(?((?-),a)),(^krasnoarm*,(kr*,(?-),arm*))),(?ul*))");
        System.out.println(fr.pattern());
        
        System.out.println(fr.match("2a krasna armeiskaa ul") + " " + fr.matchedTokenCount());
        System.out.println(fr.match("20 krasno-armeiskaa ul") + " " + fr.matchedTokenCount());
        System.out.println(fr.match("7-a krasna-ormeiskaa ul") + " " + fr.matchedTokenCount());
        System.out.println(fr.match("6- krasnoarmeiskaa ul") + " " + fr.matchedTokenCount());
        System.out.println(fr.match("uleca 6 krasoarmesikaa") + " " + fr.matchedTokenCount());
        System.out.println(fr.match("ul 11 krestnourmeiska") + " " + fr.matchedTokenCount());
        System.out.println(fr.match("4a kraso-armesika") + " " + fr.matchedTokenCount());

        fr = new Regex("(^krasnoarm*,(kr*,(?-),arm*))");
        System.out.println(fr.pattern());
        System.out.println(fr.presentInSequence("20 krasno-armeikaa ul") + " " + fr.getMatchStart() + " " + fr.getMatchEnd());
        System.out.println('"' + fr.prefix() + ',' + fr.suffix() + '"');

        fr = new Regex("((#)~A,(^pes*|p-m,tr*,ost*|ost)~B)|total $A but $B");
        System.out.println(fr.pattern());
        System.out.println(fr.match("7 peskom") + ": " + fr.getReplacement()); 
    } // main
    

} // class CmdLineTest
