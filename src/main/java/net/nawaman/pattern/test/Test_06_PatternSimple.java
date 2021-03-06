package net.nawaman.pattern.test;

import net.nawaman.compiler.CodeFeeder;
import net.nawaman.curry.Accessibility;
import net.nawaman.curry.Engine;
import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.Location;
import net.nawaman.curry.MExecutable;
import net.nawaman.curry.PackageBuilder;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.TLPackage;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.TypeSpec;
import net.nawaman.curry.UnitBuilder;
import net.nawaman.curry.UnitBuilders;
import net.nawaman.curry.Instructions_Context.Inst_GetContextInfo;
import net.nawaman.curry.Instructions_Context.Inst_GetVarValue;
import net.nawaman.curry.Instructions_Core.Inst_NewInstance;
import net.nawaman.curry.Instructions_Operations.InstFormat;
import net.nawaman.curry.Instructions_Operations.InstPrintLn;
import net.nawaman.curry.Instructions_StackOwner.Inst_thisConfigAttr;
import net.nawaman.curry.Instructions_StackOwner.Inst_thisGetAttrValue;
import net.nawaman.curry.Instructions_StackOwner.Inst_thisSetAttrValue;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.curry.compiler.EE_Language;
import net.nawaman.curry.test.lang.Curry.AllTests.TestCaseUnit;
import net.nawaman.pattern.PKSingle;
import net.nawaman.pattern.Pattern;
import net.nawaman.pattern.PAAssignment;
import net.nawaman.pattern.TBPattern;
import net.nawaman.pattern.TKPattern;
import net.nawaman.pattern.UPattern;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

/** Test basic functionality of Pattern */
public class Test_06_PatternSimple extends TestCaseParser {
	
	static public void main(String ... Args) {
		runTest(Args);
	}

	static private String  UName  = "TUP06";
	static private String  PName  = "P06";
	static private String  TName1 = "TP1";
	static private String  TName2 = "TP2";

