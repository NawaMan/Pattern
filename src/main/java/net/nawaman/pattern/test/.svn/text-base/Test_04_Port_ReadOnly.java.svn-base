package net.nawaman.pattern.test;

import net.nawaman.curry.Context;
import net.nawaman.curry.Documentation;
import net.nawaman.curry.Engine;
import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.Expression;
import net.nawaman.curry.ExternalContext;
import net.nawaman.curry.JavaExecutable;
import net.nawaman.curry.Location;
import net.nawaman.curry.Scope;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.Executable.Macro;
import net.nawaman.curry.Instructions_Executable.Inst_Call;
import net.nawaman.curry.compiler.CompileProductContainer;
import net.nawaman.curry.compiler.CurryCompilationOptions;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.curry.compiler.EE_Language;
import net.nawaman.pattern.PKSingle;
import net.nawaman.pattern.PortFactory;
import net.nawaman.pattern.PortInfo;
import net.nawaman.pattern.test.AllTests.TestCaseParser;
import net.nawaman.curry.util.DataHolder;
import net.nawaman.util.UString;

public class Test_04_Port_ReadOnly extends TestCaseParser {
	
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
		
		ExecSignature ES = ExecSignature.newSignature(
				"GV1",
				TypeRef.EmptyTypeRefArray,
				UString.EmptyStringArray,
				false,
				TKJava.TInteger.getTypeRef(),
				new Location("TestCode", 15, 20),
				Documentation.Util.NewMoreData("Test Documentation")
			);
		Macro GetValue = new JavaExecutable.JavaMacro_Complex(ES) {
		    
            private static final long serialVersionUID = -363833855588476187L;
            
            /** Executing this */ @Override
			protected Object run(Context pContext, Object[] pParams) {
				Expression Expr = ExternalContext.getCurrentExpression(pContext);
				System.out.println("GV1: " + ExternalContext.getCurrentDocumentation(pContext));
				System.out.println((Expr == null) ? "" : "GV1: " + Expr.toDetail(ExternalContext.getEngine(pContext)));
				System.out.println("GV1: " + ExternalContext.getLocationsToString(pContext));
				return 5 + 10;
			}
		};
		Expression CallGetValue = $Engine.getExecutableManager().newExpr(Inst_Call.Name, GetValue);

		final PortInfo   OPI1 = new PortInfo(TKJava.TInteger.getTypeRef(), PKSingle.Instance, true, CallGetValue, false,  null, null);
		final DataHolder DH1  = PortFactory.Factory.newDataHolder(null, $Engine, TKJava.TInteger,   CallGetValue,  true, false, null, OPI1);
		
		CompileProductContainer CPC = new CompileProductContainer();
		CurryCompilationOptions COp = new CurryCompilationOptions();
		
		Scope FScope = new Scope();
		FScope.newConstant("DH1", TKJava.TDataHolder, DH1);
		COp.setFrozens(new String[] { "DH1" });
		COp.setTopScope(FScope);
		
		ES = ExecSignature.newSignature(
				"GV2",
				TypeRef.EmptyTypeRefArray,
				UString.EmptyStringArray,
				false,
				TKJava.TInteger.getTypeRef(),
				new Location("TestCode", 30, 40),
				Documentation.Util.NewMoreData("Test Documentation2")
			);
		GetValue = $Language.compileMacro(ES,
				"System.out.println(`GV2: ` + @:getContextInfo(`CurrentDocumentation`));\n"     +
				"System.out.println(`GV2: ` + @:getContextInfo(`CurrentLocationString`));\n"    +
				"@<?{ Inner document }?>:{\n"                                                   +
				"	System.out.println(`GV2: ` + @:getContextInfo(`CurrentDocumentation`));\n"  +
				"	System.out.println(`GV2: ` + @:getContextInfo(`CurrentLocationString`));\n" +
				"};\n"                                                                          +
				"System.out.println();\n"                                                       +
				"System.out.println(`GV2: ` + @:getDHValue(DH1));\n"                            +
				"return 5+6;",
				COp,
				CPC);
		CallGetValue = $Engine.getExecutableManager().newExpr(Inst_Call.Name, GetValue);
		
		final PortInfo   OPI2 = new PortInfo(TKJava.TInteger.getTypeRef(), PKSingle.Instance, true, CallGetValue, false,  null, null);
		final DataHolder DH2  = PortFactory.Factory.newDataHolder(null, $Engine, TKJava.TInteger,   CallGetValue,  true, false, null, OPI2);

		this.startCapture();
		this.assertValue(DH2.getData(), 11);
		this.assertCaptured(
			"GV2: <?[---\n"                                                   +
			"Test Documentation2\n"                                           +
			"---]?>\n"                                                        +
			"GV2: TestCode at CR( 29,  1) => GV2():int\n"                     +
			"GV2: <?[---\n"                                                   +
			"Inner document\n"                                                +
			"---]?>\n"                                                        +
			"GV2: TestCode at CR( 30,  4) => GV2():int\n"                     +
			"\n"                                                              +
			"GV1: <?[---\n"                                                   +
			"Test Documentation\n"                                           +
			"---]?>\n"                                                        +
			"\n"                                                              +
			"GV1: \n"                                                         +
			"	TestCode       at CR( 15, 20) => GV1():int\n"                 +
			"	TestCode       at CR( 29,  7) => GV2():int\n"                 +
			"	<<-- ROOT -->> at CR(xxx,xxx) => <<-- ROOT -->>.root():any\n" +
			"GV2: 15\n"
		);

		this.startCapture();
		this.assertValue(DH1.getData(), 15);
		this.assertCaptured("");

		this.startCapture();
		this.assertValue(DH2.getData(), 11);
		this.assertCaptured("");
		
		this.printSection("DONE!!!");
	}
}
