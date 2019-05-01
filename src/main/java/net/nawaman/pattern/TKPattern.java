package net.nawaman.pattern;

import net.nawaman.curry.Accessibility;
import net.nawaman.curry.Context;
import net.nawaman.curry.CurryError;
import net.nawaman.curry.Documentation;
import net.nawaman.curry.Engine;
import net.nawaman.curry.Location;
import net.nawaman.curry.MType;
import net.nawaman.curry.ParameterizedTypeInfo;
import net.nawaman.curry.StackOwnerBuilderEncloseObject;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.Type;
import net.nawaman.curry.TypeBuilder;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.TypeSpec;
import net.nawaman.curry.compiler.TypeSpecCreator;
import net.nawaman.curry.extra.type_object.TKClass;
import net.nawaman.curry.util.MoreData;

public class TKPattern extends TKClass {

	@SuppressWarnings("hiding")
	static public final String KindName = "Pattern";
	
	/** Constructs a new TypeKind. */
	protected TKPattern(Engine pEngine) {
		super(pEngine);
	}

	// Classification -----------------------------------------------------------------------------

	/**{@inheritDoc} */ @Override
	public String getKindName() {
		return KindName;
	}

	// Validate the type 
	/**{@inheritDoc} */ @Override
	protected Exception doValidateType(Context pContext, Type pType) {
		TypeRef SuperRef = pType.getSuperRef();
		Type    Super    = ((SuperRef == null) || TKJava.TAny.getTypeRef().equals(SuperRef))
		                       ? null
		                       : this.getTypeFromRef(pContext, SuperRef);
		if(Super != null) {
			if(!(Super instanceof TPattern)) {
				return new CurryError(
					String.format(
						"Super type of a pattern type must be a pattern: '%s' <TKPattern:51>.",
						pType.getTypeRef()
					)
				);
			}
		}
		
		return null;
	}
	
	// Type Info -------------------------------------------------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	protected TypeBuilder createNewTypeBuilder(TypeSpec pTS, Accessibility pPAccess, Location pLocation,
			StackOwnerBuilderEncloseObject pEncloseObject) {
		if(!(pTS instanceof TSPattern)) throw new IllegalArgumentException("Invalid TypeSpec.");
		
		// Create a new type builder from the information
		return new TBPattern((TSPattern)pTS, pPAccess, pLocation, pEncloseObject);
	}
	
	/**{@inheritDoc}*/ @Override
	public TSPattern getTypeSpec(TypeRef pTRef, boolean pIsAbstract, boolean pIsFinal, TypeRef pSuperRef,
			TypeRef[] pInterfaces, ParameterizedTypeInfo pTPInfo, MoreData pMoreData, MoreData pExtraInfo) {
		return this.getTypeSpec(pTRef, pIsAbstract, pIsFinal, pSuperRef, pInterfaces, true, pTPInfo, pMoreData, pExtraInfo);
	}
	
	/**{@inheritDoc}*/ @Override
	protected TSPattern getTypeSpec(TypeRef pTRef, boolean pIsAbstract, boolean pIsFinal, TypeRef pSuperRef,
			TypeRef[] pInterfaces, boolean pIsVerify, ParameterizedTypeInfo pTPInfo, MoreData pMoreData,
			MoreData pExtraInfo) {
		return new TSPattern(pTRef, this.getKindName(), pIsAbstract, pIsFinal, pSuperRef, pInterfaces, pTPInfo, pMoreData, pExtraInfo);
	}

	/**{@inheritDoc}*/ @Override
	public TypeSpecCreator getTypeSpecCreator(final boolean pIsAbstract, final boolean pIsFinal, final TypeRef pSuperRef,
			final TypeRef[] pInterfaces, final MoreData pMoreData,
			final MoreData pExtraInfo) {
		return getTypeSpecCreator(pIsAbstract, pIsFinal, pSuperRef, pInterfaces, null, pMoreData, pExtraInfo);
	}

	/**{@inheritDoc}*/ @Override
	public TypeSpecCreator getTypeSpecCreator(final boolean pIsAbstract, final boolean pIsFinal, final TypeRef pSuperRef,
			final TypeRef[] pInterfaces, final ParameterizedTypeInfo pTPInfo, final MoreData pMoreData,
			final MoreData pExtraInfo) {
		return new TypeSpecCreator() {
			public TypeSpec newTypeSpec(Engine pEngine, TypeRef pTRef, boolean pIsVerify, Documentation pDocument) {
				return getTypeSpec(pTRef, pIsAbstract, pIsFinal, pSuperRef, pInterfaces, pIsVerify, pTPInfo, pMoreData,
						MoreData.combineMoreData(
							(pDocument == null)?null:new MoreData(Documentation.MIName_Documentation, pDocument),
							pExtraInfo));
			}
		};
	}
	
	// DObject ---------------------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	protected Pattern doType_newDObject(Context pContext, Type pTheType) {
		return new Pattern(pContext, pTheType);
	}
	
	// Type checking ---------------------------------------------------------------------

	/**{@inheritDoc}*/ @Override
	protected Class<? extends Type> getTypeClass(Context pContext) {
		return TPattern.class;
	}
	/**{@inheritDoc}*/ @Override
	protected Class<?> getTypeDataClass(Context pContext, TypeSpec pSpec) {
		if(!(pSpec instanceof TSPattern)) return null;
		return Pattern.class;
	}
	
	// Type Construction -----------------------------------------------------------------

	/**{@inheritDoc}*/ @Override
	protected Type getType(Engine pEngine, Context pContext, TypeSpec pSpec) {
		if(!(pSpec instanceof TSPattern)) return null;
		return new TPattern(this, (TSPattern)pSpec);
	}

	/**{@inheritDoc}*/ @Override
	protected boolean isVirtual(Context pContext) {
		return false;
	}
	
	// Type Checking
	
	/**{@inheritDoc}*/ @Override
	protected boolean checkIfTypeCanTypeBeAssignedByTypeWith_Revert(Context pContext, Engine pEngine, TypeSpec TheSpec,
			TypeSpec BySpec) {
		if(super.checkIfTypeCanTypeBeAssignedByTypeWith_Revert(pContext, pEngine, TheSpec, BySpec))
			return true;

		if(!(BySpec instanceof TSPattern)) return false;
		// If the type of TheSpec can be assigned by TDataHolder then it can be assigned by this object
		return MType.CanTypeRefByAssignableByInstanceOf(pContext, pEngine, TheSpec.getTypeRef(), UPattern.TREF_Pattern);
	}
}
