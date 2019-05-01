package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.Engine;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.TypeRef;

/** Single valued port kind */
public final class PKSingle extends PortKind {
	
	static private final long serialVersionUID = 8416546516516531231L;
	
	/** The name of the kind */
	static public final String Name = "Single";
	
	static public final PKSingle Instance = new PKSingle();
	
	/** Default construction */
	PKSingle() {}
	
	/**{@inheritDoc}*/ @Override
	public String getName() {
		return Name;
	}
	
	/**{@inheritDoc}*/ @Override
	public TypeRef getBaseTypeRef(Engine $Engine) {
		return TKJava.TAny.getTypeRef();
	}
	
	/**{@inheritDoc}*/ @Override
	public TypeRef getTypeRefFor(Engine $Engine, TypeRef ... ParameterTypeRefs) {
		TypeRef TRef = TKJava.TAny.getTypeRef();
		
		if((ParameterTypeRefs != null) && (ParameterTypeRefs.length == 1))
			TRef = ParameterTypeRefs[0];
		
		return TRef;
	}
	
	/**{@inheritDoc}*/ @Override
	public PortInternalData newInternalData(
            final Engine  $Engine,
            final Port    pPort,
            final TypeRef pDataTypeRef) {
		// The default one
		return new PortInternalData();
	}
	
	/**{@inheritDoc}*/ @Override
	public FinalValue newFinalValue(
	        final Engine           $Engine,
	        final Object           pFinalValue,
	        final PAAssignment     pFinalAction,
	        final Port             pPort) {
		// The default one
		return new FinalValue(pFinalValue, pFinalAction);
	}
    
    /**{@inheritDoc}*/ @Override
    Object tryNonNullDefaultFinalValue(
            Context          $Context,
            FinalValue       pFinalValue,
            TypeRef          pDataTypeRef,
            PortInternalData pInternalData) {
        return pFinalValue.getValue();
    }
}