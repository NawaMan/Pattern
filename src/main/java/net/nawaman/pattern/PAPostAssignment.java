package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;

/** Port action that should be performed AFTER the value is instantiated */
abstract public class PAPostAssignment extends PortAction {
	
	static private final long serialVersionUID = 45721541541524414L;
	
	/** Constructs a Port action */
	protected PAPostAssignment(Executable pCondition) {
		super(pCondition);
	}
	
	/** Returns the number indicating priority of the action. 0 or less is the highest */
	protected int getPriority() {
		return 0;
	}
	
	/** Performs the action */
	abstract protected void doAction(Context $Context, Port Port, Object PortValue);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port Assignment Action */
	static public class Simple extends PAPostAssignment {
		
		static private final long serialVersionUID = 64556546546565665L;
		
		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected void doAction(Context $Context, Port Port, Object PortValue) {
			// Get the value
			if((this.Exec != null) && (this.Exec.getSignature().getParamCount() == 0))
				 this.executeExecutable($Context, this.Exec, Port);
			else this.executeExecutable($Context, this.Exec, Port, PortValue);
		}
	}

}
