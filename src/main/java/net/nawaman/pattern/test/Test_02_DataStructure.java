package net.nawaman.pattern.test;

import net.nawaman.curry.Engine;
import net.nawaman.curry.MUnit;
import net.nawaman.curry.TLPackage;
import net.nawaman.curry.Type;
import net.nawaman.curry.Instructions_Core.Inst_Type;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.curry.compiler.EE_Language;
import net.nawaman.pattern.test.AllTests.TestCaseParser;

public class Test_02_DataStructure extends TestCaseParser {
	
	static public void main(String ... Args) { runTest(Args); }
	
	String ParaserTypeName = "Command";
	/**{@inheritDoc}*/ @Override
	protected String getParserTypeName() {
		return this.ParaserTypeName;
	}

	/**{@inheritDoc}*/ @Override
	protected void doTest(final String ... Args) {
		Engine        $Engine   = AllTests.getEngine();
		CurryLanguage $Language = AllTests.getLanguage();
		MUnit         $Units    = $Engine.getUnitManager();
		
		AllTests.TheEngine   = $Engine;
		AllTests.TheLanguage = $Language;
		((EE_Language)$Engine.getExtension(EE_Language.Name)).setDefaultLanguage($Language);
		/* */
		
		$Units.discoverUsepaths();

		if($Units.getPackage("pattern~>data") == null) return;
		
		Type T;
		 
		this.printSection("Get Type");
		this.assertValue("pattern~>data=>SimpleArray.type",   (Type)$Engine.execute(Inst_Type.Name, new TLPackage.TRPackage("pattern~>data", "SimpleArray")));
		this.assertValue("pattern~>data=>ArrayIterator.type", (Type)$Engine.execute(Inst_Type.Name, new TLPackage.TRPackage("pattern~>data", "ArrayIterator")));
				
		T = (Type)$Engine.execute(Inst_Type.Name, new TLPackage.TRPackage("pattern~>data", "SimpleArray"));
		this.println(java.util.Arrays.toString(T.getTypeInfo().getObjectOperationInfos()));
		
		T = (Type)$Engine.execute(Inst_Type.Name, new TLPackage.TRPackage("pattern~>data", "ArrayIterator"));
		this.println(java.util.Arrays.toString(T.getTypeInfo().getObjectOperationInfos()));
		
		this.printSection("SimpleArray && ArrayIterator");

		this.printSubSection("Types");
		this.assertValue("pattern~>data=>SimpleArray.type",   (Type)$Engine.execute(Inst_Type.Name, new TLPackage.TRPackage("pattern~>data", "SimpleArray")));
		this.assertValue("@:isKindOf(java.lang.Iterable.type, pattern~>data=>SimpleArray.type)", true);
		this.assertValue("@:isKindOf(curry=>Iterable.type,    pattern~>data=>SimpleArray.type)", true);

		this.assertValue("pattern~>data=>ArrayIterator.type",    (Type)$Engine.execute(Inst_Type.Name, new TLPackage.TRPackage("pattern~>data", "ArrayIterator")));
		this.assertValue("pattern~>data=>ArrayIterator.typeref", new TLPackage.TRPackage("pattern~>data", "ArrayIterator"));		
		this.assertValue("@:isKindOf(java.util.Iterator.type,    pattern~>data=>ArrayIterator.type)", true);		
		this.assertValue("@:isKindOf(curry=>Iterator.type,       pattern~>data=>ArrayIterator.type)", true);

		this.printSubSection("In the loop");
		this.assertValue(
				"{\n"                                                                     +
				"	String S = ``;\n"                                                     +
				"	foreach(int I : new int[] { 0,6,1,5,2,4,3 }) {\n"                     +
				"		if($Count$ != 0) S += `,`;\n"                                     +
				"		S += I;\n"                                                        +
				"	}\n"                                                                  +
				"	S;\n"                                                                 +
				"}",
				"0,6,1,5,2,4,3");
		
		this.assertValue(
				"{\n"                                                                               +
				"	@@:Import(pattern~>data=>*);\n"                                                 +
				"	String S = ``;\n"                                                               +
				"	ArrayIterator<int> AI = new ArrayIterator<int>(new int[] { 0,6,1,5,2,4,3 });\n" +
				"	while(AI.hasNext()) {\n"                                                        +
				"		int I = AI.next();\n"                                                       +
				"		if($Count$ != 0) S += `,`;\n"                                               +
				"		S += I;\n"                                                                  +
				"	}\n"                                                                            +
				"	S;\n"                                                                           +
				"}",
				"0,6,1,5,2,4,3");

		this.assertValue(
				"{\n"                                                             +
				"	@@:Import(pattern~>data=>*);\n"                               +
				"	String S = ``;\n"                                             +
				"	ArrayIterator<int> AI = new (new int[] { 0,6,1,5,2,4,3 });\n" +
				"	foreach(int I : AI) {\n"                                      +
				"		if($Count$ != 0) S += `,`;\n"                             +
				"		S += I;\n"                                                +
				"	}\n"                                                          +
				"	S;\n"                                                         +
				"}",
				"0,6,1,5,2,4,3");
		
		this.assertValue(
				"{\n//1\n" +
				"	@@:Import(pattern~>data=>*);\n"                                               +
				"	String S = ``;\n"                                                             +
				"	SimpleArray<int> SAIs = new SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 });\n" +
				"	fromto(int i = 0 : SAIs.length()) {\n"                                        +
				"		if($Count$ != 0) S += `,`;\n"                                             +
				"		S += SAIs.get(i);\n"                                                      +
				"	}\n"                                                                          +
				"	fromto(int i = 0 : SAIs.length()) {\n"                                        +
				"		if($Count$ != 0) S += `,`;\n"                                             +
				"		SAIs.set(i,i);\n"                                                         +
				"		S += SAIs.get(i);\n"                                                      +
				"	}\n"                                                                          +
				"	S;\n"                                                                         +
				"}"
				,
				"0,6,1,5,2,4,30,1,2,3,4,5,6"
			);

		this.assertValue(
				"{" +
				"	@@:Import(pattern~>data=>*);\n"                                  +

				"	SimpleArray<int> SAIs = new (new int[] { null, 1 });\n" +
				"	SAIs.get(0) + ` & ` + SAIs.get(1) + ` - ` + SAIs.get(0)?? + ` & ` + SAIs.get(1)??;" +
				"}"
				,
				"null & 1 - 0 & 1"
			);

		this.printSubSection("Comapare");
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) === new int[] { 0,6,1,5,2,4,3 }; }", false);
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) ==  new any[] { 0,6,1,5,2,4,3 }; }", true);
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) =#= new int[] { 0,6,1,5,2,4,3 }; }", true);
		
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) === new pattern~>data=>SimpleArray(new int[] { 0,6,1,5,2,4,3 }); }", false);
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) ==  new pattern~>data=>SimpleArray(new any[] { 0,6,1,5,2,4,3 }); }", true);
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) =#= new pattern~>data=>SimpleArray(new int[] { 0,6,1,5,2,4,3 }); }", true);
		
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) === (new pattern~>data=>SimpleArray(new int[] { 0,6,1,5,2,4,3 })).iterator(); }", false);
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) ==  (new pattern~>data=>SimpleArray(new any[] { 0,6,1,5,2,4,3 })).iterator(); }", true);
		this.assertValue("{ new pattern~>data=>SimpleArray<int>(new int[] { 0,6,1,5,2,4,3 }) =#= (new pattern~>data=>SimpleArray(new int[] { 0,6,1,5,2,4,3 })).iterator(); }", true);
		
		
		this.printSection("List");

		this.printSubSection("Types");
		this.assertValue("pattern~>data=>SimpleList.type",      (Type)$Engine.execute(Inst_Type.Name, new TLPackage.TRPackage("pattern~>data", "SimpleList")));
		this.assertValue("@:isKindOf(java.util.List.type,       pattern~>data=>SimpleList.type)", true);
		this.assertValue("@:isKindOf(java.lang.Iterable.type,   pattern~>data=>SimpleList.type)", true);
		this.assertValue("@:isKindOf(java.util.Collection.type, pattern~>data=>SimpleList.type)", true);
		this.assertValue("@:isKindOf(curry=>List.type,          pattern~>data=>SimpleList.type)", true);
		
		this.printSubSection("In the loop");
		this.assertValue(
				"{\n"                                                    +
				"	@@:Import(pattern~>data=>*);\n"                      +
				"	SimpleList<int> SLIs = new();\n"                     +
				"	int I = 1;"                                          +
				"	fromto(int i = 0 :     5) SLIs.add(I *= (i + 1));\n" +
				"	fromto(int i = 4 : -1: 0) SLIs.add(I /= (i + 1));\n" +
				"	\n"                                                  +
				"	SLIs.toString();\n"                                  +
				"}"
				,
				"[1, 2, 6, 24, 120, 24, 6, 2, 1]"
			);
		

		this.assertValue(
				"{\n"                                                   +
				"	@@:Import(pattern~>data=>*);\n"                     +
				"	SimpleSet<int> SSs = new();\n"                      +
				"	int I = 1;"                                         +
				"	fromto(int i = 0 :     5) SSs.add(I *= (i + 1));\n" +
				"	fromto(int i = 4 : -1: 0) SSs.add(I /= (i + 1));\n" +
				"	SSs.toString();\n"                                  +
				"}"
				,
				"{1, 2, 6, 24, 120}"
			);
		
		this.startCapture();
		this.assertValue(
				"{\n"                                                                                         +
				"	@@:Import(pattern~>data=>*);\n"                                                           +
				"	SimpleMap<int,int> SMs = new();\n"                                                        +
				"	int I = 1;"                                                                               +
				"	fromto(int i = 0 :     5) SMs.put(@:show(``,i,` -> `), @:show(``,I *= (i + 1),`\\n`));\n" +
				"	fromto(int i = 4 : -1: 0) SMs.put(@:show(``,i,` -> `), @:show(``,I /= (i + 1),`\\n`));\n" +
				"	\n"                                                                                       +
				"	SMs.toString();\n"                                                                        +
				"}"
				,
				"{0->1, 1->1, 2->2, 3->6, 4->24}"
			);
		this.assertCaptured(
			"`0`:int -> `1`:int\n"   +
			"`1`:int -> `2`:int\n"   +
			"`2`:int -> `6`:int\n"   +
			"`3`:int -> `24`:int\n"  +
			"`4`:int -> `120`:int\n" +
			"`4`:int -> `24`:int\n"  +
			"`3`:int -> `6`:int\n"   +
			"`2`:int -> `2`:int\n"   +
			"`1`:int -> `1`:int\n"
		);
	}
}
