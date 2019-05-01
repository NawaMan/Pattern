package net.nawaman.pattern.test;

import net.nawaman.curry.Engine;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.curry.compiler.EE_Language;
import net.nawaman.pattern.UPattern;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_07_PatternVariable extends TestCaseParser {
	
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

		this.printSection("Simple");
		this.startCapture();
		this.assertValue(
			"{\n"                                                                               +
			"	port int I := @:show(`Let set to `, 5, `.\\n`);\n"                              +
			"	I = @:show(`But also set to: `, 10, `.\\n`);\n"                                 +
			"	\n"                                                                             +
			"	@:println(`Is I rendered? ` + I->>"+ UPattern.CONFIG_NAME_IS_RENDERED +"());\n" +
			"	@:println(`I = ` + I + `.`);\n"                                                 +
			"	@:println(`Is I rendered? ` + I->>"+ UPattern.CONFIG_NAME_IS_RENDERED +"());\n" +
			"	``;\n"                                                                          +
			"}",
			""
		);
		this.assertCaptured(
			"Is I rendered? false\n" +
			"Let set to `5`:int.\n"  +
			"I = 5.\n"               +
			"Is I rendered? true\n"
		);

		this.printSection("Depending");
		this.startCapture();
		this.assertValue(
			"{\n"                                                   +
			"	port int ID := @:show(`I set ID to `,5,`\\n`);\n" +
			"	@:println(`Before JD.`);"                           +
			"	port int JD := ID + 5;\n"                         +
			"	@:println(`After JD.`);\n"                          +
			"	JD;\n"                                              +
			"}",
			"10"
		);
		this.assertCaptured(                                      
			"Before JD.\n" +
			"After JD.\n"  +
			"I set ID to `5`:int\n"
		);
		
		/* This will cause problem * /
		this.printSection("Recursive");
		this.assertValue(
			"{\n"                                                 +
			"	port int I := @:show(`I set I to `,5,`\\n`);\n" +
			"	port int J := I + 5;\n"                         +
			"	I := J + 10;\n"                                   +
			"	J;\n"                                             +
			"}",
			"10"
		);
		/* */
		

		this.enableOutputCapture();
		this.printSection("Assertion");
		this.assertValue(
			"{\n"                                                         +
			"	port int I := @:show(`Set I to `,5    ,`\\n`);\n"            +
			"	port int J := @:show(`Set J to `,I + 5,`\\n`);\n"            +
			"	~:assert(I):   @:show(`Is I <  10? `, I <  10, `\\n`);\n" +
			"	~:assert(J):   @:show(`Is J >= 10? `, J >= 10, `\\n`);\n" +
			"	~:assert(I,J): @:show(`Is I <   J? `, I <   J, `\\n`);\n" +
			"	@:printf(`J = %d, I = %d\\n`, J, I);\n"                   +
			"}",
			"J = 10, I = 5\n"
		);
		this.assertCaptured(
			"Set I to `5`:int\n"           +
			"Is I <  10? `true`:boolean\n" +
			"Set J to `10`:int\n"          +
			"Is J >= 10? `true`:boolean\n" +
			"Is I <   J? `true`:boolean\n" +
			"J = 10, I = 5\n"
		);
		
		this.printSection("DONE!!!");
	}

}
