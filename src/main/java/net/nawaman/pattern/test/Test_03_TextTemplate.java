package net.nawaman.pattern.test;

import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_03_TextTemplate extends TestCaseParser {
	
	static public void main(String ... Args) {
		runTest(Args);
	}
	
	String ParaserTypeName = "Command";
	/**{@inheritDoc}*/ @Override protected String getParserTypeName() { return this.ParaserTypeName; }

	/**{@inheritDoc}*/ @Override
	protected void doTest(final String ... Args) {		
		this.printSection("New Text");
		this.printSubSection("Single");
		this.assertValue("@:createText(`5`)",            "5");
		this.assertValue("@:createText(`5`).toDetail()", "Package:default given at '<<Unknown code>> at CR(  0,  0) => Expression():any': \"|5|\"");

		this.printSubSection("Multiple");
		this.assertValue("@:createText(`5`,`-`,`6`)",                           "5-6");
		this.assertValue("({ int I = 7; @:createText(`5-`,I);   }).toDetail()", "Package:default given at '<<Unknown code>> at CR( 14,  0) => Expression():any': \"|5-7|\"");
		this.assertValue("({ int I = 7; @:createText(`5-`,I+1); }).toDetail()", "Package:default given at '<<Unknown code>> at CR( 14,  0) => Expression():any': \"|5-8|\"");

		this.printSubSection("Expression");
		this.assertValue(
				"({ int I = 7; @:createText(`5-`,@@:Expr( I+1 )); }).toDetail()",
				"Package:default given at '<<Unknown code>> at CR( 14,  0) => Expression():any':\n"             +
				"	<null>                                                                        : \"|5-|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 42,  0) => Expression():any': \"|8|\"\n"
			);
		
		this.printSubSection("Sub");
		this.assertValue(
				"({\n" +
				"	int I = 7;\n" +
				"	@:createText(\n" +
				"		`5-`,\n" +
				"		@@:Expr( I+1 ),\n" +
				"		`-`,\n" +
				"		@:createText(\n" +
				"			@@:Expr(`:`+`D`)\n" +
				"		)\n" +
				"	);\n" +
				"}).toDetail()",
				"Package:default given at '<<Unknown code>> at CR(  1,  2) => Expression():any':\n"             +
				"	<null>                                                                        : \"|5-|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 12,  4) => Expression():any': \"|8|\"\n"  +
				"	<null>                                                                        : \"|-|\"\n"  +
				"	Package:default given at '<<Unknown code>> at CR(  2,  6) => Expression():any':\n"          +
				"		Package:default given at '<<Unknown code>> at CR( 14,  7) => Expression():any': \"|:D|\"\n\n"
			);
		
		this.printSubSection("Echo");
		this.assertValue(
				"({\n" +
				"	int I = 7;\n" +
				"	@:createText(\n" +
				"		`5-`,\n" +
				"		@@:Expr( I+1 ),\n" +
				"		`-`,\n" +
				"		@@:Expr({\n" +
				"			@:echoText(`:`+`D`);\n" +
				"			null;\n" +
				"		})\n" +
				"	);\n" +
				"}).toDetail()",
				"Package:default given at '<<Unknown code>> at CR(  1,  2) => Expression():any':\n"             +
				"	<null>                                                                        : \"|5-|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 12,  4) => Expression():any': \"|8|\"\n"  +
				"	<null>                                                                        : \"|-|\"\n"  +
				"	<null>                                                                        : \"|:D|\"\n"
			);
		this.assertValue(
				"({\n" +
				"	int I = 7;\n" +
				"	@:createText(\n" +
				"		`5-`,\n" +
				"		@@:Expr( I+1 ),\n" +
				"		`-`,\n" +
				"		@@:Expr({\n" +
				"			fromto(int i = 0 : 5) {\n" +
				"				@:echoText($=(`:`+`D`));\n" +
				"			}\n" +
				"			null;\n" +
				"		})\n" +
				"	);\n" +
				"}).toDetail()",
				"Package:default given at '<<Unknown code>> at CR(  1,  2) => Expression():any':\n"             +
				"	<null>                                                                        : \"|5-|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 12,  4) => Expression():any': \"|8|\"\n"  +
				"	<null>                                                                        : \"|-|\"\n"  +
				"	Package:default given at '<<Unknown code>> at CR( 15,  8) => Expression():any': \"|:D|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 15,  8) => Expression():any': \"|:D|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 15,  8) => Expression():any': \"|:D|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 15,  8) => Expression():any': \"|:D|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 15,  8) => Expression():any': \"|:D|\"\n"
			);

		this.printSubSection("Doc");
		this.assertValue(
				"({\n" +
				"	int I = 7;\n" +
				"	@:createText(\n" +
				"		`5-`,\n" +
				"		@@:Expr( @<?{Documentation}?>: (I+1); ),\n" +
				"		`-`,\n" +
				"		@@:Expr({\n" +
				"			fromto(int i = 0 : 5) {\n" +
				"				echo $=(\\f`:%dD`(i));\n" +
				"			}\n" +
				"			null;\n" +
				"		})\n" +
				"	);\n" +
				"}).toDetail()",
				"Package:default given at '<<Unknown code>> at CR(  1,  2) => Expression():any':\n"             +
				"	<null>                                                                        : \"|5-|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR( 11,  4) => Expression():any': \"|8|\"\n"  +
				"	<null>                                                                        : \"|-|\"\n"  +
				"	Package:default given at '<<Unknown code>> at CR(  9,  8) => Expression():any': \"|:0D|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR(  9,  8) => Expression():any': \"|:1D|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR(  9,  8) => Expression():any': \"|:2D|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR(  9,  8) => Expression():any': \"|:3D|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR(  9,  8) => Expression():any': \"|:4D|\"\n"
			);
		

		this.printSubSection("newText");
		this.assertValue("$`5`", "5");
		this.assertValue("$`5`.toDetail()", "Package:default given at '<<Unknown code>> at CR(  0,  0) => Expression():any': \"|5|\"");
		this.assertValue("$=(`5`+`7`)", "57");
		this.assertValue("$=(`5`+`7`).toDetail()", "Package:default given at '<<Unknown code>> at CR(  0,  0) => Expression():any': \"|57|\"");

		this.printSubSection("createText");
		this.assertValue("<${5<(6)>5}$>", "565");
		this.assertValue("<${5<(6)>5}$>.toDetail()",
				"Package:default given at '<<Unknown code>> at CR(  0,  0) => Expression():any':\n"            +
				"	<null>                                                                        : \"|5|\"\n" +
				"	Package:default given at '<<Unknown code>> at CR(  0,  0) => Expression():any': \"|6|\"\n" +
				"	<null>                                                                        : \"|5|\"\n"
		);
		this.assertValue("<${5<{ echo $\\#`#7#`#; }>5}$>.toDetail()",
				"Package:default given at '<<Unknown code>> at CR(  0,  0) => Expression():any':\n"                +
				"	<null>                                                                        : \"|5|\"\n"     +
				"	Package:default given at '<<Unknown code>> at CR( 12,  0) => Expression():any':\n"             +
				"		Package:default given at '<<Unknown code>> at CR( 12,  0) => Expression():any': \"|7|\"\n" +
				"\n"                                                                                               +
				"	<null>                                                                        : \"|5|\"\n"
		);
		
		this.printSection("DONE!!!");
	}
}
