package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;

/** Action that perform just before a port is rendered. */
abstract public class PAPreRendering extends PortAction {
	
	static private final long serialVersionUID = 5664546546512365L;
	
	/** Constructs a Port action */
	protected PAPreRendering(Executable pCondition) {
		super(pCondition);
	}
	
	/** Performs the action */
	abstract protected void doAction(Context $Context, Port Port);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port Assignment Action */
	static public class Simple extends PAPreRendering {
		
		static private final long serialVersionUID = 25465165165165152L;
		
		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected void doAction(Context $Context, Port Port) {
			// Get the value
			if(this.Exec == null) return;
			this.executeExecutable($Context, this.Exec, Port);
		}
	}

}
