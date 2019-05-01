package net.nawaman.pattern;

import java.util.Vector;

import net.nawaman.curry.ConstructorInfo;
import net.nawaman.curry.ParameterizedTypeInfo;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.Executable.Macro;
import net.nawaman.curry.extra.type_object.TSClass;
import net.nawaman.curry.util.MoreData;

/** TypeSpec of a pattern type */
public class TSPattern extends TSClass {
    
    private static final long serialVersionUID = -5533261664680097397L;
    
	// Constants ----------------------------------------------------------------------------------
    
    @SuppressWarnings("hiding")
	final static public int IndexCount = 16;
	
	final static public int Index_RenderFunction = 15;	// Macro
	
	// Constructor and verification ---------------------------------------------------------------
	
	/** Constructs a TSPattern */
	protected TSPattern(TypeRef pTRef, String pKind, boolean pIsAbstract, boolean pIsFinal, TypeRef pSuperRef,
			TypeRef[] pInterfaces, ParameterizedTypeInfo pTPInfo, MoreData pMoreData, MoreData pExtraInfo) {
		super(pTRef, pKind, pIsAbstract, pIsFinal, pSuperRef, pInterfaces, pTPInfo, pMoreData, pExtraInfo);
	}

	// StackOwner ---------------------------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	protected int getDataIndexCount() {
		return TSPattern.IndexCount;
	}
	
	// Re-define access to elements so that TBPattern can access
	
	/**{@inheritDoc}*/ @Override
	protected Vector<ConstructorInfo> getConstructorInfo() {
		return super.getConstructorInfo();
	}
	
	/** Change the constuction info array */
	void setConstructorInfo(Vector<ConstructorInfo> CIs) {
		this.Datas[Index_Constructors] = CIs;
	}
	
	/** Returns the Macro for the Render Function of this Pattern tyep */
	protected Macro getRenderFunctionMacro() {
		return (Macro)this.getData(Index_RenderFunction);
	}
}
