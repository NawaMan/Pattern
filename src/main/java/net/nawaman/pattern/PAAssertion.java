package net.nawaman.pattern;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;
import net.nawaman.curry.Expression;
import net.nawaman.curry.LocationSnapshot;

abstract public class PAAssertion extends PAPostRendering {
	
	static private final long serialVersionUID = 85416516516515846L;
	
	/** Constructs a Port action */
	protected PAAssertion(Executable pCondition) {
		super(pCondition);
	}
	
	/** Performs the action */
	abstract protected boolean doAssert(Context $Context, Port Port, Object PortValue);
	
	/**{@inheritDoc}*/ @Override
	protected void doAction(Context $Context, Port Port, Object PortValue) {
		boolean IsVerified = this.doAssert($Context, Port, PortValue);
		if(IsVerified) return;
		
		String ExprStr = "";
		if(this instanceof Simple)
			ExprStr = String.format(
				"Expr:\n        %s\n    ",
				Port.PData.getEngine().toDetail(((Simple)this).Exec)
			); 
		
        ActionRecord     AR = this.ARecord;
        LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
		throw new PatternActionError(
			String.format(
				"\nPort assertion error: %s <PAAssertion:36>\n    %s%s",
				Port, ExprStr, LS
			)
		);
	}
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port Assignment Action */
	static public class Simple extends PAAssertion {
		
		static private final long serialVersionUID = 13546546546549851L;
		
		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			
			if(pExec == null) pExec = Expression.TRUE;
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected boolean doAssert(Context $Context, Port Port, Object PortValue) {
			// Get the value
			Object O;
			if((this.Exec != null) && (this.Exec.getSignature().getParamCount() == 0))
				 O = this.executeExecutable($Context, this.Exec, Port);
			else O = this.executeExecutable($Context, this.Exec, Port, PortValue);
			
			// Process the value
			return Boolean.TRUE.equals(O);
		}
	}
}
