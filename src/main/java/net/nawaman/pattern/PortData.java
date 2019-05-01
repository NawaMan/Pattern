package net.nawaman.pattern;

import java.io.Serializable;
import java.util.Hashtable;

import net.nawaman.curry.Engine;
import net.nawaman.curry.Expression;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.TypeRef;

/** The source or the Type of a port */
abstract public class PortData implements Serializable {
    
    private static final long serialVersionUID = -7456876300054892528L;
    
	// Caches And Constants -----------------------------------------------------------------------
    
    static Hashtable<TypeRef, Expression> TypeAsExpressions = new Hashtable<TypeRef, Expression>();
    
	// Constructor --------------------------------------------------------------------------------
	
	/** Constructs a PortData */
	PortData(PortKind pKind) {
		this.Kind = (pKind == null) ? PKSingle.Instance : pKind;
	}
	
	// Elements -----------------------------------------------------------------------------------

	/** Returns the port source */
	abstract public Pattern  getPattern();
	/** Returns the port name of this port source */
	abstract public String   getPortName();
	/** Returns the port info of this port */
	abstract public PortInfo getPortInfo();
	/** Returns the port type */
	abstract public TypeRef  getTypeRef();
	/** Returns the engine usable by the port */
	abstract public Engine   getEngine();
	
	final PortKind Kind;
	
	/** Returns the port kind */
	public PortKind getKind() {
		return this.Kind;
	}
	
	/** Returns the port type as an expression*/
	Expression getTypeAsExpression() {
		TypeRef TR = this.getTypeRef();
		if(TR == null) TR = TKJava.TAny.getTypeRef();
		Expression Expr = TypeAsExpressions.get(TR);
		if(Expr == null) {
			Expr = Expression.toExpr(this.getEngine().getExecutableManager().newType(TR));
			TypeAsExpressions.put(TR, Expr);
		}
		return Expr;
	}
	
	private Boolean Equals(
	        final Object pObj1,
	        final Object pObj2) {
        if (pObj1 != pObj2) {
            if (pObj1 == null)
                return false;
            if (!pObj1.equals(pObj2))
                return false;
        }
        return null;
	}
	
	@Override
	public boolean equals(Object pObj) {
	    if (!(pObj instanceof PortData))
	        return false;
	    
	    final PortData aPData = (PortData)pObj;
	    if (this == aPData)
	        return true;
	    
	    final Pattern aThisPattern = this.getPattern();
	    final Pattern aDataPattern = aPData.getPattern();
	    if (aThisPattern != aDataPattern)
	        return false;
        
        final Engine aThisEngine = this.getEngine();
        final Engine aDataEngine = aPData.getEngine();
        if (aThisEngine != aDataEngine)
            return false;
	    
	    final String  aThisPName = this.getPortName();
	    final String  aDataPName = aPData.getPortName();
	    final Boolean aIsNameEquals = Equals(aThisPName, aDataPName);
	    if (aIsNameEquals != null)
	        return aIsNameEquals.booleanValue();
        
        final TypeRef aThisPType = this.getTypeRef();
        final TypeRef aDataPType = aPData.getTypeRef();
        final Boolean aIsTypeEquals = Equals(aThisPType, aDataPType);
        if (aIsTypeEquals != null)
            return aIsTypeEquals.booleanValue();
	    
        final PortInfo aThisPInfo = this.getPortInfo();
        final PortInfo aDataPInfo = aPData.getPortInfo();
        final Boolean aIsInfoEquals = Equals(aThisPInfo, aDataPInfo);
        if (aIsInfoEquals != null)
            return aIsInfoEquals.booleanValue();
	    
	    return true;
	}

	// Lock --------------------------------------------------------------------
	
	/** This method will help limiting the implementation of this interface to be within this package. */
	abstract Pattern.LocalLock getLocalInterface(Pattern.LocalLock pLocalInterface);
}
