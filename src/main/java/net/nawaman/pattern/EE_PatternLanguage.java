package net.nawaman.pattern;

import net.nawaman.curry.compiler.EE_Language;

public class EE_PatternLanguage extends EE_Language {
	
	/** Constructs an engine extension. */
	public EE_PatternLanguage() {
		this.isStackOwnerVariableShouldBeTreatedAsVariable = true;
	}
	
}
