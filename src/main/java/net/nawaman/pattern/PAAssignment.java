package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;

/** Port action that should be performed BEFORE the value is instantialized */
abstract public class PAAssignment extends PortAction {
	
	static private final long serialVersionUID = 22626132654685465L;
	
	/** Constructs a Port action */
	protected PAAssignment(Executable pCondition) {
		super(pCondition);
	}
	/** Constructs a Port action */
	protected PAAssignment(Executable pCondition, boolean pIsDefaultAssignment) {
		super(pCondition);
		this.IsDefaultAssignment = pIsDefaultAssignment;
	}
	
	boolean IsDefaultAssignment = true;
	/** Checks if this action is a default assignment */
	public boolean isDefaultAssignment() {
		return IsDefaultAssignment;
	}
	
	/** Performs the action */
	abstract protected FinalValue doAction(Context $Context, Port IPort);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port Assignment Action */
	static public class Simple extends PAAssignment {
		
		static private final long serialVersionUID = 71256223612325213L;

		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec, boolean pIsDefaultAssignment) {
			super(pCondition, pIsDefaultAssignment);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected FinalValue doAction(Context $Context, Port Port) {
			// Get the value
			Object Value = this.executeExecutable($Context, this.Exec, Port);
			
			// Returns the the final-value object
			PortData   PData  = Port.PData;
			FinalValue FValue = PData.getKind().newFinalValue(
			        PData.getEngine(),
			        Value,
			        this,
			        Port);

			return FValue;
		}
	}
}
