package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.DObject;
import net.nawaman.curry.Executable;

public abstract class PAReOrganizeAppendable extends PAPostAssignment {
	
	static private final long serialVersionUID = 12458748582568456L;
	
	/** Constructs a Port action */
	protected PAReOrganizeAppendable(Executable pCondition) {
		super(pCondition);
	}
	
	/**{@inheritDoc}*/ @Override
	final protected int getPriority() {
		return Integer.MAX_VALUE;
	}
	
	/** Reconstructs final value from the new re-organized PortFinalValue Object */
	final protected void remakeFinalValue(Context $Context, Port Port) {
		DObject DO = (DObject)Port.IPIFinal.Value;
		
		DObject NewDO = (DObject)DO.getTheType().newInstance((Object[])null);
		for(int i = 0; i < ((PFVCollection)Port.IPIFinal).FinalValues.size(); i++) {
			FinalValue PFV = ((PFVCollection)Port.IPIFinal).FinalValues.get(i);
			NewDO.invoke("add", PFV.Value);
		}
		Port.IPIFinal.Value = DO = NewDO;
	}

}
