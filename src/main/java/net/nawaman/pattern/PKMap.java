package net.nawaman.pattern;

import java.io.Serializable;
import java.util.*;

import net.nawaman.curry.*;
import net.nawaman.curry.Instructions_Core.*;
import net.nawaman.curry.util.MoreData;
import net.nawaman.util.*;

public class PKMap extends PortKind {
	
	static private final long serialVersionUID = 2154254525254527844L;

    static public final String CONFIG_NAME_NEW_MAP_ELEMENT_PORT = "newMapElementPort";
	static public final String CONFIG_NAME_GET_MAP_ELEMENT_PORT = "getMapElementPort";
	
	/** The name of the kind */
	static public final String Name = "Map";
    
    /** Name or MoreData entry that holds a map-port comparator */
    static public final String MIName_Comparator = "Comparator";
	
	static public final PKMap Instance = new PKMap();

	/** Default construction */
	PKMap() {}
	
	/**{@inheritDoc}*/ @Override
	public String getName() {
		return Name;
	}
	
	static HashMap<Engine, TypeRef> BaseTypes       = new HashMap<Engine, TypeRef>();
    static HashMap<Engine, TypeRef> BaseSimpleTypes = new HashMap<Engine, TypeRef>();
	
	/**{@inheritDoc}*/ @Override
	public TypeRef getBaseTypeRef(Engine $Engine) {
		return getMapTypeRef($Engine);
	}
	
	static public TypeRef getMapTypeRef(Engine $Engine) {
		TypeRef BRef = BaseTypes.get($Engine);
		if(BRef == null) {
			BRef = new TLPackage.TRPackage("curry", "Map");
			BaseTypes.put($Engine, BRef);
		}
		return BRef;
	}
	static public TypeRef getSimpleMapTypeRef(Engine $Engine) {
        TypeRef BRef = BaseSimpleTypes.get($Engine);
        if(BRef == null) {
            BRef = new TLPackage.TRPackage("pattern~>data", "WrappingMap");
            BaseSimpleTypes.put($Engine, BRef);
        }
        return BRef;
    }
	
	static HashMap<Engine, HashMap<TypeRef[], TypeRef>> Types       = new HashMap<Engine, HashMap<TypeRef[], TypeRef>>();
	static HashMap<Engine, HashMap<TypeRef[], TypeRef>> SimpleTypes = new HashMap<Engine, HashMap<TypeRef[], TypeRef>>();
	
    // TODO - Refactor this
	
	/**{@inheritDoc}*/ @Override
	public TypeRef getTypeRefFor(Engine $Engine, TypeRef ... ParameterTypeRefs) {
		if((ParameterTypeRefs == null) || (ParameterTypeRefs.length != 2))
            // TODO - Here should throw Exception
			return null;
		
		if(ParameterTypeRefs[0] == null) ParameterTypeRefs[0] = TKJava.TAny.getTypeRef();
		if(ParameterTypeRefs[1] == null) ParameterTypeRefs[1] = TKJava.TAny.getTypeRef();
		
		TypeRef BTRef = this.getBaseTypeRef($Engine);
		HashMap<TypeRef[], TypeRef> Ts = Types.get($Engine);
		if(Ts == null) {
			Ts = new HashMap<TypeRef[], TypeRef>();
			Types.put($Engine, Ts);
		}
		TypeRef TRef = Ts.get(ParameterTypeRefs);
		if(TRef == null) {
			TRef = new TLParametered.TRParametered(BTRef, ParameterTypeRefs);
			Ts.put(ParameterTypeRefs, TRef);
		}
		return TRef;
	}
    public TypeRef getSimpleTypeRefFor(Engine $Engine, TypeRef ... ParameterTypeRefs) {
        if((ParameterTypeRefs == null) || (ParameterTypeRefs.length != 2))
            // TODO - Here should throw Exception
            return null;
        
        if(ParameterTypeRefs[0] == null) ParameterTypeRefs[0] = TKJava.TAny.getTypeRef();
        if(ParameterTypeRefs[1] == null) ParameterTypeRefs[1] = TKJava.TAny.getTypeRef();
        
        TypeRef BTRef = PKMap.getSimpleMapTypeRef($Engine);
        HashMap<TypeRef[], TypeRef> Ts = SimpleTypes.get($Engine);
        if(Ts == null) {
            Ts = new HashMap<TypeRef[], TypeRef>();
            SimpleTypes.put($Engine, Ts);
        }
        TypeRef TRef = Ts.get(ParameterTypeRefs);
        if(TRef == null) {
            TRef = new TLParametered.TRParametered(BTRef, ParameterTypeRefs);
            Ts.put(ParameterTypeRefs, TRef);
        }
        return TRef;
    }
	
