package net.nawaman.pattern.test;

import net.nawaman.curry.*;
import net.nawaman.curry.util.*;
import net.nawaman.pattern.*;
import net.nawaman.pattern.test.AllTests.*;

public class Test_12_Map extends TestCaseParser {
    
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
        Engine $Engine = AllTests.getEngine();
        MUnit  $Units  = $Engine.getUnitManager();
        
        $Units.discoverUsepaths();
        
        final PKMap    aPKMap = PKMap.Instance;
        final TypeRef  aTRef1 = aPKMap.getTypeRefFor($Engine, TKJava.TString.getTypeRef(), TKJava.TInteger.getTypeRef());
        final Type     aType1 = (Type)$Engine.execute($Engine.getExecutableManager().newType(aTRef1));
        final MoreData aMData = new MoreData(PKMap.MIName_Comparator, new SerializableComparator<Object>() {
            
            private static final long serialVersionUID = -7638140827480304656L;
            
            public @Override int compare(Object pO1, Object pO2) {
                if (pO1 == pO2)
                    return 0;
                if (pO1 == null)
                    return -1;
                final int aCompareValue = pO1.toString().length() - pO2.toString().length();
                //final int aCompareValue = UObject.compare(pO1, pO2);
                return aCompareValue;
            }
            public @Override boolean equals(Object pObj) {
                return (this == pObj);
            }
        });
        final PortInfo aInfo1 = new PortInfo(aTRef1, aPKMap, true, null, aMData);
        final Port     aPort1 = PortFactory.Factory.newDataHolder(null, $Engine, aType1, true, true, null, aInfo1);
        
        this.assertValue(aPort1, "Port<:curry=>Map<String,int>>");
        
        final Port   aPort1_One1 = (Port)aPort1.config(PKMap.CONFIG_NAME_NEW_MAP_ELEMENT_PORT, new Object[] { "One" });
        final Port   aPort1_One2 = (Port)aPort1.config(PKMap.CONFIG_NAME_NEW_MAP_ELEMENT_PORT, new Object[] { "One" });
        final Port   aPort1_One3 = (Port)aPort1.config(PKMap.CONFIG_NAME_NEW_MAP_ELEMENT_PORT, new Object[] { "One" });
        aPort1_One2.setData(15);
        aPort1_One1.setData(10);
        aPort1_One2.config(
            net.nawaman.pattern.UPattern.CONFIG_NAME_PATTERN_ACTION,
            new Object[] {
                new net.nawaman.pattern.PAAssignment.Simple(
                    null,
                    Expression.newData(20),
                    false
                )
            }
        );
        aPort1_One3.config(
                net.nawaman.pattern.UPattern.CONFIG_NAME_PATTERN_ACTION,
                new Object[] {
                    new net.nawaman.pattern.PAAssertion.Simple(
                        null,
                        Expression.TRUE
                    )
                }
            );
        
        final Port aPort2_One1 = (Port)aPort1.config(PKMap.CONFIG_NAME_NEW_MAP_ELEMENT_PORT, new Object[] { "Two" });
        final Port aPort2_One2 = (Port)aPort1.config(PKMap.CONFIG_NAME_NEW_MAP_ELEMENT_PORT, new Object[] { "Two" });
        aPort2_One2.setData(45);
        aPort2_One1.setData(30);
        
        final Port aPort3_One1 = (Port)aPort1.config(PKMap.CONFIG_NAME_NEW_MAP_ELEMENT_PORT, new Object[] { "Three" });
        final Port aPort3_One2 = (Port)aPort1.config(PKMap.CONFIG_NAME_NEW_MAP_ELEMENT_PORT, new Object[] { "Three" });
        aPort3_One2.setData(90);
        aPort3_One1.setData(60);
        
        this.assertValue(aPort1.getData(), "{One->20, Three->60}");
        
        this.assertValue(
            "{\n" +
            "   port M:[[String->int]] = null;\n" +
            "   M->>getData();"                   +
            "}",
            "null"
        );
        
        this.assertValue(
            "{\n" +
            "   port M:[[String->int]];\n" +
            "   M->>getData();"            +
            "}",
            "{->}"
        );
        
        this.startCapture();
        this.assertValue(
            "{\n" +
            "   port M:[[String{==}->int]];\n" +
            "   @:println(M[[`One`]]);\n"      +
            "   M->>getData();\n"              +
            "}",
            "{One->null}"
        );
        this.assertCaptured("null\n");
        
        
        this.assertValue(
            "{\n" +
            "   port M:[[String{==}->int]];\n" +
            "   M[[`One`]]  = 1;\n"             +
            "   M[[`One`]] := 2;\n"             +
            "   @:println(M[[`One`]]);\n"      +
            "   M->>getData();\n"              +
            "}",
            "{One->2}"
        );
        
        this.startCapture();
        this.assertValue(
                "{\n" +
                "   port M:[[String{==}->int]];\n"        +
                "   M[[`One`]]  = 1;\n"                   +
                "   M[[`One`]] := 2;\n"                   +
                "   M->>getData();\n"                     +
                "   @:println(`One: ` + M[[`One`]]);\n"   +
                "   @:println(`Two: ` + M[[`Two`]]);\n"   +
                "   @:println(`One: ` + M.get(`One`));\n" +
                "   @:println(`Two: ` + M.get(`Two`));\n" +
                "   M->>getData();\n"+
                "}",
                "{One->2}"
            );
        this.assertCaptured("One: 2\nTwo: null\nOne: 2\nTwo: null\n");
        
        this.startCapture();
        this.assertValue(
                "{\n" +
                "   port M:[[String{==}->int]];\n"       +
                "   M[[`One`]]  = 1;\n"                  +
                "   M[[`One`]] := 2;\n"                  +
                "   @:println(`Keys: ` + M.keySet());\n" +
                "   M;\n"+
                "}",
                "{One->2}"
            );
        this.assertCaptured("Keys: {\"One\"}\n");
        
        this.printSection("END!!!");
    }
}
