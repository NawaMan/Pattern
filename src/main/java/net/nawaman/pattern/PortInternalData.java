package net.nawaman.pattern;

import java.util.Vector;

/** Internal Data of an Port */
class PortInternalData {
	
	/** PreRenderings are Actions that are done before the port is rendered */  
	Vector<PAPreRendering> PreRenderings = null;

	/** Default values */
	PAAssignment DefaultInstance = null;
	
	/** Instances are actions that will result in producing the final instance of the port */
	Vector<PAAssignment> Instances = null;
	
	/** PostRenderings are Actions that are done to the final instance after the instance is rendered */  
	Vector<PAPostRendering> PostRenderings = null;
	
	// Services --------------------------------------------------------------------------------------------------------
	
	/** Add a port action into the data */
	protected void setDefaultValueAction(PAAssignment PAction) {
		if(this.DefaultInstance != null) return;
		this.DefaultInstance = PAction;
	}
	
	/** Checks if the default value is assigned */
	protected boolean isDefaultValueAssigned() {
		return (this.DefaultInstance != null);
	}
	
	/** Add a port action into the data */
	protected void addAction(PortAction PAction) {
		if(PAction == null) throw new NullPointerException("Null Action...");
		
		if(PAction instanceof PAAssignment) {
			if(((PAAssignment)PAction).isDefaultAssignment())
				this.setDefaultValueAction((PAAssignment)PAction);
			else {
				if(this.Instances == null) this.Instances = new Vector<PAAssignment>();
				this.Instances.add((PAAssignment)PAction);
			}
			
		} else if(PAction instanceof PAPostRendering) {
			if(this.PostRenderings == null) this.PostRenderings = new Vector<PAPostRendering>();
			this.PostRenderings.add((PAPostRendering)PAction);
			
		} else if(PAction instanceof PAPreRendering) {
			if(this.PreRenderings == null) this.PreRenderings = new Vector<PAPreRendering>();
			this.PreRenderings.add((PAPreRendering)PAction);
			
		} else {
			throw new IllegalArgumentException("Unknown port-action type: " + PAction);
		}
	}
	
	
	// Merging -------------------------------------------------------------------------------------
	
	final protected <T> Vector<T> mergeVector(
	        final Vector<T> pTo,
	        final Vector<T> pFrom) {
	    if ((pFrom == null) || (pFrom.size() == 0))
	        return pTo;
	    
	    final Vector<T> aTo = (pTo == null) ? new Vector<T>() : pTo;
	    aTo.addAll(pFrom);
	    
	    return aTo;
	}
	private void mergeBy_General(final PortInternalData pPIData) {
	    if (pPIData == null)
	        return;
	    
	    if (this.getClass() != pPIData.getClass())
            this.throwIncompatiblePortInternalData_forMerging(pPIData);
	    
	    if ((this.DefaultInstance == null) && (pPIData.DefaultInstance != null))
	        this.DefaultInstance = pPIData.DefaultInstance;
	    this.PreRenderings  = this.mergeVector(this.PreRenderings,  pPIData.PreRenderings);
	    this.Instances      = this.mergeVector(this.Instances,      pPIData.Instances);
	    this.PostRenderings = this.mergeVector(this.PostRenderings, pPIData.PostRenderings);
	}
	
	final protected void throwIncompatiblePortInternalData_forMerging(
	        final PortInternalData pPIData) {
	    throw new PatternActionError(String.format(
	            "Unable to merge two port internal data: \n\tthis: %s\n\tby  : %s",
	            this,
	            pPIData
	        ));
	}
	
    void mergeBy_More(final PortInternalData pPIData) {}
	
	final void mergeBy(final PortInternalData pPIData) {
	    if (pPIData == null)
	        return;
	    this.mergeBy_General(pPIData);
	    this.mergeBy_More   (pPIData);
	}
	
}
