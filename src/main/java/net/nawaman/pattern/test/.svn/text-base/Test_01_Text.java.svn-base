package net.nawaman.pattern.test;

import net.nawaman.pattern.test.AllTests.TestCase;
import net.nawaman.text.AppendableText;
import net.nawaman.text.FixedLengthText;
import net.nawaman.text.StructureText;
import net.nawaman.text.Text;
import net.nawaman.text.VirtualText;

public class Test_01_Text extends TestCase {
	
	static public void main(String ... Args) { runTest(Args); }

	/**{@inheritDoc}*/ @Override
	protected void doTest(final String ... Args) {
		
		this.printSection("Fix-Length Text");
		Text T1 = new FixedLengthText(           "Text1");
		Text T2 = new FixedLengthText("Text#02", "Text2");
		this.assertValue(T1.toString(), "Text1");
		this.assertValue(T2.toString(), "Text2");
		this.assertValue(T1.toDetail(), "<null>: \"|Text1|\"");
		this.assertValue(T2.toDetail(), "Text#02: \"|Text2|\"");

		this.printSection("Appendable Text");
		this.printSubSection("Before");
		Text T3 = new AppendableText(           "Text3");
		Text T4 = new AppendableText("Text#04", "Text4");
		this.assertValue(T3.toString(), "Text3");
		this.assertValue(T4.toString(), "Text4");
		this.assertValue(T3.toDetail(), "<null>: \"|Text3|\"");
		this.assertValue(T4.toDetail(), "Text#04: \"|Text4|\"");

		this.printSubSection("After");
		T3.asAppendable().append("-T3");
		T4.asAppendable().append("-T4");
		this.assertValue(T3.toString(), "Text3-T3");
		this.assertValue(T4.toString(), "Text4-T4");
		this.assertValue(T3.toDetail(), "<null>: \"|Text3-T3|\"");
		this.assertValue(T4.toDetail(), "Text#04: \"|Text4-T4|\"");

		this.printSection("Virtual Text");
		Text T5 = new VirtualText(          T3, 6, 8);
		Text T6 = new VirtualText("Text#06", T4, 6, 8);
		this.assertValue(T5.toString(), "T3");
		this.assertValue(T6.toString(), "T4");
		this.assertValue(T5.toDetail(), "<null>: \"|T3|\"");
		this.assertValue(T6.toDetail(), "Text#06: \"|T4|\"");

		this.printSection("Structure Text");
		this.printSubSection("One levels");
		Text T7 = new StructureText(           T3, new FixedLengthText("::"), T5);
		Text T8 = new StructureText("Text#08", T4, new FixedLengthText("::"), T6);
		this.assertValue(T7.toString(), "Text3-T3::T3");
		this.assertValue(T8.toString(), "Text4-T4::T4");
		this.assertValue(T7.toDetail(),
			"<null>:\n"                   + 
			"	<null>: \"|Text3-T3|\"\n" +
			"	<null>: \"|::|\"\n"       +
			"	<null>: \"|T3|\"\n"
		);
		this.assertValue(T8.toDetail(),
			"Text#08:\n"                   + 
			"	Text#04: \"|Text4-T4|\"\n" +
			"	<null> : \"|::|\"\n"       +
			"	Text#06: \"|T4|\"\n"
		);

		this.printSubSection("Two level");
		Text T09 = new StructureText(           T3, new FixedLengthText("::"), T7);
		Text T10 = new StructureText("Text#10", T4, new FixedLengthText("::"), T8);
		this.assertValue(T09.toString(), "Text3-T3::Text3-T3::T3");
		this.assertValue(T10.toString(), "Text4-T4::Text4-T4::T4");
		this.assertValue(T09.toDetail(),
			"<null>:\n"                       + 
			"	<null>: \"|Text3-T3|\"\n"     +
			"	<null>: \"|::|\"\n"           +
			"	<null>:\n"                    + 
			"		<null>: \"|Text3-T3|\"\n" +
			"		<null>: \"|::|\"\n"       +
			"		<null>: \"|T3|\"\n"       +
			"\n"
		);
		this.assertValue(T10.toDetail(),
			"Text#10:\n"                       + 
			"	Text#04: \"|Text4-T4|\"\n"     +
			"	<null> : \"|::|\"\n"           +
			"	Text#08:\n"                    + 
			"		Text#04: \"|Text4-T4|\"\n" +
			"		<null> : \"|::|\"\n"       +
			"		Text#06: \"|T4|\"\n"       +
			"\n"
		);

		return;
	}
}
