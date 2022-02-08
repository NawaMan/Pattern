package net.nawaman.pattern.test;

import net.nawaman.curry.Engine;
import net.nawaman.curry.compiler.CurryLanguage;
import net.nawaman.pattern.EE_Pattern;

public class AllTests extends net.nawaman.curry.test.lang.Keang.AllTests {
		
	// Run the test
	static public void main(final String ... Args) {
		AllTests.getEngine();	// prepare the engine
		AllTests.getLanguage();	// prepare the language
		runTests(Args);
	}
	
	/**{@inheritDoc}*/ @Override 
	public boolean preTest(net.nawaman.testsuite.TestCase TC, final String ... Args) {
		AllTests.getEngine();	// prepare the engine
		AllTests.getLanguage();	// prepare the language
		return true;
	}
	
	// Engine ----------------------------------------------------------------------------------------------------------

	/** The current engine */
	static public Engine getEngine() {
		if(AllTests.TheEngine == null) {
			PrepareEngineWithPattern((String[])null);
			ShowEngineCreating = false;
		}
		return AllTests.TheEngine;
	}
	
	static void PrepareEngineWithPattern(final String ... Args) {
		if((AllTests.TheEngine != null) && (AllTests.TheEngine.getExtension(EE_Pattern.Name) != null)) 
			return;
		
		AllTests.TheEngine = getLanguage().getTargetEngine();
	}
	
	// Langauge --------------------------------------------------------------------------------------------------------

	/** The current engine */
	static public CurryLanguage getLanguage() {
		if(AllTests.TheLanguage == null) PrepareLanguage(false);
		return AllTests.TheLanguage;
	}
	
	static public void PrepareLanguage(boolean IsQuite) {
		if(AllTests.TheLanguage != null) return;
		AllTests.TheLanguage = CurryLanguage.Util.GetGetCurryLanguage("Pattern").getCurryLanguage(null, null);
		System.out.println();
	}
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple Curry Test Case */
	static abstract public class TestCaseParser extends net.nawaman.curry.test.lang.Curry.AllTests.TestCaseParser {}
	
	/** Simple Curry Test Case */
	static abstract public class TestCase extends net.nawaman.curry.test.lang.Curry.AllTests.TestCase {}
	
}
