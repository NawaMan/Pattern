package net.nawaman.pattern.test;

import net.nawaman.curry.Engine;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.curry.compiler.EE_Language;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_09_DefaultDependent extends TestCaseParser {
	
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
		Engine        $Engine   = AllTests.getEngine();
		CurryLanguage $Language = AllTests.getLanguage();
		
		AllTests.TheEngine   = $Engine;
		AllTests.TheLanguage = $Language;
		((EE_Language)$Engine.getExtension(EE_Language.Name)).setDefaultLanguage($Language);

		this.printSection("List port");
		this.startCapture();
		// `Length` defaultly depends on `Ls` meaning that as soon as `Ls` is rendered, Length is 
		this.assertValue(
				"{\n"                                                                +
				"	port int* Ls;\n"                                                 +
				"	port int  Length ::= @:show(`Ls.length = `, Ls.size(),`\\n`);"   +
				"	Ls <+= @:show(`X   (`,10,`)\\n`);\n"                             +
				"	Ls <+= 20;\n"                                                    +
				"	Ls <+= 15;\n"                                                    +
				"	Ls <+= @:show(`XXV (`,25,`)\\n`);\n"                             +
				"	@:println(`Before render`);\n"                                   +
				"	Ls;\n"                                                           +
				"}",
				"[10, 20, 15, 25]"
			);
		this.assertCaptured("Before render\nX   (`10`:int)\nXXV (`25`:int)\nLs.length = `4`:int\n");
		
		this.startCapture();
		this.assertValue(
				"{\n"                                                                           +
				"	port int* Ls;\n"                                                            +
				"	port int* Is;\n"                                                            +
				"	port int  Length ::= @:show(`Length:    `, Is.size() + Ls.size(),`\\n`);"   +
				"	\n"                                                                         +
				"	Ls <+= 10;\n"                                                               +
				"	Ls <+= 20;\n"                                                               +
				"	Ls <+= 15;\n"                                                               +
				"	Ls <+= 25;\n"                                                               +
				"	\n"                                                                         +
				"	Is <+= 1;\n"                                                                +
				"	Is <+= 2;\n"                                                                +
				"	\n"                                                                         +
				"	@:println(`Is.length: ` + ((java.util.List)Is->>getData()).size());\n"      +
				"	@:println(`Ls.length: ` + ((java.util.List)Ls->>getData()).size());\n"      +
				"	@:println(`` + Is->>getData() + Ls->>getData());\n"                         +
				"}",
				"[1, 2][10, 20, 15, 25]"
			);
		this.assertCaptured(
			"Is.length: 2\n"           +
			"Length:    `6`:int\n"     +
			"Ls.length: 4\n"           +
			"[1, 2][10, 20, 15, 25]\n"
		);
		this.disableOutputCapture();
		
		this.startCapture();
		this.assertValue(
				"{\n"                                                                           +
				"	port int* Ls;\n"                                                            +
				"	port int* Is;\n"                                                            +
				"	port int  Length ::= ({{\n"                                                 +
				"		int L = Is.size() + Ls.size();\n"                                       +
				"		@:println(`Length:    ` + L);"                                          +
				"		L;\n"                                                                   +
				"	}});\n"                                                                     +
				"	\n"                                                                         +
				"	Ls <+= 10;\n"                                                               +
				"	Ls <+= 20;\n"                                                               +
				"	Ls <+= 15;\n"                                                               +
				"	Ls <+= 25;\n"                                                               +
				"	\n"                                                                         +
				"	Is <+= 1;\n"                                                                +
				"	Is <+= 2;\n"                                                                +
				"	\n"                                                                         +
				"	@:println(`Is.length: ` + ((java.util.List)Is->>getData()).size());\n"      +
				"	@:println(`Ls.length: ` + ((java.util.List)Ls->>getData()).size());\n"      +
				"	@:println(`` + Is->>getData() + Ls->>getData());\n"                         +
				"}",
				"[1, 2][10, 20, 15, 25]"
			);
		this.assertCaptured(
			"Is.length: 2\n"           +
			"Length:    6\n"           +
			"Ls.length: 4\n"           +
			"[1, 2][10, 20, 15, 25]\n"
		);
		this.disableOutputCapture();
		
		this.startCapture();
		this.assertValue(
				"{\n"                                                                      +
				"	port int* Is;\n"                                                       +
				"	\n"                                                                    +
				"	Is <+= 1;\n"                                                           +
				"	Is <+= 2;\n"                                                           +
				"	\n"                                                                    +
				"	@:println(`Is.length: ` + ((java.util.List)Is->>getData()).size());\n" +
				"	port int Length ::= ({{\n"                                          +
				"		int L = Is.size();\n"                                              +
				"		@:println(`Length:    ` + L);"                                     +
				"		L;\n"                                                              +
				"	}});\n"                                                                +
				"	@:println(`` + Is->>getData());\n"                                     +
				"}",
				"[1, 2]"
			);
		this.assertCaptured(
			"Is.length: 2\n" +
			"Length:    2\n" +
			"[1, 2]\n"
		);
		this.disableOutputCapture();
		
		this.printSection("DONE!!!");
	}
}