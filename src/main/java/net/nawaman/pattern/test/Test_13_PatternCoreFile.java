package net.nawaman.pattern.test;

import net.nawaman.curry.*;
import net.nawaman.curry.compiler.*;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_13_PatternCoreFile extends TestCaseParser {
    
    static public void main(String ... Args) {
        runTest(Args); 
    }
    
    String ParaserTypeName = "Command";
    /**{@inheritDoc}*/ @Override
    protected String getParserTypeName() {
        return this.ParaserTypeName;
    }
    
    /**{@inheritDoc}*/ @Override
    protected void doTest(final String ... Args) {
        
        AllTests.TheEngine   = null;
        AllTests.TheLanguage = null;
        CurryLanguage.Util.ClearCachedLanguages();
        Engine $Engine = AllTests.getEngine();
        MUnit  $Units  = $Engine.getUnitManager();
        $Units.discoverUsepaths();
        /*
        this.startCapture();
        this.assertValue(
                "{"                                                              + "\n" +
                "   import nawaman~>text=>Text;"                                 + "\n" +
                "   import pattern~>file=>*;"                                    + "\n" +
                "   "                                                            + "\n" +
                "   const aF1 = new Folder();"                                   + "\n" +
                "   const aF2 = new Folder();"                                   + "\n" +
                "   const aF3 = new Folder();"                                   + "\n" +
                "   const aF4 = new File();"                                     + "\n" +
                "   "                                                            + "\n" +
                "   aF1.children[[`home`]]      := aF2;"                         + "\n" +
                "   aF2.children[[`nawaman`]]   := aF3;"                         + "\n" +
                "   aF3.children[[`Crontab-l`]] := aF4;"                         + "\n" +
                "   "                                                            + "\n" +
                "   const aF01 = new Folder();"                                  + "\n" +
                "   "                                                            + "\n" +
                "   aF3.processors <+= @@:New sub(pAsFile :AsFile):void {"       + "\n" +
                "      @:println(`Processing: ` + pAsFile.asFile().fullpath());" + "\n" +
                "   };"                                                          + "\n" +
                "   "                                                            + "\n" +
                "   @:println(`Begin`);"                                         + "\n" +
                "   @:println(`aF1:  ` + aF1.fullpath());"                       + "\n" +
                "   @:println(`aF2:  ` + aF2.fullpath());"                       + "\n" +
                "   @:println(`aF3:  ` + aF3.fullpath());"                       + "\n" +
                "   @:println(`aF4:  ` + aF4.fullpath());"                       + "\n" +
                "   @:println(`aF01: ` + aF01.fullpath());"                      + "\n" +
                "   "                                                            + "\n" +
                "   @:println(`-------------------------------------`);"         + "\n" +
                "   @:println(`(aF1 == aF01):    ` + (aF1 ==  aF01));"           + "\n" +
                "   @:println(`(aF1 === aF01):   ` + (aF1 === aF01));"           + "\n" +
                "   @:println(`aF1.is    (aF01): ` + aF1.is    (aF01));"         + "\n" +
                "   @:println(`aF1.equals(aF01): ` + aF1.equals(aF01));"         + "\n" +
                "   @:println(`-------------------------------------`);"         + "\n" +
                "   @:println(`(aF2 == aF01):    ` + (aF2 ==  aF01));"           + "\n" +
                "   @:println(`(aF2 === aF01):   ` + (aF2 === aF01));"           + "\n" +
                "   @:println(`aF2.is    (aF01): ` + aF2.is    (aF01));"         + "\n" +
                "   @:println(`aF2.equals(aF01): ` + aF2.equals(aF01));"         + "\n" +
                "   "                                                            + "\n" +
                "   @:println(`aF4.processors:  ` + aF4.processors);"            + "\n" +
                "   "                                                            + "\n" +
                "   @:println(`DONE!!!`);"                                       + "\n" +
                "}",
                "DONE!!!"
            );
        this.assertCaptured(
                "Begin" +
           "\n"+"aF1:  /" +
           "\n"+"aF2:  /home" +
           //"\n"+"Processing: /home/nawaman" +
           "\n"+"aF3:  /home/nawaman" +
           "\n"+"aF4:  /home/nawaman/Crontab-l" +
           "\n"+"aF01: /" +
           "\n"+"-------------------------------------" +
           "\n"+"(aF1 == aF01):    true" +
           "\n"+"(aF1 === aF01):   true" +
           "\n"+"aF1.is    (aF01): true" +
           "\n"+"aF1.equals(aF01): true" +
           "\n"+"-------------------------------------" +
           "\n"+"(aF2 == aF01):    false" +
           "\n"+"(aF2 === aF01):   false" +
           "\n"+"aF2.is    (aF01): false" +
           "\n"+"aF2.equals(aF01): false" +
           "\n"+"aF4.processors:  null" +
           "\n"+"DONE!!!"+
           "\n"
            );
        
        this.printSection("END!!!");*/
    }
}
