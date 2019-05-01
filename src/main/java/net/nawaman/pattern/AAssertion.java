package net.nawaman.pattern;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;
import net.nawaman.curry.LocationSnapshot;

/** Assertion action */
public abstract class AAssertion extends Action_PostRender {
	
	static private final long serialVersionUID = 85455145515565695L;
	
	/** Constructs an action */
	protected AAssertion(Executable pCondition) {
		super(pCondition);
	}
	
	/** Performs the action */
	abstract protected boolean doAssert(Context $Context);
	
	/**{@inheritDoc}*/ @Override
	protected void doAction(Context $Context) {
		boolean IsVerified = this.doAssert($Context);
		if(IsVerified) return;
		
		ActionRecord     AR = this.ARecord;
		LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
		throw new PatternActionError(
			String.format(
				"\nPattern(s) assertion error: %s <AAssertion:26>\n    %s",
				this, LS
			)
		);
	}
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a post rendering Action */
	static public class Simple extends AAssertion {
		
		static private final long serialVersionUID = 64521415425425546L;
		
		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected boolean doAssert(Context $Context) {
			// Get the value
			if(this.Exec == null) return true;
			
			Object O = this.executeExecutable($Context, this.Exec, null);
			if(O == null) return false;
			return ((Boolean)O).booleanValue();
		}
	}
}