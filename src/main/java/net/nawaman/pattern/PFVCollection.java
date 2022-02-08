package net.nawaman.pattern;

import java.lang.reflect.*;
import java.util.*;

import net.nawaman.util.*;

/** Port Final Value for List */
public class PFVCollection extends FinalValue {

	PFVCollection(Object pFinalValue, Action pFinalAction) {
		super(pFinalValue, pFinalAction);
	}
	
	Vector<FinalValue> FinalValues = new Vector<FinalValue>();
	
	/**{@inheritDoc}*/ @Override
	public Object getValue() {
		if(this.Value == null) return null;
		
		// Try to clone it first
		Method M = UClass.getMethod(this.Value.getClass(), "clone", false, (Object[])null);
		if(M == null) return this.Value;
		try { return M.invoke(this.Value, (Object[])null); }
		catch (Exception e) { return this.Value; }
	}
	
	/** Returns the length of the final result array */
	public int getLength() {
		return this.FinalValues.size();
	}
	/** Returns the value at the index */
	public Object getValue(int Index) {
		if((Index < 0) || (Index >= this.getLength())) return null;
		return this.FinalValues.get(Index).Value;
	}
	/** Returns the final value object at the index */
	public FinalValue getFinalValue(int Index) {
		if((Index < 0) || (Index >= this.getLength())) return null;
		return this.FinalValues.get(Index);
	}
	
	/**{@inheritDoc}*/ @Override
	public String toString() {
		StringBuilder SB = new StringBuilder();
		SB.append(String.format("FinalValue: %s\n    %s", this.getValue(), this.getActionRecord()));
		
		List<?> L = (List<?>)this.Value;
		if(L != null) {
			for(int i = 0; i < L.size(); i++) {
				Object O = L.get(i);
				SB.append(String.format("  [%d]: %s\n    %s", i, O, this.getFinalValue(i).getActionRecord()));
			}
		}
		
		return SB.toString();
	}
}