	/**{@inheritDoc}*/ @Override
	protected void doTest(final String ... Args) {
		Engine        $Engine   = AllTests.getEngine();
		CurryLanguage $Language = AllTests.getLanguage();
		MExecutable   $ME       = $Engine.getExecutableManager();
		
		AllTests.TheEngine   = $Engine;
		AllTests.TheLanguage = $Language;
		((EE_Language)$Engine.getExtension(EE_Language.Name)).setDefaultLanguage($Language);

		// Prepare the types
		this.prepareTypes(/* ReCreate? (not load from the package file) */ true);

		TypeRef TR1 = new TLPackage.TRPackage(PName, TName1);
		TypeRef TR2 = new TLPackage.TRPackage(PName, TName2);

		this.printSection("Type");
		this.assertValue(TR1,                               "P06=>TP1");
		this.assertValue($Engine.execute($ME.newType(TR1)), "P06=>TP1");
		
		this.printSection("Instance");

		this.printSubSection("Default constructor");
		Pattern P1 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR1));
		
		/* Result in error * /
		this.println("P.isRendered(): " + P1.configAttr( "ID", UPattern.CONFIG_NAME_IS_RENDERED));
		this.println("P.ID          : " + P1.getAttrData("ID"));
		this.println("P.isRendered(): " + P1.configAttr( "ID", UPattern.CONFIG_NAME_IS_RENDERED));
		/* */
		
		/* Result in no error */
		this.assertValue("`P.isRendered(): "   + P1.configAttr( "ID", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.isRendered(): false");
		this.assertValue("`P.ID = \"Me\"   : " + P1.setAttrData("ID", "Me")                             + "`", "P.ID = \"Me\"   : true");
		this.assertValue("`P.ID          : "   + P1.getAttrData("ID")                                   + "`", "P.ID          : Me");
		this.assertValue("`P.isRendered(): "   + P1.configAttr( "ID", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.isRendered(): true");
		/* */

		this.printSubSection("ID constructor");
		Pattern P2 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR1), "P1");
		
		this.assertValue("`P.isRendered(): "   + P2.configAttr( "ID", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.isRendered(): false");
		this.assertValue("`P.ID = \"Me\"   : " + P2.setAttrData("ID", "Me")                             + "`", "P.ID = \"Me\"   : true");
		this.assertValue("`P.ID          : "   + P2.getAttrData("ID")                                   + "`", "P.ID          : P1");
		this.assertValue("`P.isRendered(): "   + P2.configAttr( "ID", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.isRendered(): true");

		this.printSubSection("ID constructor - Redundant NoConflict");
		Pattern P3 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR1), "P3");
		this.assertValue("`P.isRendered(): "   + P3.configAttr("ID", UPattern.CONFIG_NAME_IS_RENDERED ) + "`", "P.isRendered(): false");
		this.assertValue(
				"`P.ID = \"P#\"   : " +
				P3.configAttr(
					"ID",
					UPattern.CONFIG_NAME_PATTERN_ACTION,
					new PAAssignment.Simple(null, $ME.newExpr(InstFormat.Name, "P%d", 3))
				) +
				"`",
				"P.ID = \"P#\"   : null"
			);
		this.assertValue("`P.ID          : "   + P3.getAttrData("ID")                                   + "`", "P.ID          : P3");
		this.assertValue("`P.isRendered(): "   + P3.configAttr( "ID", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.isRendered(): true");

		/* Result in error * /
		this.printSubSection("ID constructor - Redundant with Conflict");
		Pattern P4 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR1), "P4");
		this.assertValue("`P.isRendered(): "   + P4.configAttr("ID", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.isRendered(): false");
		this.assertValue(
				"`P.ID = \"P#\"   : " +
				P4.configAttr(
					"ID",
					UPattern.CONFIG_NAME_PATTERN_ACTION,
					new PAAssignment.Simple(null, $ME.newExpr(Inst_Format.Name, "P%d", 10))
				) +
				"`",
				"P.ID = \"P#\"   : null"
			);
		this.assertValue("`P.ID          : "   + P4.getAttrData("ID")                                            + "`", "P.ID          : P4");
		this.assertValue("`P.isRendered(): "   + P4.configAttr("ID", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.isRendered(): true");
		/* */
		
		this.printSection("Port");

		this.printSubSection("Port - ReadOnly");
		Pattern P5 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR2), "Nawa");
		
		this.assertValue("`P.Name.isRendered(): "   + P5.configAttr( "Name", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Name.isRendered(): false");
		this.assertValue("`P.Name             : "   + P5.getAttrData("Name")                                   + "`", "P.Name             : ID(Nawa)");
		this.assertValue("`P.Name.isRendered(): "   + P5.configAttr( "Name", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Name.isRendered(): true");

		/* Result in error * /
		Pattern P6 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR2), "Nawa");
		this.assertValue("`P.Name.isRendered(): " + P6.getAttrMoreInfo("Name", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Name.isRendered(): false");
		this.assertValue(
			"`P.Name = \"P#\"      : " +
				P6.configAttr(
					"Name",
					UPattern.CONFIG_NAME_PATTERN_ACTION,
					new PAAssignment.Simple(null, $ME.newExpr(Inst_Format.Name, "P%d", 10))
				) +
				"`",
				"P.Name = \"P#\"      : null"
			);
		this.assertValue("`P.Name             : " + P6.getAttrData("Name")                                            + "`", "P.Name             : P10");
		this.assertValue("`P.Name.isRendered(): " + P6.getAttrMoreInfo("Name", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Name.isRendered(): true");
		/* */

		
		this.printSection("OutPort");

		this.printSubSection("Port");
		Pattern P6 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR2), "Nawa");
		
		this.assertValue("`P.Name    .isRendered(): "   + P6.configAttr( "Name",     UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Name    .isRendered(): false");
		this.assertValue("`P.Greeting.isRendered(): "   + P6.configAttr( "Greeting", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Greeting.isRendered(): false");
		this.assertValue("`P.Greeting             : "   + P6.getAttrData("Greeting")                                   + "`", "P.Greeting             : Hello from ID(Nawa).");
		this.assertValue("`P.Name    .isRendered(): "   + P6.configAttr( "Name",     UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Name    .isRendered(): true");
		this.assertValue("`P.Greeting.isRendered(): "   + P6.configAttr( "Greeting", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Greeting.isRendered(): true");
		this.assertValue("`P.Name                 : "   + P6.getAttrData("Name")                                       + "`", "P.Name                 : ID(Nawa)");

		
		this.printSection("Pattern render");
		// This prove that 1) InPorts are rendred before the render function and OutPort is rendered just after
		// the pattern is rendered
		Pattern P7 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR2), "Nawa");

		this.startCapture();
		this.assertValue("`P         .isRendered(): " + P7.isRendered()                                             + "`", "P         .isRendered(): false");
		this.assertValue("`P.Name    .isRendered(): " + P7.configAttr("Name",     UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Name    .isRendered(): false");
		this.assertValue("`P.Greeting.isRendered(): " + P7.configAttr("Greeting", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Greeting.isRendered(): false");
		this.assertValue("`P         .render()    : " + P7.invoke(    "render")                                     + "`", "P         .render()    : null" );
		this.assertValue("`P         .isRendered(): " + P7.isRendered()                                             + "`", "P         .isRendered(): true" );
		this.assertValue("`P.Name    .isRendered(): " + P7.configAttr("Name",     UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Name    .isRendered(): true" );
		this.assertValue("`P.Greeting.isRendered(): " + P7.configAttr("Greeting", UPattern.CONFIG_NAME_IS_RENDERED) + "`", "P.Greeting.isRendered(): true" );
		this.assertCaptured(
			"Hello from the render function.\n"                                                              +
			"ID(Nawa)\n"                                                                                     +
			"net/nawaman/pattern/test/Test_06_PatternSimple.java at CR(  0,281) => P06=>TP2.render():void\n"
		);
		

		this.printSection("Pattern function");
		// This test try to ensure if an invocation of a pattern's function will not result in the rendering of the
		//     pattern 
		Pattern P8 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR2), "Nawa");
		this.assertValue("`P.isRendered()    : " + P8.isRendered()             + "`", "P.isRendered()    : false");
		this.assertValue("`P.isNameRendered(): " + P8.invoke("isNameRendered") + "`", "P.isNameRendered(): false");
		this.assertValue("`P.isRendered()    : " + P8.isRendered()             + "`", "P.isRendered()    : false");
		this.assertValue("`P.render()        : " + P8.invoke("render")         + "`", "P.render()        : null" );
		this.assertValue("`P.isNameRendered(): " + P8.invoke("isNameRendered") + "`", "P.isNameRendered(): true");
		this.assertValue("`P.isRendered()    : " + P8.isRendered()             + "`", "P.isRendered()    : true");
		

		this.printSection("Pattern method");
		// This test try to ensure if an invocation of a pattern's method will automatically result in the rendering of
		//     the pattern 
		Pattern P9 = (Pattern)$Engine.execute(Inst_NewInstance.Name, $ME.newType(TR2), "Nawa");
		this.assertValue("`P.isRendered(): " + P9.isRendered()     + "`", "P.isRendered(): false");
		this.assertValue("`P.whoAmI()    : " + P9.invoke("whoAmI") + "`", "P.whoAmI()    : My name is ID(Nawa)." );
		this.assertValue("`P.isRendered(): " + P9.isRendered()     + "`", "P.isRendered(): true");
		
		this.printSection("END!!!");
	}
	
	/** Prepare Types for the testing */
	private void prepareTypes(boolean IsReCreated) {
		Engine      E  = AllTests.getEngine();
		MExecutable ME = E.getExecutableManager();

		if(!IsReCreated) {
			E.getUnitManager().registerUnitFactory("File://" + TestCaseUnit.UnitFilePrefix + UName);
			return;
		}
	
		UnitBuilder    UB = new UnitBuilders.UBFile(E, TestCaseUnit.UnitFilePrefix, UName, null, (CodeFeeder)null);
		PackageBuilder PB = UB.newPackageBuilder(PName);

		// Get the TypeKind
		TKPattern TKP = ((TKPattern)E.getTypeManager().getTypeKind(TKPattern.KindName));
		
		// Prepare often use TypeRefs
		TypeRef TR1 = new TLPackage.TRPackage(PB.getName(), TName1);
		TypeRef TR2 = new TLPackage.TRPackage(PB.getName(), TName2);
		
		// TV1 ---------------------------------------------------------------------------------------------------------

		TypeSpec TS1 = TKP.getTypeSpec(
				TR1,	// TypeRef
				false,	// IsAbstract
				false,	// IsFinal
				null,	// SuperRef
				null,	// InterfaceRefs
				null,	// ParamterizedInfo
				null,	// MoreData
				null	// ExtraInfo
			);

		TBPattern TBP1 = (TBPattern)PB.newTypeBuilder(Accessibility.Public, TS1, null);
		
		TBP1.addConstructor(Accessibility.Public,
			ME.newMacro(
				ExecSignature.newProcedureSignature("new", TKJava.TVoid.getTypeRef()),
				null
			),
			null
		);
		TBP1.addConstructor(Accessibility.Public,
			ME.newMacro(
				ExecSignature.newSignature(
					"new",
					new TypeRef[] { TKJava.TString.getTypeRef() },
					new String[]  { "$ID" },
					TKJava.TVoid.getTypeRef()
				),
				ME.newExpr(Inst_thisSetAttrValue.Name, "ID", ME.newExpr(Inst_GetVarValue.Name, "$ID"))
			),
			null
		);
		
		/** Add ID */
		TBP1.addInPort(
			Accessibility.Public,			// Read Accessibility
			Accessibility.Public,			// Write Accessibility
			"ID",							// Port Name
			TKJava.TString.getTypeRef(),	// TypeRef
			PKSingle.Instance,				// PortKind
			true,							// IsWritable
			null, 							// Location
			null, 							// Document
			null, 							// MoreInfo
			null 							// MoreData
		);
		
		// TV2 ---------------------------------------------------------------------------------------------------------

		TypeSpec TS2 = TKP.getTypeSpec(
				TR2,	// TypeRef
				false,	// IsAbstract
				false,	// IsFinal
				null,	// SuperRef
				null,	// InterfaceRefs
				null,	// ParamterizedInfo
				null,	// MoreData
				null	// ExtraInfo
			);

		TBPattern TBP2 = (TBPattern)PB.newTypeBuilder(Accessibility.Public, TS2, null);
		
		TBP2.addConstructor(Accessibility.Public,
			ME.newMacro(
				ExecSignature.newProcedureSignature("new", TKJava.TVoid.getTypeRef()),
				null
			),
			null
		);
		TBP2.addConstructor(Accessibility.Public,
			ME.newMacro(
				ExecSignature.newSignature(
					"new",
					new TypeRef[] { TKJava.TString.getTypeRef() },
					new String[]  { "$ID" },
					TKJava.TVoid.getTypeRef()
				),
				ME.newExpr(Inst_thisSetAttrValue.Name, "ID", ME.newExpr(Inst_GetVarValue.Name, "$ID"))
			),
			null
		);
		
		/* Add ID */
		TBP2.addInPort(
			Accessibility.Public,			// Read Accessibility
			Accessibility.Public,			// Write Accessibility
			"ID",							// Port Name
			TKJava.TString.getTypeRef(),	// TypeRef
			PKSingle.Instance,				// PortKind
			true,							// IsWritable
			null, 							// Location
			null, 							// Document
			null, 							// MoreInfo
			null 							// MoreData
		);
		
		/* Add an InPort Name */
		TBP2.addInPort(
			Accessibility.Public,			// Read Accessibility
			Accessibility.Public,			// Write Accessibility
			"Name",							// Port Name
			TKJava.TString.getTypeRef(),	// TypeRef
			PKSingle.Instance,				// PortKind
			false,							// IsWritable
			true, 
			ME.newExpr(						// Default Value
				InstFormat.Name,
				"ID(%s)",
				ME.newExpr(
					Inst_thisGetAttrValue.Name,
					"ID"
				)
			),
			null, 							// Location
			null, 							// Document
			null, 							// MoreInfo
			null 							// MoreData
		);
		
		/* Add an OutPort Greeting */
		TBP2.addOutPort(
			Accessibility.Public,			// Read Accessibility
			Accessibility.Public,			// Write Accessibility
			"Greeting",						// Port Name
			TKJava.TString.getTypeRef(),	// TypeRef
			PKSingle.Instance,				// PortKind
			false,							// IsWritable
			true, 
			ME.newExpr(InstFormat.Name, "Hello from %s.", ME.newExpr(Inst_thisGetAttrValue.Name, "Name")),	// Default Value
			null, 							// Location
			null, 							// Document
			null, 							// MoreInfo
			null 							// MoreData
		);
		
		/* Add a render function */
		TBP2.addFunction(
			Accessibility.Public,
			ME.newMacro(
				ExecSignature.newProcedureSignature(
					"render",
					TKJava.TVoid.getTypeRef(),
					new Location("net/nawaman/pattern/test/Test_06_PatternSimple.java", 0, 281),
					null
				),
				ME.newGroup(
					ME.newExpr(
						InstPrintLn.Name,
						"Hello from the render function."
					),
					ME.newExpr(
						InstPrintLn.Name,
						ME.newExpr(
							InstFormat.Name,
							"ID(%s)",
							ME.newExpr(
								Inst_thisGetAttrValue.Name,
								"ID"
							)
						)
					),
					ME.newExpr(
						InstPrintLn.Name,
						ME.newExpr(
							Inst_GetContextInfo.Name,
							"CurrentLocationString"
						)
					)
				)
			),
			null
		);
		
		/* Add another function */
		TBP2.addFunction(
			Accessibility.Public,
			ME.newMacro(
				ExecSignature.newProcedureSignature(
					"isNameRendered",
					TKJava.TBoolean.getTypeRef(),
					new Location("net/nawaman/pattern/test/Test_06_PatternSimple.java", 0, 351),
					null
				),
				ME.newExpr(
					Inst_thisConfigAttr.Name,
					"Name",
					UPattern.CONFIG_NAME_IS_RENDERED
				)
			),
			null
		);
		
		/* Add a method */
		TBP2.addMethod(
			Accessibility.Public,
			ME.newMacro(
				ExecSignature.newProcedureSignature(
					"whoAmI",
					TKJava.TString.getTypeRef(),
					new Location("net/nawaman/pattern/test/Test_06_PatternSimple.java", 0, 351),
					null
				),
				ME.newExpr(
					InstFormat.Name,
					"My name is %s.",
					ME.newExpr(
						Inst_thisGetAttrValue.Name,
						"Name"
					)
				)
			),
			null
		);
		
		// SAVE ================================================================================================

		UB.save();
		
	}
}
