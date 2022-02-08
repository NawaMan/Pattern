package net.nawaman.pattern;

import java.util.Vector;

public class PIDConfigurable extends PortInternalData {
	
	/** Configurations are Actions that are done to the final instance */  
	Vector<PAPostAssignment> Configurations = null;
	
	/**{@inheritDoc}*/ @Override
	protected void addAction(PortAction PAction) {
		if(PAction instanceof PAPostAssignment) {
			if(this.Configurations == null) this.Configurations = new Vector<PAPostAssignment>();
			this.Configurations.add((PAPostAssignment)PAction);
			return;
		}
		super.addAction(PAction);
	}
	
	@Override
	protected void mergeBy_More(final PortInternalData pPIData) {
	    super.mergeBy_More(pPIData);	    
	    final PIDConfigurable aFrom = (PIDConfigurable)pPIData;
	    this.Configurations = this.mergeVector(this.Configurations, aFrom.Configurations);
	}

}
