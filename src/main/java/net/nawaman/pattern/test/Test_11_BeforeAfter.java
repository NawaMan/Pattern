package net.nawaman.pattern.test;

import net.nawaman.curry.Engine;
import net.nawaman.curry.MUnit;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_11_BeforeAfter extends TestCaseParser {
	
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

		this.startCapture();
		this.assertValue(
			"{\n" +
			"	port int I = 10;\n"                        +
			"	I := 15;\n"                                +
			"	~:after(I): @:println(`After I: ` + I);\n" +
			"	@:println(`Before I`);"                    +
			"	I->>getData();"                            +
			"}",
			"15"
		);
		this.assertCaptured("Before I\nAfter I: 15\n");

		this.startCapture();
		this.assertValue(
			"{\n" +
			"	port int* Is;\n" +
			"	port int  Length = 10;\n" +
			"	port int  Step   = 1;\n" +
			"	~:after(Is): { ~:after(Is): @:println(`After after 'after' of Is.`); }\n"+
			"	~:after(Length, Step): {\n" +
			"		for(int i = 0; i < Length; i += Step) {\n" +
			"			Is <+= i;\n" +
			"			@:println(`Append Is with ` + i);\n" +
			"		}\n" +
			"	}\n" +
			"	Length := 20;\n" +
			"	Step   := 2;\n" +
			"	Length->>render();\n" +
			"	Step  ->>render();\n" +
			"	Is    ->>getData();\n" +
			"}",
			"[0, 2, 4, 6, 8, 10, 12, 14, 16, 18]"
		);
		this.assertCaptured(
			"Append Is with 0\n" +
			"Append Is with 2\n" +
			"Append Is with 4\n" +
			"Append Is with 6\n" +
			"Append Is with 8\n" +
			"Append Is with 10\n" +
			"Append Is with 12\n" +
			"Append Is with 14\n" +
			"Append Is with 16\n" +
			"Append Is with 18\n" +
			"After after 'after' of Is.\n"
		);
		
		this.printSection("Before");
		
		this.startCapture();
		this.assertValue(
			"{\n"                                           +
			"	port Length :int  = 10;\n"                  +
			"	port Ints   :int*;\n"                       +
			"	~:before(Ints):{\n"                         +
			"		for(var i :int = 0; i < Length; i++)\n" +
			"			Ints <+= i*10;\n"                   +
			"	}\n"                                        +
			"	Length := 5;\n"                             +
			"	Ints->>getData()?$;\n"                      +
			"}\n",
			"[0, 10, 20, 30, 40]"
		);
		
		// 10 is default, 30 is assigned as default but later
		this.startCapture();
		this.assertValue(
			"{\n"                   +
			"	port int I = 10;\n" +
			"	I = 30;\n"          +
			"	I->>getData()?$;\n" +
			"}\n",
			"10"
		);
		
		// I has no value, 30 is default and 10 is given before I is rendered so it is given after 30
		this.startCapture();
		this.assertValue(
			"{\n"                       +
			"	port int I;\n"          +
			"	~:before(I): I = 10;\n" +
			"	I = 30;\n"              +
			"	I->>getData()?$;\n"     +
			"}\n",
			"30"
		);
		
		// I has no default, 10 is given just before I is rendered so the value is I
		this.startCapture();
		this.assertValue(
			"{\n"                       +
			"	port int I;\n"          +
			"	~:before(I): I = 10;\n" +
			"	I->>getData()?$;\n"     +
			"}\n",
			"10"
		);
		
		this.printSection("Multiple Before");
		this.startCapture();
		this.assertValue(
			"{\n"                                                      +
			"	port I :int = 10;\n"                                    +
			"	port J :int = 20;\n"                                    +
			"	~:before(I,J): @:println(`This is before I and J`);\n" +
			"	@:println(`Before all.`);\n"                           +
			"	@:println(`I = ` + I->>getData()?$);\n"                +
			"	@:println(`After I.`);\n"                              +
			"	@:println(`J = ` + J->>getData()?$);\n"                +
			"	@:println(`After all.`);\n"                            +
			"}\n",
			"After all."
		);
		this.assertCaptured(
			"Before all.\n"            +
			"This is before I and J\n" +
			"I = 10\n"                 +
			"After I.\n"               +
			"J = 20\n"                 +
			"After all.\n"
		);
        
        this.printSection("Pattern After");
        this.startCapture();
        this.assertValue(
            "{\n"                                                            +
            "   const aPA1 = new test=>PatternA();\n"                        +
            "   const aPA2 = new test=>PatternA();\n"                        +
            "   ~:after(~aPA1, ~aPA2): @:println(`The result is: ` + aPA1.result + ` & ` + aPA2.result);\n" +
            "   aPA1.prefix  = `^`;\n"                                       +
            "   aPA1.suffix  = `$`;\n"                                       +
            "   aPA1.content = `...`;\n"                                     +
            "   aPA1.render();\n"                                            +
            "   aPA2.prefix  = `->`;\n"                                      +
            "   aPA2.suffix  = `<-`;\n"                                      +
            "   aPA2.content = `---`;\n"                                     +
            "   aPA2.render();\n"                                            +
            "   @:println(`After all.`);\n"                                  +
            "}\n",
            "After all."
        );
        this.assertCaptured(
            "The result is: ^...$ & ->---<-\n" +
            "After all.\n"
        );
        
        this.printSection("Pattern After");
        this.startCapture();
        this.assertValue(
            "{\n"                                                              +
            "   port aPA1:test=>PatternA = new test=>PatternA();\n"            +
            "   ~:after(~aPA1): @:println(`The result is: ` + aPA1.result);\n" +
            "   @:println(`Is Rendered: ` + aPA1->>isRendered());\n"           +
            "   aPA1.prefix  = `^`;\n"                                         +
            "   aPA1.suffix  = `$`;\n"                                         +
            "   aPA1.content = `...`;\n"                                       +
            "   aPA1.render();\n"                                              +
            "   @:println(`After all.`);\n"                                    +
            "}\n",
            "After all."
        );
        this.assertCaptured(
            "Is Rendered: false\n"   +
            "The result is: ^...$\n" +
            "After all.\n"
        );
        
        this.printSection("Pattern Before");
        this.startCapture();
        this.assertValue(
            "{\n"                                                    +
            "   port aPA1:test=>PatternA = new test=>PatternA();\n"  +
            "   ~:before(~aPA1): @:println(`Is Rendered: ` + aPA1->>isRendered() + ` & ` + aPA1.isRendered());\n" +
            "   aPA1.prefix  = `^`;\n"                               +
            "   aPA1.suffix  = `$`;\n"                               +
            "   aPA1.content = `...`;\n"                             +
            "   @:println(`Before all.`);\n"                         +
            "   aPA1.render();\n"                                    +
            "   @:println(`After all.`);\n"                          +
            "}\n",
            "After all."
        );
        this.assertCaptured(
            "Before all.\n"               +
            "Is Rendered: true & false\n" + // aPA1 (as a port) is rendered before aPA1 as a patter is rendered.
            "After all.\n"
        );
        
		this.printSection("END!!!");
	}
}
