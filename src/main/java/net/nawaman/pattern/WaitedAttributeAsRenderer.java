package net.nawaman.pattern;

import net.nawaman.curry.AttributeInfo;
import net.nawaman.curry.Context;
import net.nawaman.curry.Type;
import net.nawaman.curry.AttributeInfo.AIDirect;

public class WaitedAttributeAsRenderer implements Renderer {

	public WaitedAttributeAsRenderer(Context aContext, Pattern aPattern, Type anAsType, String aAttrName) {
		// Save the attribute
		AttributeInfo AI = ((TPattern)aPattern.getTheType()).doData_getAttributeInfo(aPattern, aContext, anAsType, aAttrName);
		
		this.TheAttr = aPattern.getPort(((AIDirect)AI).getDHIndex());
	}
	
	Port TheAttr;
	
	/**{@inheritDoc}*/ @Override
	public boolean isNotRendered() {
		return this.TheAttr.isNotRendered();
	}
	
	/**{@inheritDoc}*/ @Override
	public boolean isRendered() {
		return TheAttr.isRendered();
	}
	
	/**{@inheritDoc}*/ @Override
	public boolean isBeingRendered() {
		return this.TheAttr.isBeingRendered();
	}

}
