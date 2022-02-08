package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;

/** Actions to be performed just before the rendered is rendered */
abstract public class Action_PreRender extends OtherAction {
	
	static private final long serialVersionUID = 21563516516546565L;
	                                             
	/** Constructs a Port action */
	protected Action_PreRender(Executable pCondition) {
		super(pCondition);
	}
	
	/** Performs the action */
	abstract protected void doAction(Context $Context);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a pre rendering Action */
	static public class Simple extends Action_PreRender {
		
		static private final long serialVersionUID = 21416541686154655L;
		
		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected void doAction(Context $Context) {
			// Get the value
			if(this.Exec == null) return;
			this.executeExecutable($Context, this.Exec, null);
		}
	}
}