    @SuppressWarnings("rawtypes")
    /**{@inheritDoc}*/ @Override
	public PortInternalData newInternalData(
            final Engine  $Engine,
            final Port    pPort,
            final TypeRef pDataTypeRef) {
        final MoreData     aMD   = pPort.PData.getPortInfo().getMoreInfo();
        final Serializable aData = aMD.getData(PKMap.MIName_Comparator);
        
        final Comparator aComparator;
        if (aData instanceof Comparator) {
            aComparator = (Comparator)aData;
        } else if (aData instanceof String) {
            aComparator = GetComparator($Engine, (String)aData);
        } else if (aData instanceof Executable) {
            aComparator = new ExecComparator($Engine, (Executable)aData);
        } else {
            aComparator = null;
        }
        
		return new PIDMap(aComparator);
	}
	
    /**{@inheritDoc}*/ @Override
	public FinalValue newFinalValue(
	        final Engine           $Engine,
	        final Object           pFinalValue,
	        final PAAssignment     pFinalAction,
	        final Port             pPort) {
		return new PFVMap(pFinalValue, pFinalAction);
	}
    
    @SuppressWarnings("rawtypes")
    /**{@inheritDoc}*/ @Override
    Object tryNonNullDefaultFinalValue(
            Context          $Context,
            FinalValue       pFinalValue,
            TypeRef          pDataTypeRef,
            PortInternalData pInternalData) {
        if (!(pFinalValue instanceof PFVMap)
          || (pFinalValue.getActionRecord() != null)
          || (pFinalValue.getValue()        != null))
            return pFinalValue.getValue();
        
        final Engine     $Engine      = ExternalContext.getEngine($Context);
        final TypeRef[]  aParameters  = pDataTypeRef.getParameters($Engine);
        final TypeRef    aDefaultTRef = this.getSimpleTypeRefFor($Engine, (TypeRef[])aParameters);
        final Comparator aComparator  = $Engine.getDefaultComparator();
        
        // When no one has set anything, let create the default one
        MExecutable $ME        = $Engine.getExecutableManager();
        Expression  aPType_Exp = $ME.newExpr(Inst_Type.Name, aDefaultTRef);
        Expression  aNewIn_Exp = $ME.newExpr(Inst_NewInstanceByTypeRefs.Name, aPType_Exp, new TypeRef[] { TKJava.TComparator.getTypeRef() }, aComparator);
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
    protected void notifyValueConfigured(
            final Context $Context,
            final Port    pPort,
            final boolean pIsHardAssigned) {
        if (pIsHardAssigned) 
            return;
        
        final PFVMap aFVMap = ((PFVMap)pPort.IPIFinal);
        aFVMap.createFinalValue($Context, pPort);
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
	
	@Override
    protected Object config(Context $Context, Port pPort, String pInfoName, Object ... pParameters) {
        final boolean aIsPortRendered = pPort.isRendered();
        if (pPort.IPIData == null) {
            if (!aIsPortRendered) {
                final Engine  $Engine   = pPort.PData.getEngine();
                final TypeRef aDataTRef = pPort.PData.getTypeRef();
                pPort.IPIData = pPort.PData.Kind.newInternalData($Engine, pPort, aDataTRef);
            } else {
                if (CONFIG_NAME_NEW_MAP_ELEMENT_PORT.equals(pInfoName)) {
                    throw new PatternActionError(
                        String.format(
                            "A map port `%s` has already been render so it can no longer create a new element",
                            pPort
                        ),
                        $Context
                    );
                } else if (CONFIG_NAME_GET_MAP_ELEMENT_PORT.equals(pInfoName)) {
                    final PFVMap aFVMap      = (PFVMap)pPort.IPIFinal;
                    final Object aKey        = pParameters[0];
                    final Object aResultPort = aFVMap.getValueAsPort(aKey);
                    return aResultPort;
                }
            }
        }
	    
	    final Object aKey = pParameters[0];
        
	    if (CONFIG_NAME_NEW_MAP_ELEMENT_PORT.equals(pInfoName)) {	        
	        final Object aResult = ((PIDMap)pPort.IPIData).newElement(pPort, aKey);
	        return aResult;
	    } else if (CONFIG_NAME_GET_MAP_ELEMENT_PORT.equals(pInfoName)) {
            final Object aResult = ((PIDMap)pPort.IPIData).getElement(pPort, aKey);
            return aResult;
        }
        return null;
    }
	
	static class ExecComparator implements SerializableComparator<Object> {
        private static final long serialVersionUID = -7638140827480304656L;
	    ExecComparator(
                final Engine     $Engine,
	            final Executable $Exec) {
	        this.$Engine = $Engine;
	        this.$Exec   = $Exec;
	    }
        private Engine     $Engine;
	    private Executable $Exec;
        public @Override int compare(Object pO1, Object pO2) {
            final MExecutable $MExecutable = $Engine.getExecutableManager();
            final Object Result;
            if($Exec instanceof Expression) Result = $Engine     .execute       ($Exec);
            else if($Exec.isFragment())     Result = $MExecutable.runFragment   ($Exec);
            else if($Exec.isMacro())        Result = $MExecutable.execMacro     ($Exec, pO1, pO2);
            else if($Exec.isSubRoutine())   Result = $MExecutable.callSubRoutine($Exec, pO1, pO2);
            else throw new PatternActionError("\nInvalid executable error: <PKMap:245>");
            
            if (Result instanceof Number) {
                final int aCompareValue = ((Number)Result).intValue();
                return aCompareValue;
            }
            if (Result instanceof Boolean) {
                final boolean aIsEqual      = ((Boolean)Result).booleanValue();
                final int     aCompareValue = aIsEqual ? 0 : -1;
                return aCompareValue;
            }
            throw new PatternActionError("\nInvalid key-compare-result error: <PKMap:254>");
        }
        public @Override boolean equals(Object pObj) {
            return (this == pObj);
        }
	}
    
    static HashMap<Engine, HashMap<String, Comparator<Object>>> Comparators = null;
    
    static Comparator<Object> GetComparator(
            final Engine $Engine,
            final String pCmpStr) {
        if (PKMap.Comparators == null)
            PKMap.Comparators = new HashMap<Engine, HashMap<String, Comparator<Object>>>();
        
        HashMap<String, Comparator<Object>> aComparators = PKMap.Comparators.get($Engine);
        if (aComparators == null) {
            aComparators = new HashMap<String, Comparator<Object>>();
            PKMap.Comparators.put($Engine, aComparators);
        }
        
        Comparator<Object> aComparator = aComparators.get(pCmpStr);
        if (aComparator != null)
            return aComparator;
        
        if ("==".equals(pCmpStr)) {
            // Equals
            aComparator = new EqualComparator() {
                private static final long serialVersionUID = -2135465165121356516L;
                public @Override boolean equals(Object o1, Object o2) {
                    final boolean aIsEqual = $Engine.equals(o1, o2);
                    return aIsEqual;
                }
            };
        } else if ("===".equals(pCmpStr)) {
            // Is
            aComparator = new EqualComparator() {
                private static final long serialVersionUID = -1223156516565165162L;
                public @Override boolean equals(Object o1, Object o2) {
                    final boolean aIsEqual = $Engine.is(o1, o2);
                    return aIsEqual;
                }
            };
        } else if ("=#=".equals(pCmpStr)) {
            // Hash-Equals
            aComparator = new SimpleComparator() {
                private static final long serialVersionUID = -8135416513216516516L;
                public @Override int compare(Object o1, Object o2) {
                    final int aI1  = $Engine.hash(o1);
                    final int aI2  = $Engine.hash(o2);
                    final int aCmp = aI1 - aI2;
                    return aCmp;
                }
            };
        } else if ("<#>".equals(pCmpStr)) {
            // Compare
            aComparator = new SimpleComparator() {
                private static final long serialVersionUID = -3665416561721651655L;
                public @Override int compare(Object o1, Object o2) {
                    final int aCmp = $Engine.compares(o1, o2);
                    return aCmp;
                }
            };
        } else if ("$=".equals(pCmpStr)) {
            // ToString-equals
            aComparator = new EqualComparator() {
                private static final long serialVersionUID = -1003501652463442131L;
                public @Override boolean equals(Object o1, Object o2) {
                    final String aS1 = $Engine.toString(o1);
                    final String aS2 = $Engine.toString(o2);
                    final boolean aIsEqual = $Engine.equals(aS1, aS2);
                    return aIsEqual;
                }
            };
        } else if ("$$=".equals(pCmpStr)) {
            // ToDetail-equals
            aComparator = new EqualComparator() {
                private static final long serialVersionUID = -7156354351651621205L;
                public @Override boolean equals(Object o1, Object o2) {
                    final String aS1 = $Engine.toDetail(o1);
                    final String aS2 = $Engine.toDetail(o2);
                    final boolean aIsEqual = $Engine.equals(aS1, aS2);
                    return aIsEqual;
                }
            };
        } else if ("<$>".equals(pCmpStr)) {
            // ToString-compare
            aComparator = new SimpleComparator() {
                private static final long serialVersionUID = -2551312025350205230L;
                public @Override int compare(Object o1, Object o2) {
                    final String aS1  = $Engine.toString(o1);
                    final String aS2  = $Engine.toString(o2);
                    final int    aCmp = $Engine.compares(aS1, aS2);
                    return aCmp;
                }
            };
        } else if ("<$$>".equals(pCmpStr)) {
            // ToDetail-equals
            aComparator = new SimpleComparator() {
                private static final long serialVersionUID = -5021580546512120351L;
                public @Override int compare(Object o1, Object o2) {
                    final String aS1  = $Engine.toDetail(o1);
                    final String aS2  = $Engine.toDetail(o2);
                    final int    aCmp = $Engine.compares(aS1, aS2);
                    return aCmp;
                }
            };
        } else if ("$.#".equals(pCmpStr)) {
            // ToString-length
            aComparator = new SimpleComparator() {
                private static final long serialVersionUID = -3252005616404776410L;
                public @Override int compare(Object o1, Object o2) {
                    final String aS1  = $Engine.toString(o1);
                    final String aS2  = $Engine.toString(o2);
                    final int    aSL1 = aS1.length();
                    final int    aSL2 = aS2.length();
                    return aSL1 - aSL2;
                }
            };
        }
        
        aComparators.put(pCmpStr, aComparator);
        return aComparator;
    }
    
    static abstract class SimpleComparator implements SerializableComparator<Object> {
        private static final long serialVersionUID = 5568989260964151201L;
        public @Override boolean equals(Object pObj) {
            return (this == pObj);
        }
    }
    static abstract class EqualComparator extends SimpleComparator {
        private static final long serialVersionUID = -6885302484311894623L;
        public @Override int compare(Object o1, Object o2) {
            final boolean aIsEquals = this.equals(o1, o2);
            final int     aCompared = aIsEquals ? 0 : -1;
            return aCompared;
        }
        public abstract boolean equals(Object o1, Object o2);
    }
}
