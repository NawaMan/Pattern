package net.nawaman.pattern.test;

import net.nawaman.curry.Engine;
import net.nawaman.curry.Scope;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.curry.compiler.EE_Language;
import net.nawaman.curry.util.DataHolder;
import net.nawaman.pattern.PKSingle;
import net.nawaman.pattern.PortFactory;
import net.nawaman.pattern.PortInfo;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_05_Port_Writable extends TestCaseParser {
	
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
		
		this.ParaserTypeName = null;

		this.printSection("Simple");
		PortInfo   OPI1 = new PortInfo(TKJava.TInteger.getTypeRef(), PKSingle.Instance, true, null, null);
		DataHolder DH1  = PortFactory.Factory.newDataHolder(null, $Engine, TKJava.TInteger, true, true, null, OPI1);
		
		Scope S1 = new Scope();
		S1.addDataHolder("DH1", DH1);

		this.TopScope = S1;
		this.assertValue(
			"@<?{ Try setting some value }?>:{\n"      +
			"	DH1 = 5;\n" +
			"}",
			true
		);
		this.assertValue(
			"@<?{ Try setting another value }?>:{\n"      +
			"	DH1 = 6;\n" +
			"}",
			true
		);
		this.assertValue("DH1", 5 );
		this.assertValue("DH1", 5 );
		
		this.printSection("Complex");
		PortInfo   OPI2 = new PortInfo(TKJava.TInteger.getTypeRef(), PKSingle.Instance, true, null, null);
		DataHolder DH2  = PortFactory.Factory.newDataHolder(null, $Engine, TKJava.TInteger, true, true, null, OPI2);
		
		Scope S2 = new Scope();
		S2.newVariable("DH2", TKJava.TDataHolder, DH2);

		this.TopScope = S2;
		this.startCapture();
		this.assertValue(
			"({\n"                                                              +
			"	@@:Import(net.nawaman.curry.*);\n"                              +
			"	@:configDH(\n"                                                  +
			"		DH2,\n"                                                     +
			"		net.nawaman.pattern.UPattern.CONFIG_NAME_PATTERN_ACTION,\n" +
			"		new net.nawaman.pattern.PAAssignment.Simple(\n"             +
			"			(Executable)null,\n"                                    +
			"			(Executable)@@:Expr(5 + 10)\n"                          +
			"		)\n"                                                        +
			"	);\n"                                                           +
			"	@:configDH(\n"                                                  +
			"		DH2,\n"                                                     +
			"		net.nawaman.pattern.UPattern.CONFIG_NAME_PATTERN_ACTION,\n" +	// Assert
			"		new net.nawaman.pattern.PAAssertion.Simple(\n"              +
			"			(Executable)null,\n"                                    +
			"			(Executable)@@:New macro (Value:int):boolean {\n"       +
			"				return Value > 5;\n"                                +
			"			}\n"                                                    +
			"		)\n"                                                        +
			"	);\n"                                                           +
			"	@:configDH(\n"                                                  +	// Use
			"		DH2,\n"                                                     +
			"		net.nawaman.pattern.UPattern.CONFIG_NAME_PATTERN_ACTION,\n" +
			"		new net.nawaman.pattern.PAPostRendering.Simple(\n" +
			"			(Executable)null,\n"                                    +
			"			(Executable)@@:New macro (Value:int):void {\n"          +
			"				@:println(`Value = ` + Value);\n"                   +
			"			}\n"                                                    +
			"		)\n"                                                        +
			"	);\n"                                                           +
			"})",
			null
		);

		this.assertValue("@:getDHValue(DH2)", 15);
		this.assertValue("@:getDHValue(DH2)", 15);
		
		this.assertCaptured("Value = 15\n");
		
		this.printSection("Done!!!");
	}
}
