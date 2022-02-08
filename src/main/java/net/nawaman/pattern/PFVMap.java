package net.nawaman.pattern;

import java.lang.reflect.*;
import java.util.*;

import net.nawaman.curry.*;
import net.nawaman.curry.Instructions_Core.*;
import net.nawaman.pattern.PIDMap.*;
import net.nawaman.util.*;
import net.nawaman.util.data.*;

public class PFVMap extends FinalValue {

    PFVMap(Object pFinalValue, Action pFinalAction) {
		super(pFinalValue, pFinalAction);
	}
    
    ComparedMap<Object, Port> FinalValues = null;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void createFinalValue(
            final Context $Context,
            final Port    pPort) {
        final Engine      $Engine      = pPort.PData.getEngine();
        final MExecutable $MExecutable = $Engine.getExecutableManager();
        
        final TypeRef    aPortRef   = pPort.PData.getTypeRef();
        final TypeRef[]  aParamRefs = aPortRef.getParameters($Engine);
        final PKMap      aPKind     = (PKMap)pPort.PData.getKind();
        final TypeRef    aMapRef    = aPKind.getSimpleTypeRefFor($Engine, aParamRefs);
        final Expression aMapType   = $MExecutable.newType(aMapRef);
        
        final PIDMap              aInternalData  = (PIDMap)pPort.IPIData;
        final Comparator          aKeyComparator = aInternalData.getKeyComparator();
        final Map<Object, Object> aMap           = new ComparedMap<Object, Object>(aKeyComparator);
        final Expression          aNewInstExpr   = $MExecutable.newExpr(Inst_NewInstance.Name, aMapType, aMap);
        this.Value       = $Engine.execute(aNewInstExpr);
        this.FinalValues = new ComparedMap<Object, Port>(aKeyComparator);
        aInternalData.collapse($Engine, aKeyComparator);
        
        // Do the data assignment
        final Vector<PIDMapElement> aAData = aInternalData.AssociationData;
        for (final PIDMapElement aElement : aAData) {
            final Object aKey    = aElement.Key;
            final Port   aPValue = aElement.VPort;
            
            aPValue.render($Context);
            final Object aValue  = aPValue.getData();
            
            this.FinalValues.put(aKey, aPValue);
            aMap            .put(aKey, aValue);
        }
        
        final CanBeImmutable aNativeValue = (CanBeImmutable)((DObjectStandalone)this.Value).getAsNative();
        aNativeValue    .toImmutable();
        this.FinalValues.toImmutable();
    }
	
    @SuppressWarnings("rawtypes")
    private Map getValueAsMap() {
        final Object aValueObj = this.Value;
        if (aValueObj == null)
            return null;
        
        final Map aValue;
        if (!(aValueObj instanceof DObjectStandalone)) {
            if (!(aValueObj instanceof Map))
                return null;
            aValue = (Map)aValueObj;
        } else {
            aValue = (Map)((DObjectStandalone)aValueObj).getAsNative();
        }
        return aValue;
	}
	
	/**{@inheritDoc}*/ @Override
	public Object getValue() {
		if(this.Value == null)
		    return null;
		
		// Try to clone it first
		Method M = UClass.getMethod(this.Value.getClass(), "clone", false, (Object[])null);
		if(M == null)
			return this.Value;
		
		try {
			return M.invoke(this.Value, (Object[])null);
		} catch (Exception e) {
			return this.Value;
		}
	}
	
	/** Returns the length of the final result array */
    @SuppressWarnings("rawtypes")
    public int getLength() {
        if (this.FinalValues != null)
            return this.FinalValues.size();
        
        final Map aValue = this.getValueAsMap();
        if (aValue == null)
            return 0;
        
        final int aSize = aValue.size();
		return aSize;
	}
	
	/** Returns the value associated with the key */
    @SuppressWarnings("rawtypes")
    public Object getValue(Object Key) {
        if (this.FinalValues != null)
            return this.FinalValues.get(Key).getData();
        
        final Map aValue = this.getValueAsMap();
        if (aValue == null)
            return null;
        
        final Object aElement = aValue.size();
        return aElement;
	}
	
    /** Returns the value associated with the key */
    @SuppressWarnings("rawtypes")
    public Port getValueAsPort(Object Key) {
        if (this.FinalValues != null) {
            final Port aPort = this.FinalValues.get(Key);
            return aPort;
        }
        
        final Map aMap = this.getValueAsMap();
        if (aMap == null)
            return null;
        
        final boolean aIsContainKey = aMap.containsKey(Key);
        if (!aIsContainKey)
            return null;
        
        final Object aElement = aMap.get(Key);
        
        final TypeRef  aAnyRef = TKJava.TAny.getTypeRef();
        final PortInfo aPInfo  = new PortInfo(aAnyRef, PKSingle.Instance, false, null, null);
        final Port     aPort   = new Port(null, aPInfo, PKSingle.Instance);
        aPort.setData(aElement);
        return aPort;
    }
    
    /** Checks if the map contains the key */
    @SuppressWarnings("rawtypes")
    public boolean containsKey(final Object pKey) {
        if (this.FinalValues != null) {
            final boolean aIsContainKey = this.FinalValues.containsKey(pKey);
            return aIsContainKey;
        }
        
        final Map aMap = this.getValueAsMap();
        if (aMap == null)
            return false;
        
        final boolean aIsContainKey = aMap.containsKey(pKey);
        return aIsContainKey;
    }
    
    /** Returns the record of the final value */
    public Action getAction(Object Key) {
        if (this.FinalValues != null) {
            final Port aPValue = this.FinalValues.get(Key);
            if (aPValue == null)
                return null;
            
            final FinalValue aFValue = aPValue.IPIFinal;
            final Action     aAction = aFValue.getAction();
            return aAction;
        }
        
        final Action aAction = this.getAction();
        return aAction;
    }
    /** Returns the record of the final value */
    public ActionRecord getActionRecord(Object Key) {
        Action A = this.getAction(Key);
        if(A == null)
            return null;
        return A.getActionRecord();
    }
	/** Returns the final value object at the index */
    @SuppressWarnings("rawtypes")
    public FinalValue getFinalValue(Object Key) {
        if (this.FinalValues != null) {
            final Port aPValue = this.FinalValues.get(Key);
            if (aPValue == null)
                return null;
            
            final FinalValue aFValue = aPValue.IPIFinal;
            return aFValue;
        }
        
        final Map aMap = this.getValueAsMap();
        if (aMap == null)
            return null;
        
        Object aValue = this.getValue(Key);
        if ((aValue == null) && !aMap.containsKey(Key))
            return null;
        
        Action     aAction = this.getAction();
        FinalValue aFValue = new FinalValue(aValue, aAction);
		return aFValue;
	}
	
	/**{@inheritDoc}*/ @Override
	public String toString() {
		StringBuilder SB = new StringBuilder();
		SB.append(String.format("FinalValue: %s\n    %s", this.getValue(), this.getActionRecord()));
		
		Map<?,?> M = (Map<?,?>)this.Value;
		if (M != null) {
			for (Object Key : M.keySet()) {
				Object Data = M.get(Key);
				SB.append(
					String.format(
						"  [[%d]]: %s\n    %s",
						Key, Data,
						this.getFinalValue(Key).getActionRecord()
					)
				);
			}
		}
		
		return SB.toString();
	}
}
