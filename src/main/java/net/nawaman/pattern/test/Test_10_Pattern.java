package net.nawaman.pattern.test;

import java.io.File;

import net.nawaman.curry.Engine;
import net.nawaman.curry.MUnit;
import net.nawaman.pattern.Pattern;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_10_Pattern extends TestCaseParser {
	
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

		if($Units.getPackage("nawaman~>pattern") == null) return;
	
		this.printSection("Regular package element");
		this.startCapture();
		String CurrentDir = (new File(".")).getAbsolutePath();
		this.assertValue(
			"{\n"                                                       +
			"	@:println(nawaman~>pattern:>ShowCurrentDirectory());\n" +
			"}",
			CurrentDir
		);
		this.assertCaptured(CurrentDir + "\n" + CurrentDir + "\n");

		int PNum;
		
		this.printSection("Simple pattern");
		
		PNum = Pattern.getCurrentPatternNumber();
		this.assertValue(
			"{\n"                                  +
			"	@@:Import(nawaman~>pattern=>*);\n" +
			"	@:println(P1.C);\n" +
			"}",
			""
		);
		
		PNum = Pattern.getCurrentPatternNumber();
		this.assertValue(
			"{\n"                                  +
			"	@@:Import(nawaman~>pattern=>*);\n" +
			"	new P1();\n" +
			"}",
			"nawaman~>pattern=>P1#" + PNum
		);
		
		PNum = Pattern.getCurrentPatternNumber();
		this.assertValue(
			"{\n"                                                   +
			"	@@:Import(nawaman~>pattern=>*);\n"                  +
			"	P1 P = new P1();\n"                                 +
			"	\\f`P = %s; P.IP = %s; P.OP = %s;`(P, P.IP, P.OP);" +
			"}",
			"P = nawaman~>pattern=>P1#"+PNum+"; P.IP = 5; P.OP = 10;"
		);
		
		PNum = Pattern.getCurrentPatternNumber();
		this.assertValue(
			"{\n"                                                   +
			"	@@:Import(nawaman~>pattern=>*);\n"                  +
			"	P1 P = new P1();\n"                                 +
			"	P.IP := 10;\n"                                      +
			"	\\f`P = %s; P.IP = %s; P.OP = %s;`(P, P.IP, P.OP);" +
			"}",
			"P = nawaman~>pattern=>P1#"+PNum+"; P.IP = 10; P.OP = 15;"
		);
		/*
		PNum = Pattern.getCurrentPatternNumber();
		this.assertValue(
			"{\n"                                  +
			"	@@:Import(nawaman~>pattern=>*);\n" +
			"	(new P1(55)).ID;\n" +
			"}",
			"55"
		);*/
		
		PNum = Pattern.getCurrentPatternNumber();
		this.assertValue(
			"{\n"                                                         +
			"	@@:Import(nawaman~>pattern=>*);\n"                        +
			"	P2 P = new P2();\n"                                       +
			"	P.IP := 10;\n"                                            +
			"	\\f`P = %s; P.IP  = %2s; P.OP  = %2s;`(P, P.IP,  P.OP) +" +
			"	\\f`P = %s; P.IP2 = %2s; P.OP2 = %2s;`(P, P.IP2, P.OP2);" +
			"}",
			"P = nawaman~>pattern=>P2#"+PNum+"; P.IP  = 10; P.OP  = 15;"  +
			"P = nawaman~>pattern=>P2#"+PNum+"; P.IP2 =  5; P.OP2 = 15;"
		);

		this.printSection("Pattern method");
		this.startCapture();
		this.assertValue(
			"{\n"                                  +
			"	@@:Import(nawaman~>pattern=>*);\n" +
			"	P1 P = new P1();\n"                +
			"	P.IP := 10;\n"                     +
			"	@:println(P.IP->>isRendered());\n" +
			"	@:println(P.getIP());\n"           +
			"	@:println(P.isRendered());\n"      +
			"	@:println(P.OP->>isRendered());\n" +
			"	@:println(P.getOP());\n"           +
			"	@:println(P.isRendered());\n"      +
			"	``;" +
			"}",
			""
		);
		this.assertCaptured(
			"false\n" +
			"10\n"    +
			"false\n" +
			"false\n" +
			"15\n"    +
			"true\n"
		);
		
		this.printSection("DONE!!!");	
	}
}