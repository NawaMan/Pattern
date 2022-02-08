package net.nawaman.pattern;

import net.nawaman.curry.ActionRecord;

/** Information about the final value of a port */
public class FinalValue {

	FinalValue(Object pFinalValue, Action pFinalAction) {
		this.Value       = pFinalValue;
		this.FinalAction = pFinalAction;
	}
	
	/** The final value */
	Object Value;
	
	/** Returns the final value */
	public Object getValue() {
		return this.Value;
	}
	
	/** The record of the final value */
	Action FinalAction;
	
	/** Returns the record of the final value */
	public Action getAction() {
		return this.FinalAction;
	}
	/** Returns the record of the final value */
	public ActionRecord getActionRecord() {
		Action A = this.FinalAction;
		if(A == null) return null;
		
		return A.getActionRecord();
	}
	
	/**{@inheritDoc}*/ @Override
	public String toString() {
		return String.format("FinalValue: %s\n    %s", this.getValue(), this.getActionRecord());
	}
}