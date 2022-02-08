package net.nawaman.pattern;

import net.nawaman.curry.AttributeInfo;
import net.nawaman.curry.Context;
import net.nawaman.curry.Type;
import net.nawaman.curry.extra.type_object.TClass;

/** Type of Patterns */
public class TPattern extends TClass {
	
	// Construction --------------------------------------------------------------------------------

	/** Constructs a Pattern Type */
	protected TPattern(TKPattern pTKind, TSPattern pTSpec) {
		super(pTKind, pTSpec);
	}

	/** Returns the attribute info of the data of this type by its name. **/
	AttributeInfo doData_getAttributeInfo(Pattern thePattern, Context pContext, Type pAsType, String pName) {
		return this.doData_getAttributeLocal_RAW(thePattern, pContext, pAsType, pName);
	}
}
