package net.nawaman.pattern;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;
import net.nawaman.curry.LocationSnapshot;

public abstract class PASortAppendable_Hash extends PAReOrganizeAppendable {
	
	static private final long serialVersionUID = 12458748582568456L;
	
	/** Constructs a Port action */
	protected PASortAppendable_Hash(Executable pCondition) {
		super(pCondition);
	}
	
	/** The comparator */
	static class EachHashComparator implements Comparator<Object[]> {
		/**{@inheritDoc}*/ @Override
	    public int compare(Object[] o1, Object[] o2) {
			return ((Integer)o1[0]).intValue() - ((Integer)o2[0]).intValue();
		}
	    /**{@inheritDoc}*/ @Override
	    public boolean equals(Object obj) {
	    	return this == obj;
	    }
	}
	
	static EachHashComparator EachHashComparator = new EachHashComparator();
	
	/**{@inheritDoc}*/ @Override
	protected void doAction(Context $Context, Port Port, Object PortValue) {
		if(!(Port.PData.getKind() instanceof PKCollection)) {
            ActionRecord     AR = this.ARecord;
            LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
			throw new PatternActionError(
				String.format(
					"\nIn compatible final value: only collection port can be filtered: %s <PASortAppendable_Hash:38>\n    %s",
					Port, LS
				)
			);
		}
		
		// Do the sort
		Vector<FinalValue> FValues = ((PFVCollection)Port.IPIFinal).FinalValues;
		if((FValues == null) || (FValues.size() <= 1)) return;	// No need to do anything
		
		Object[][] ToSort = new Object[FValues.size()][];
		for(int i = 0; i < ToSort.length; i++) {
			FinalValue PFV = FValues.get(i);
			int hash = this.hash($Context, Port, PFV.Value);
			ToSort[i] = new Object[] { hash, PFV };
		}
		
		Arrays.sort(ToSort, EachHashComparator);
		FValues = new Vector<FinalValue>();
		for(int i = 0; i < ToSort.length; i++) {
			FValues.add((FinalValue)(ToSort[i])[1]);
		}
		
		((PFVCollection)Port.IPIFinal).FinalValues = FValues;
		this.remakeFinalValue($Context, Port);
	}
	
	/** Performs the action */
	abstract protected int hash(Context $Context, Port Port, Object Each);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port ReOrganize Action for Appendable (List, Set)*/
	static public class Simple extends PASortAppendable_Hash {
		
		static private final long serialVersionUID = 52157852685565556L;

		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected int hash(Context $Context, Port Port, Object Each) {
			// Get the value
			Object Value = this.executeExecutable($Context, this.Exec, Port, Each);
			return (Value == null) ? 0 : Value.hashCode();
		}
	}
}
