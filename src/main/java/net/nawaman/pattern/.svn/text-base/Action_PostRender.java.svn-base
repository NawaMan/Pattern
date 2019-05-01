package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.Engine;
import net.nawaman.curry.Executable;

/** Actions to be performed after the rendered is rendered */
abstract public class Action_PostRender extends OtherAction {
	
	static private final long serialVersionUID = 98251522321222325L;
	
	/** Constructs a Port action */
	protected Action_PostRender(Executable pCondition) {
		super(pCondition);
	}
	
	/** Performs the action */
	abstract protected void doAction(Context $Context);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a post rendering Action */
	static public class Simple extends Action_PostRender {
		
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
			this.executeExecutable($Context, this.Exec, null);
		}
		
		/**{@inheritDoc}*/
		public String toString(Engine $Engine) {
			return String.format("PostRendere(%s):{%s}", $Engine.toDetail(this.Condition), $Engine.toDetail(this.Exec));
		}
	}
}
