package net.nawaman.pattern;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;
import net.nawaman.curry.LocationSnapshot;

/** Action to re-organize Appendable */
abstract public class PASortAppendable_Compare extends PAReOrganizeAppendable {
	
	static private final long serialVersionUID = 12458748582568456L;
	
	/** Constructs a Port action */
	protected PASortAppendable_Compare(Executable pCondition) {
		super(pCondition);
	}
	
	/** The comparator */
	static class EachComparator implements Comparator<FinalValue> {
		PASortAppendable_Compare Action;
		Context                        $Context;
		Port                           Port;
		EachComparator(PASortAppendable_Compare A, Context C, Port P) {
			this.Action   = A;
			this.$Context = C;
			this.Port     = P;
		}
		/**{@inheritDoc}*/ @Override
	    public int compare(FinalValue O1, FinalValue O2) {
			return this.Action.compare(this.$Context, this.Port, O1.Value, O2.Value);
		}
	    /**{@inheritDoc}*/ @Override
	    public boolean equals(Object obj) {
	    	return this == obj;
	    }
	}
	
	/**{@inheritDoc}*/ @Override
	protected void doAction(Context $Context, Port Port, Object PortValue) {
		if(!(Port.PData.getKind() instanceof PKCollection)) {
            ActionRecord     AR = this.ARecord;
            LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
			throw new PatternActionError(
				String.format(
					"\nIn compatible final value: only collection port can be filtered: %s <PASortAppendable_Compare:45>\n    %s",
					Port, LS
				)
			);
		}
		
		// Do the sort
		Vector<FinalValue> FValues = ((PFVCollection)Port.IPIFinal).FinalValues;
		if((FValues == null) || (FValues.size() <= 1)) return;	// No need to do anything
		
		Collections.sort(FValues, new EachComparator(this, $Context, Port));
		this.remakeFinalValue($Context, Port);
	}
	
	/** Performs the action */
	abstract protected int compare(Context $Context, Port Port, Object First, Object Second);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port ReOrganize Action for Appendable (List, Set)*/
	static public class Simple extends PASortAppendable_Compare {
		
		static private final long serialVersionUID = 52157852685565556L;

		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected int compare(Context $Context, Port Port, Object First, Object Second) {
			// Get the value
			Object Value = this.executeExecutable($Context, this.Exec, Port, First, Second);
			return (Value == null) ? 0 : Value.hashCode();
		}
	}
}
