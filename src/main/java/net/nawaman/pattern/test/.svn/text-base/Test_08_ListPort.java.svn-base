package net.nawaman.pattern.test;

import net.nawaman.curry.Engine;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.curry.compiler.EE_Language;
import net.nawaman.pattern.PatternActionError;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_08_ListPort extends TestCaseParser {
	
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
		
		$Engine.getUnitManager().discoverUsepaths();

		this.printSection("List port");
		this.startCapture();
		this.assertValue(
				"{\n"                                     +
				"	port int* Ls;\n"                      +
				"	Ls <+= @:show(`X   (`,10,`)\\n`);\n"  +
				"	Ls <+= 20;\n"                         +
				"	Ls <+= 15;\n"                         +
				"	Ls <+= @:show(`XXV (`,25,`)\\n`);\n"  +
				"	@:println(`Before render`);\n"        +
				"	Ls;\n"                                +
				"}",
				"[10, 20, 15, 25]"
			);
		this.assertCaptured("Before render\nX   (`10`:int)\nXXV (`25`:int)\n");
		this.disableOutputCapture();

		this.printSection("Sort -  Hash");
		this.assertValue(
				"{\n"                                   +
				"	port int* Is;\n"                    +
				"	Is <+= 10;\n"                       +
				"	Is <+= 20;\n"                       +
				"	Is <+= 15;\n"                       +
				"	Is <+= 25;\n"                       +
				"	~:sort(Is):{:(I):int; return I; }\n" +
				"	Is;\n"                              +
				"}",
				"[10, 15, 20, 25]"
			);
		this.assertValue(
				"{\n"                                    +
				"	port int* Is;\n"                     +
				"	Is <+= 10;\n"                        +
				"	Is <+= 20;\n"                        +
				"	Is <+= 15;\n"                        +
				"	Is <+= 25;\n"                        +
				"	~:sort(Is):{:(I):int; return -I; }\n" +
				"	Is;\n"                               +
				"}",
				"[25, 20, 15, 10]"
			);

		this.printSection("Sort -  Comparator");
		this.assertValue(
				"{\n"                                       +
				"	port int* Is;\n"                        +
				"	Is <+= 10;\n"                           +
				"	Is <+= 20;\n"                           +
				"	Is <+= 15;\n"                           +
				"	Is <+= 25;\n"                           +
				"	~:sort(Is):{:(I,J):int; return I-J; }\n" +
				"	Is;\n"                                  +
				"}",
				"[10, 15, 20, 25]"
			);
		this.assertValue(
				"{\n"                                       +
				"	port int* Is;\n"                        +
				"	Is <+= 10;\n"                           +
				"	Is <+= 20;\n"                           +
				"	Is <+= 15;\n"                           +
				"	Is <+= 25;\n"                           +
				"	~:sort(Is):{:(I,J):int; return J-I; }\n" +
				"	Is;\n"                                  +
				"}",
				"[25, 20, 15, 10]"
			);

		this.printSection("Immutable");
		this.assertValue(
				"{\n"                                   +
				"	port int* Is;\n"                    +
				"	Is <+= 10;\n"                       +
				"	Is <+= 20;\n"                       +
				"	Is <+= 15;\n"                       +
				"	Is <+= 25;\n"                       +
				"	~:sort(Is):{:(I):int; return I; }\n" +
				"	curry=>List<int> CurryList = Is;\n" +
				"	CurryList->isImmutable();\n"        +
				"}",
				"true"
			);
		this.assertProblem(
				"{\n"                                    +
				"	port int* Is;\n"                     +
				"	Is <+= 10;\n"                        +
				"	Is <+= 20;\n"                        +
				"	Is <+= 15;\n"                        +
				"	Is <+= 25;\n"                        +
				"	~:sort(Is):{:(I):int; return I; }\n"  +
				"	port int* Is2 = Is;\n"             +
				"	Is2 <+= 30;\n"                       +
				"	curry=>List<int> CurryList = Is2;\n" +
				"	CurryList;\n"                        +
				"}",
				PatternActionError.class,
				"The final value is immutable.*"
			);

		this.printSection("Filter");
		this.assertValue(
				"{\n"                                              +
				"	port int* Is;\n"                               +
				"	Is <+= 10;\n"                                  +
				"	Is <+= 20;\n"                                  +
				"	Is <+= 15;\n"                                  +
				"	Is <+= 25;\n"                                  +
				"	~:filter(Is):{:(I):boolean; return I < 30; }\n" +
				"	Is->>render();\n"                              +
				"	Is->>getData();\n"                             +
				"}",
				"[10, 20, 15, 25]"
			);
		this.assertProblem(
				"{\n"                                              +
				"	port int* Is;\n"                               +
				"	Is <+= 10;\n"                                  +
				"	Is <+= 20;\n"                                  +
				"	Is <+= 15;\n"                                  +
				"	Is <+= 25;\n"                                  +
				"	~:filter(Is):{:(I):boolean; return I < 20; }\n" +
				"	Is->>render();\n"                              +
				"	Is->>getData();\n"                             +
				"}",
				PatternActionError.class,
				"Port filtering fail:.*"
			);
		
		this.printSection("DONE!!!");
	}
}
