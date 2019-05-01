package net.nawaman.pattern;

import java.util.HashMap;

import net.nawaman.curry.*;
import net.nawaman.curry.Instructions_Core.*;
import net.nawaman.util.*;

/** Single valued port kind */
public final class PKCollection extends PortKind {
	
	static private final long serialVersionUID = 1356352165165415441L;
	
	/** The name of the kind */
	static public final String Name = "Collection";
	
	static public final PKCollection Instance = new PKCollection();
	
	/** Default construction */
	PKCollection() {}
	
	/**{@inheritDoc}*/ @Override
	public String getName() {
		return Name;
	}
	
	static HashMap<Engine, TypeRef> BaseTypes       = new HashMap<Engine, TypeRef>();
	static HashMap<Engine, TypeRef> BaseSimpleTypes = new HashMap<Engine, TypeRef>();
	
	/**{@inheritDoc}*/ @Override
	public TypeRef getBaseTypeRef(Engine $Engine) {
		return getListTypeRef($Engine);
	}
	
	// TODO - Refactor here
	static public TypeRef getListTypeRef(Engine $Engine) {
		TypeRef BRef = BaseTypes.get($Engine);
		if(BRef == null) {
			BRef = new TLPackage.TRPackage("curry", "List");
			BaseTypes.put($Engine, BRef);
		}
		return BRef;
	}
    static public TypeRef getSimpleListTypeRef(Engine $Engine) {
        TypeRef BRef = BaseSimpleTypes.get($Engine);
        if(BRef == null) {
            BRef = new TLPackage.TRPackage("pattern~>data", "SimpleList");
            BaseSimpleTypes.put($Engine, BRef);
        }
        return BRef;
    }
	
	static HashMap<Engine, HashMap<TypeRef, TypeRef>> Types       = new HashMap<Engine, HashMap<TypeRef, TypeRef>>();
	static HashMap<Engine, HashMap<TypeRef, TypeRef>> SimpleTypes = new HashMap<Engine, HashMap<TypeRef, TypeRef>>();
	
	// TODO - Refactor this
	
	/**{@inheritDoc}*/ @Override
	public TypeRef getTypeRefFor(Engine $Engine, TypeRef ... ParameterTypeRefs) {
		TypeRef BTRef = this.getBaseTypeRef($Engine);
		HashMap<TypeRef, TypeRef> Ts = Types.get($Engine);
		if(Ts == null) {
			Ts = new HashMap<TypeRef, TypeRef>();
			Types.put($Engine, Ts);
		}
		TypeRef DRef = ParameterTypeRefs[0];
		TypeRef TRef = Ts.get(DRef);
		if(TRef == null) {
			TRef = new TLParametered.TRParametered(BTRef, ParameterTypeRefs);
			Ts.put(DRef, TRef);
		}
		return TRef;
	}
	
    public TypeRef getSimpleTypeRefFor(Engine $Engine, TypeRef ... ParameterTypeRefs) {
        TypeRef BTRef = PKCollection.getSimpleListTypeRef($Engine);
        HashMap<TypeRef, TypeRef> Ts = SimpleTypes.get($Engine);
        if(Ts == null) {
            Ts = new HashMap<TypeRef, TypeRef>();
            SimpleTypes.put($Engine, Ts);
        }
        TypeRef DRef = ParameterTypeRefs[0];
        TypeRef TRef = Ts.get(DRef);
        if(TRef == null) {
            TRef = new TLParametered.TRParametered(BTRef, ParameterTypeRefs);
            Ts.put(DRef, TRef);
        }
        return TRef;
    }
	
	/**{@inheritDoc}*/ @Override
	public PortInternalData newInternalData(
            final Engine  $Engine,
            final Port    pPort,
            final TypeRef pDataTypeRef) {
		// The default one
		return new PIDConfigurable();
	}
	
	/**{@inheritDoc}*/ @Override
	public FinalValue newFinalValue(
	        final Engine           $Engine,
	        final Object           pFinalValue,
	        final PAAssignment     pFinalAction,
	        final Port             pPort) {
		// The default one
		return new PFVCollection(pFinalValue, pFinalAction);
	}
    
    /**{@inheritDoc}*/ @Override
    Object tryNonNullDefaultFinalValue(
            Context          $Context,
            FinalValue       pFinalValue,
            TypeRef          pDataTypeRef,
            PortInternalData pInternalData) {
        if (!(pFinalValue instanceof PFVCollection) || (pFinalValue.getActionRecord() != null))
            return pFinalValue.getValue();
        
        final Engine              $Engine      = ExternalContext.getEngine($Context);
        final ParameteredTypeInfo aPTInfo      = pDataTypeRef.getParameteredTypeInfo($Engine);
        final TypeRef             aValueTRef   = aPTInfo.getParameterTypeRef(0);
        final TypeRef             aDefaultTRef = this.getSimpleTypeRefFor($Engine, aValueTRef);
        
        // When no one has set anything, let create the default one
        MExecutable $ME        = $Engine.getExecutableManager();
        Expression  aPType_Exp = $ME.newExpr(Inst_Type.Name, aDefaultTRef);
        Expression  aNewIn_Exp = $ME.newExpr(Inst_NewInstance.Name, aPType_Exp);
        Object      aFValue    = ExternalContext.execute($Context, aNewIn_Exp);
        pFinalValue.Value      = aFValue;
        
        if(aFValue instanceof DObjectStandalone)
            aFValue = ((DObjectStandalone)aFValue).getAsDObject();
        
        return aFValue;
    }
    
    /**{@inheritDoc}*/ @Override
    protected void notifyValueAssigned(
            final Context $Context,
            final Port    pPort,
            final boolean pIsHardAssigned) {
        if (!pIsHardAssigned)
            return;
        
        // Only the hard-assigned with be immutable at this state
        final Object aValue = pPort.IPIFinal.Value;
        if(aValue == null)
            return;
        // If the size is not null at this state, assume it have assigned and not new
        Object DO = ((DObjectStandalone)aValue).getAsNative();
        if(!((CanBeImmutable)DO).isImmutable())
            ((CanBeImmutable)DO).toImmutable();
    }
    
    /**{@inheritDoc}*/ @Override
    protected void notifyValueVerified(
            final Context $Context,
            final Port    pPort,
            final boolean pIsHardAssigned) {
        final Object aValue = pPort.IPIFinal.Value;
        if(aValue == null)
            return;
        
        // If the size is not null at this state, assume it have assigned and not new
        Object DO = ((DObjectStandalone)aValue).getAsNative();
        if(!((CanBeImmutable)DO).isImmutable())
            ((CanBeImmutable)DO).toImmutable();
    }
}