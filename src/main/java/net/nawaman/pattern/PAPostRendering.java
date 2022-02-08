package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.Executable;

/** Action that perform after a port is rendered. */
abstract public class PAPostRendering extends PortAction {
	
	static private final long serialVersionUID = 1236450506520325L;
	
	/** Constructs a Port action */
	protected PAPostRendering(Executable pCondition) {
		super(pCondition);
	}
	
	/** Performs the action */
	abstract protected void doAction(Context $Context, Port Port, Object PortValue);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port Assignment Action */
	static public class Simple extends PAPostRendering {
		
		static private final long serialVersionUID = 41252802150205002L;
		
		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected void doAction(Context $Context, Port Port, Object PortValue) {
			// Get the value
			if(this.Exec == null) return;
			ExecSignature ES = this.Exec.getSignature();
			if(ES.getParamCount() == 0)
				 this.executeExecutable($Context, this.Exec, Port);
			else this.executeExecutable($Context, this.Exec, Port, PortValue);
		}
	}

}
