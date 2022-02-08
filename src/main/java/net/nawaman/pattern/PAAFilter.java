package net.nawaman.pattern;

import java.util.Vector;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;
import net.nawaman.curry.Expression;
import net.nawaman.curry.LocationSnapshot;

abstract public class PAAFilter extends PAPostAssignment {
	
	static private final long serialVersionUID = 12458522459845525L;
	
	/** Constructs a Port action */
	protected PAAFilter(Executable pCondition) {
		super(pCondition);
	}
	
	/** Performs the action */
	abstract protected boolean doAssert(Context $Context, Port Port, Object EachValue);
	
	/**{@inheritDoc}*/ @Override
	protected void doAction(Context $Context, Port Port, Object PortValue) {
		if(!(Port.PData.getKind() instanceof PKCollection)) {
			throw new PatternActionError(
				String.format(
					"\nIn compatible final value: only collection port can be filtered: %s <PAFiltered:24>\n    %s",
					Port, this.ARecord.getLocationSnapshot()
				)
			);
		}
		
		Vector<FinalValue> FinalValues = ((PFVCollection)Port.IPIFinal).FinalValues;
		for(FinalValue Value : FinalValues) {
			boolean IsVerified = this.doAssert($Context, Port, Value.Value);
			if(IsVerified) continue;
			
            ActionRecord     AR = this.ARecord;
            LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
			throw new PatternActionError(
				String.format(
					"\nPort filtering fail: %s <PAFiltered:36>\n    %s" +
					"\nBy '%s'\n    %s",
					Port, LS, Value.Value, Value.FinalAction.ARecord
				)
			);	
		}
	}
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port filter */
	static public class Simple extends PAAFilter {
		
		static private final long serialVersionUID = 24687465168465465L;
		
		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			
			if(pExec == null) pExec = Expression.TRUE;
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected boolean doAssert(Context $Context, Port Port, Object EachValue) {
			// Get the value
			Object O = this.executeExecutable($Context, this.Exec, Port, EachValue);
			
			// Process the value
			return Boolean.TRUE.equals(O);
		}
	}
}