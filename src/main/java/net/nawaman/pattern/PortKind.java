package net.nawaman.pattern;

import java.io.Serializable;

import net.nawaman.curry.Context;
import net.nawaman.curry.Engine;
import net.nawaman.curry.TypeRef;

/** Kind of Port */
abstract public class PortKind implements Serializable {
    
    private static final long serialVersionUID = 8699672964744464137L;
	
	// Services --------------------------------------------------------------------------------------------------------
    
    /** Returns the name of the kind */
	abstract public String getName();
	
	/** Returns the TypeRef of the base type of data for this kind */
	abstract public TypeRef getBaseTypeRef(Engine $Engine);
	
	/** Returns the TypeRef of the actual data type with the data typeref */
	abstract public TypeRef getTypeRefFor(Engine $Engine, TypeRef ... ParameterTypeRefs);
	
	/** Returns the Internal-Data Object used for a port of this kind with the data TypeRef */
	abstract public PortInternalData newInternalData(
	        Engine  $Engine,
	        Port    pPort,
	        TypeRef pDataTypeRef);
	
	/** Returns the Final-Value Object used for a port of this kind with the data TypeRef */
	abstract public FinalValue newFinalValue(
	        Engine           $Engine,
			Object           pFinalValue,
			PAAssignment     pFinalAction,
			Port             pPort);
	
	abstract Object tryNonNullDefaultFinalValue(
            Context          $Context,
            FinalValue       pFinalValue,
            TypeRef          pDataTypeRef,
            PortInternalData pInternalData);
	
    /** Notify this final value object that the final value is now assigned */
    protected void notifyValueAssigned(
            final Context $Context,
            final Port    pPort,
            final boolean pIsHardAssigned) {}
    
    /** Notify this final value object that the final value is now fully configured */
    protected void notifyValueConfigured(
            final Context $Context,
            final Port    pPort,
            final boolean pIsHardAssigned) {}
    
    /** Notify this final value object that the value is now accepted as the final */
    protected void notifyValueAccepted(
            final Context $Context,
            final Port    pPort,
            final boolean pIsHardAssigned) {}

    /** Notify this final value object that the value is now verified as the final */
    protected void notifyValueVerified(
            final Context $Context,
            final Port    pPort,
            final boolean pIsHardAssigned) {}
    
	
	protected Object getMoreInfo(Port pPort, String pInfoName) {
	    return null;
	}
    protected Object config(Context $Context, Port pPort, String pInfoName, Object ... pParameters) {
        return null;
    }
	
	/**{@inheritDoc}*/ @Override
	public String toString() {
		return "PortKind:" + this.getName();
	}
}
