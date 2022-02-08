package net.nawaman.pattern;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.Context;
import net.nawaman.curry.CurryError;
import net.nawaman.curry.Engine;
import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.Executable;
import net.nawaman.curry.Expression;
import net.nawaman.curry.ExternalContext;
import net.nawaman.curry.JavaExecutable;
import net.nawaman.curry.MType;
import net.nawaman.curry.TKInterface;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.Type;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.AttributeInfo.AIDirect;
import net.nawaman.curry.Instructions_Core.Inst_NewInstance;
import net.nawaman.curry.Instructions_DataHolder.Inst_GetDHValue;
import net.nawaman.curry.Instructions_Executable.Inst_Call;
import net.nawaman.curry.Instructions_Executable.Inst_Exec;
import net.nawaman.curry.util.DataHolder;
import net.nawaman.curry.util.DataHolder_Curry;
import net.nawaman.curry.util.MoreData;

/** Pattern port */
final public class Port implements DataHolder_Curry, Renderer {
	
	// Port -------------------------------------------------------------------------------------------------
	
	Port(Engine Engine, PortInfo PT, PortKind PKind) {
		this.PData = new PDInfo(Engine, PT, PKind);
		
		// The default one
		if(PKind == null) PKind = PKSingle.Instance;
		
		// Prepare the 
		this.IPIData = PKind.newInternalData(Engine, this, PT.getTypeRef());
	}
	
	PortData   PData;
	Expression Result;
	
	PortInternalData IPIData  = null;
	FinalValue       IPIFinal = null;
	
	/** Change from PDInfo to PDHost */
	void setToHostAndName(Pattern Host, AIDirect AInfo) {
		if(this.PData instanceof PDHost) return;
		this.PData = new PDHost(Host, AInfo, this.PData.getKind());
	}
	
	/** Returns the port data of this port */
	public PortData getPortData() {
		return this.PData;
	}
	
	// Merging -------------------------------------------------------------------------------------
    private void ensurePorts_notRendered(final Port pFrom) {
        final boolean aIsPortRendered = this.isReadable();
        final boolean aIsFromRendered = pFrom.isReadable();
        if (aIsPortRendered || aIsFromRendered)
            return;
        throw new PatternActionError(
                "Unable to collapse Map-Port's elements: one of them is already been rendered: " +
                (aIsPortRendered ? this.toString() : pFrom.toString())
            );
    }
    private void ensurePorts_canBeMerged(final Port pFrom) {
        final PortData aPortPData = this .PData;
        final PortData aFromPData = pFrom.PData;
        if (aPortPData != aFromPData) {
            if ((aPortPData != null)
             && !aPortPData.equals(aFromPData))
                throw new PatternActionError(String.format(
                        "Unable to collapse Map-Port's elements: " +
                        "the elements are not compatible:\n\t%s\n\t%s",
                        this.toString(), pFrom.toString()
                    ));
        }
        final PortKind aPortKind = this .PData.Kind;
        final PortKind aFormKind = pFrom.PData.Kind;
        
        if (!aPortKind.getName().equals(aFormKind.getName()))
            throw new PatternActionError(String.format(
                    "Unable to collapse Map-Port's elements: " +
                    "the elements' internal data are not compatible:\n\t%s\n\t%s",
                    this.toString(), pFrom.toString()
                ));
    }
    
    private void mergeBy_General(final Port pFrom) {
        this.ensurePorts_notRendered(pFrom);
        this.ensurePorts_canBeMerged(pFrom);
    }
    
    void mergeBy_More(final Port pFrom) {}
    
    final void mergeBy(final Port pFrom) {
        if (pFrom == null)
            return;
        this.mergeBy_General(pFrom);
        this.mergeBy_More   (pFrom);
        this.IPIData.mergeBy(pFrom.IPIData);
    }
	
	// Helping class -------------------------------------------------------------------------------
	
	/** SubRoutine used for Port  */
	abstract protected class PortMacro extends JavaExecutable.JavaMacro_Complex {
	    
        private static final long serialVersionUID = 6466419141252983085L;
        
        /** Construct a new macro */
		protected PortMacro(ExecSignature ES, Port pPort) {
			super(null, ES, null, null);
			this.Port = pPort;
		}
		Port Port;
	}

	// Rendering --------------------------------------------------------------------------------------------
	
	static private HashSet<Port> BeingRendered = new HashSet<Port>();
	
	/** Checks if this port has not been asked to render */
	public boolean isNotRendered() {
		return !this.isRendered() && !this.isBeingRendered();
	}
	
	/** Checks if this port has been rendered */
	public boolean isRendered() {
		return this.Result != null;
	}
	
	/** Checks if this port is currently being rendering */
	public boolean isBeingRendered() {
		return BeingRendered.contains(this);
	}
	
	/** Throw a port rendering recursive detected */
	final protected void throwPortRenderRecursiveDetected(Context $Context, int LineNumber) {
		StringBuilder SB = new StringBuilder();
		for(Port P : BeingRendered) {
			SB.append(P).append(":\n");
			
			PortInternalData PIData = P.IPIData;
			
			PAAssignment DInstance = PIData.DefaultInstance;
			if(DInstance != null)
				SB.append("     = ").append(DInstance.ARecord).append("\n");
			
			Vector<PAAssignment> Instances = PIData.Instances;
			if(Instances != null) {
				for(PAAssignment AAA : Instances) {
					SB.append("    := ").append(AAA.ARecord).append("\n");
				}
			}
		}
		
		throw new CurryError(
			String.format(
				"Recursive rendering is detected ('%s') <Port:%d>: \n%s",
				this, LineNumber, SB
			),
			$Context
		);
	}
	
	/** Render this port */
	final protected void render(Context $Context) {
		if(this.isRendered())
			return; 
		
		if(BeingRendered.contains(this))
			this.throwPortRenderRecursiveDetected($Context, 105);
		
		BeingRendered.add(this);
		
		
		// Pre rendered Actions ----------------------------------------------------------------------------------------
		
		// The actions that prefer to be done before the port is rendered ---------------
		// Do the pre-rendering actions
		Vector<PAPreRendering> PreRenderings = this.IPIData.PreRenderings;
		if(PreRenderings != null) {
			do {
				boolean IsDoSomething = false;
				for(int i = 0; i < PreRenderings.size(); i++) {
					PAPreRendering PAPR = PreRenderings.get(i);
					if(PAPR == null)
						continue;
						
					PAPR.doAction($Context, this);
					PreRenderings.set(i, null);
					IsDoSomething = true;
				}
				if(!IsDoSomething) break;
			} while(true);
		}
		
        final PortData PD = this.getPortData();
        final TypeRef  PT = PD.getTypeRef();
        final Engine   $E  = PD.getEngine();
        
        
        // The actions that waits for many port ---------------------------------------------------
        // Notify that this port is about to be rendered
        final EE_Pattern EEP = (EE_Pattern)$E.getExtension(EE_Pattern.Name);
        EEP.notifyRendering(this);
		
		
		
		// Perform the determination based on the given port ----------------------------
		// Render
		boolean aIsHardAssign = this.assignFinalValue($Context);
		Object  aFinalValue   = this.IPIFinal.Value;
		
		
		// Verify the value and marks as rendered --------------------------------------- 
		
		// Ensure that the result is in the right type
		if((aFinalValue != null) && !MType.CanTypeRefByAssignableBy($Context, $E, PT, aFinalValue)) {
			throw new CurryError(
				String.format("Invalid port-result type: `%s` for type '%s' <Port:61>", Result, PT),
				$Context
			);
		}
		
		final PortKind aPKind = this.PData.Kind;
		
		// Notify that the value is configured
		aPKind.notifyValueConfigured($Context, this, aIsHardAssign);
		aFinalValue = this.IPIFinal.Value;
		
		// Set the result - marked as rendered
		this.Result = Expression.toExpr(aFinalValue);
		// Notify that the value is accepted
		aPKind.notifyValueAccepted($Context, this, aIsHardAssign);
		
		// Done
		BeingRendered.remove(this);
		
		
		// Notify that the value is verified
		aPKind.notifyValueVerified($Context, this, aIsHardAssign);
		
		
		// Post rendered Actions ---------------------------------------------------------------------------------------
		
		// The actions that solely wait for this port ----------------------------------------------
		// Do the post-rendering actions
		Vector<PAPostRendering> PostRenderings = this.IPIData.PostRenderings;
		if(PostRenderings != null) {
			do {
				boolean IsDoSomething = false;
				boolean HasAssertion  = false;
				for(int i = 0; i < PostRenderings.size(); i++) {
					PAPostRendering PAPR = PostRenderings.get(i);
					if(!(PAPR instanceof PAAssertion))
						continue;
						
					PAPR.doAction($Context, this, aFinalValue);
					PostRenderings.set(i, null);
					IsDoSomething = true;
					HasAssertion  = true;
				}
				// Ensure all assertion is all done
				if(HasAssertion) continue;
				
				for(int i = 0; i < PostRenderings.size(); i++) {
					PAPostRendering PAPR = PostRenderings.get(i);
					if(PAPR == null)
						continue;
					
					// Break from for loop but continue the while loop.
					if(PAPR instanceof PAAssertion) {
						HasAssertion = true;
						break;
					}
						
					PAPR.doAction($Context, this, aFinalValue);
					PostRenderings.set(i, null);
					IsDoSomething = true;
				}
				if(!IsDoSomething && !HasAssertion) break;
			} while(true);
		}
		
		
		// The actions that waits for many port ---------------------------------------------------
		// Notify that this port is rendered
		EEP.notifyRendered(this);
		
		
		// Clear the data to save memory
		this.IPIData = null;
	}
	
	/** Checks if this port has no value */
	private boolean hasNoValue() {
		if(this.isRendered()) return false;
		return ((this.IPIData == null) || ((this.IPIData.Instances == null) && (this.IPIData.DefaultInstance == null)));
	}
	
	/** Render the port value */
	private boolean assignFinalValue(Context $Context) {
		Engine $Engine = this.PData.getEngine();
		
		boolean aIsHardAssigned = false;
		
		// Nothing to render
		if(this.IPIData == null) {
			Expression   DefaultValue  = Expression.toExpr(this.PData.getTypeRef().getDefaultValue(this.PData.getEngine()));
			PAAssignment DefaultAction = new PAAssignment.Simple(null, DefaultValue);
			
			// Determine the value
			this.IPIFinal = DefaultAction.doAction($Context, this);
			
		} else {
    
    		// Determine the final action
    		Vector<PAAssignment> Instances = this.IPIData.Instances;
    		if((Instances == null) || (Instances.size() == 0))  {
    			// Use the default value
    			PAAssignment DefaultAction = this.IPIData.DefaultInstance;
    			
    			if(DefaultAction == null)
    				 DefaultAction = new PAAssignment.Simple(null, null);
    			else if (!(DefaultAction instanceof PAAssignment_New))
    			    aIsHardAssigned = true;
    			
    			// Determine the value
    			this.IPIFinal = DefaultAction.doAction($Context, this);
    			
    		} else {
    			// Do the rest of the assignment actions
    			boolean HasNew     = false;
    			boolean HasProblem = false;
    			
    			HashMap<TypeRef, Vector<PAAssignment_New>> NewTRefs = null;
    			HashMap<Object,  Vector<FinalValue>>       Finals   = new HashMap<Object, Vector<FinalValue>>(); 
    			for(int i = 0; i < Instances.size(); i++) {
    				PAAssignment PAA = Instances.get(i);
    				if(PAA == null)
    					continue;
    				
    				// Assignment New
    				if(HasNew = (PAA instanceof PAAssignment_New)) {
    					if(NewTRefs == null) NewTRefs = new HashMap<TypeRef, Vector<PAAssignment_New>>();
    					
    					PAAssignment_New New     = (PAAssignment_New)PAA;
    					TypeRef          NewTRef = New.TRef;
    					
    					Vector<PAAssignment_New> PAANews = NewTRefs.get(NewTRef);
    					if(PAANews == null) NewTRefs.put(NewTRef, (PAANews = new Vector<PAAssignment_New>()));
    					
    					// Add the new TypeRef to the collection 
    					PAANews.add(New);
    					
    					continue;
    				}
    
    				// Assignment Value
    				FinalValue Final  = PAA.doAction($Context, this);
    				Object     FValue = Final.Value;
    				
    				Vector<FinalValue> IPFValues = Finals.get(FValue);
    				if(IPFValues == null) Finals.put(FValue, (IPFValues = new Vector<FinalValue>()));
    				
    				// Add the final-value object to the collection 
    				IPFValues.add(Final);
    			}
    			
    			if(Finals.size() == 0) {
    				// No assigned, try the new assignments or the default value.
    				
    				boolean IsToUseData = false;
    				Object  Data    = null;
    				
    				if(HasNew) {
    					// New assignments ----------------------------------------
    					TypeRef TRef = null;
    					// Find the smallest type
    					for(TypeRef PAATRef : NewTRefs.keySet()) {
    						if(PAATRef == null)
    							continue;
    						
    						if(TRef == null) {	// First one
    							TRef   = PAATRef;
    							HasNew = true;
    							
    						} else {
    							// Ignore the interface
    							if(TKInterface.isTypeRefInterface($Engine, PAATRef))
    								continue;
    							
    							// Ignore the abstract
    							if(PAATRef.isAbstract($Engine))
    								continue;
    							
    							// PAATRef is bigger, keep going
    							if(MType.CanTypeRefByAssignableByInstanceOf($Context, $Engine, PAATRef, TRef))
    								continue;
    							
    							// If PAATRef is smaller, replace TRef with PAATRef.
    							if(MType.CanTypeRefByAssignableByInstanceOf($Context, $Engine, TRef, PAATRef))
    								TRef = PAATRef;
    							
    							else{
    								// There is a problem
    								HasProblem = true;
    								TRef = null;
    								break;
    							}
    						}
    					}
    	
    					// Use the instance of the type
    					if(TRef != null) {
    						Data        = $Engine.execute(Inst_NewInstance.Name, $Engine.getExecutableManager().newType(TRef));
    						IsToUseData = true;
    					}
    				}
    				
    				if(!HasProblem && !IsToUseData) {
    					// Default value ------------------------------------------
    					
    					if(this.IPIData.DefaultInstance != null) {
    						// Use the default value
    						PAAssignment FinalAction = this.IPIData.DefaultInstance;
    						// Determine the value
    						this.IPIFinal = FinalAction.doAction($Context, this);
    							
    					} else {
    						// Use the default value of the this type
    						Data        = this.PData.getTypeRef().getDefaultValue($Engine);
    						IsToUseData = true;
    					}
    				}
    				
    				if(IsToUseData) {
    					// Prepare the default value list
    					ActionRecord ARecord = ExternalContext.newCallerActionRecord($Context);
    					if(ARecord == null) ARecord = ExternalContext.newActionRecord($Context);
    					// Determine the final
    					Executable            Exec    = Expression.toExpr(Data);
    					PAAssignment PAction = new PAAssignment.Simple(null, Exec);
    					PAction.ARecord = ARecord;
    					// Determine the value
    					this.IPIFinal = PAction.doAction($Context, this);
    				}
    				
    			} else if(Finals.size() == 1) {
    				Vector<FinalValue> IPFValues = Finals.get(Finals.keySet().iterator().next());
    				FinalValue         FValue    = IPFValues.get(0); 
    				this.IPIFinal = FValue;
    				aIsHardAssigned = true;
    				
    			} else {
    				HasProblem = true;
    			}
    			
    			// A value is assigned, now check if all assignment new agrees
    			if(this.IPIFinal != null) {
    				Object Value = this.IPIFinal.getValue();
    
    				// There is one final value assigned so we see if all the new type are compatible.
    				for(int i = 0; i < Instances.size(); i++) {
    					PAAssignment PAA = Instances.get(i);
    					if(!(PAA instanceof PAAssignment_New))
    						continue;
    					
    					TypeRef DesiredTRef = ((PAAssignment_New)PAA).TRef;
    					if(DesiredTRef == null) continue;
    					
    					if(MType.CanTypeRefByAssignableBy($Context, $Engine, DesiredTRef, Value))
    						continue;
    					
    					// Not compatible, mark it so error will be report
    					HasProblem = true; 
    				}
    			}
    
    			// There is a problem, show error
    			if(HasProblem) {
    				StringBuilder SB = new StringBuilder();
    
    				for(Object Value : Finals.keySet()) {
    					Vector<FinalValue> IPFValues = Finals.get(Value);
    					if((IPFValues == null) || (IPFValues.size() == 0)) continue;
    					
    					SB.append("\n");
    					SB.append("`").append(Value).append("`: ");
    					for(FinalValue FValue : IPFValues) {
    						SB.append("\n---> ");
    						ActionRecord ARecord = FValue.FinalAction.ARecord;
    						if(ARecord.getActor() == this.PData.getPattern())
    							 SB.append(ARecord.getLocationSnapshot());
    						else SB.append(ARecord);
    						
    					}
    				}
    
    				if(NewTRefs != null) {
    					for(TypeRef TRef : NewTRefs.keySet()) {
    						Vector<PAAssignment_New> PAANews = NewTRefs.get(TRef);
    						if((PAANews == null) || (PAANews.size() == 0)) continue;
    						
    						SB.append("\n");
    						SB.append("`new() of ").append(TRef).append("`: ");
    						for(PAAssignment_New NewAction : PAANews) {
    							SB.append("\n---> ");
    							ActionRecord ARecord = NewAction.ARecord;
    							if(ARecord.getActor() == this.PData.getPattern())
    								 SB.append(ARecord.getLocationSnapshot());
    							else SB.append(ARecord);
    							
    						}
    					}
    				}
    				SB.append("\n");
    				
    				throw new PatternActionError(
    					String.format(
    						"\nPort multiple values error: %s <Port:208>:%s",
    						this, SB
    					),
    					$Context
    				);
    			}
    			
    		}
		}
		
		// Ensure the final result is a valid object
		if(this.IPIFinal == null) {
			throw new PatternActionError(
				String.format(
					"\nInvalid port final result object: '%s' for %s <Port:225>",
					this.IPIFinal, this
				),
				$Context
			);
		}
        
        final Object   aFinalValue = this.IPIFinal.Value;
        final PortKind aPKind      = this.PData.Kind;
        final TypeRef  aTRef       = this.PData.getTypeRef();
		
		// Ensure the type of the final value to be compatible
		if(!MType.CanTypeRefByAssignableBy($Context, $Engine, aTRef, aFinalValue)) {
			throw new PatternActionError(
				String.format(
					"\nInvalid port final result value: '%s' for %s <Port:235>:\n    %s",
					this.IPIFinal.Value, this, this.IPIFinal.FinalAction.ARecord
				),
				$Context
			);
		}
		
		// The value is assigned
		aPKind.notifyValueAssigned($Context, this, aIsHardAssigned);
		
		// Do the configuration actions
		Vector<PAPostAssignment> Configurations;
		if((this.IPIData instanceof PIDConfigurable) && ((Configurations = ((PIDConfigurable)this.IPIData).Configurations) != null)) {
			int LBound = Integer.MIN_VALUE;
			int UBound = 0;
			Vector<PAPostAssignment> HighestPriorityActions = null;
			while((LBound != UBound) || (UBound != Integer.MAX_VALUE)) {
				int NextUB = Integer.MAX_VALUE;
				for(int i = 0; i < Configurations.size(); i++) {
					PAPostAssignment PAPA = Configurations.get(i);
					if(PAPA == null) continue;
						
					int Priority = PAPA.getPriority();
					
					// See if this action should be performed now
					if((Priority > LBound) && (Priority <= UBound)) {
						PAPA.doAction($Context, this, aFinalValue);

						if(Priority == Integer.MAX_VALUE) {
							if(HighestPriorityActions == null)
								HighestPriorityActions = new Vector<PAPostAssignment>();
							
							HighestPriorityActions.add(PAPA);
							// Will not do the maximum-prioritized action more than once
							if(HighestPriorityActions.size() > 1)
								continue;
						}
					}
					
					// Finding the next priority
					if((Priority > UBound) && (Priority < NextUB))
						NextUB = Priority;
				}
				LBound = UBound;
				UBound = NextUB;
			}
			
			if((HighestPriorityActions != null) && (HighestPriorityActions.size() > 1)) {
				// There are more than one highest priorities actions - report error
				
				StringBuilder SB = new StringBuilder();
				for(PAPostAssignment PAPA : HighestPriorityActions) {
					SB.append("\n    ");
					SB.append(PAPA.getClass().getSimpleName()).append(": ");
					SB.append(PAPA.ARecord);
				}
				
				throw new PatternActionError(
					String.format(
						"\nMultiple highest-priority post-assignment actions detected: '%s' <Port:380>:%s",
						this, SB
					),
					$Context
				);
			}
		}
		
		return aIsHardAssigned;
	}

	// Active methods ---------------------------------------------------------------------------------------
	
	// SetData ------------------------------------------------------------------------------------

	static final ExecSignature              SetDataMacro_Port_Signature = ExecSignature.newEmptySignature("SetDataPort", null, null);
	static final SetDataMacro_AfterRendered SetDataMacro_AfterRendered  = new SetDataMacro_AfterRendered();
	
	static final SetDataMacro_NotWritable SetDataMacro_NotWritable = new SetDataMacro_NotWritable();
	
	/** A macro to determine the value of port */
	static final class SetDataMacro_NotWritable extends JavaExecutable.JavaMacro_Complex {
		
        private static final long serialVersionUID = -2625450835850058836L;
        
        static final ExecSignature ES = ExecSignature.newEmptySignature("SetDataPort_NotWritable", null, null);
		/** Construct a new native macro */
		SetDataMacro_NotWritable() { super(null, ES, null, null); }
		/** Executing this */ @Override
		protected Object run(Context pContext, Object[] pParams) {
			throw new CurryError("The port is not writable.", pContext);
		}
	}
	
	/**{@inheritDoc}*/ @Override
	public final Object setData(Object pValue) {
		Engine     $Engine = this.getPortData().getEngine();
		Executable $Exec   = this.getExpr_setData($Engine, pValue);
		return this.executeExecutable($Engine, $Exec);
	}
	
	static final class SetDataMacro_AfterRendered extends JavaExecutable.JavaMacro_Complex {
		
        private static final long serialVersionUID = -9178583078500829962L;
        
        SetDataMacro_AfterRendered() { super(null, SetDataMacro_Port_Signature, null, null); }
		protected @Override Object run(Context pContext, Object[] pParams) {
			throw new CurryError("The Port is already rendered.", pContext);
		}
	}
	
	/**{@inheritDoc}*/ @Override
	public Executable getExpr_setData(Engine pEngine, final Object pData) {
		if(!this.PData.getPortInfo().isWritable())
			return SetDataMacro_NotWritable;
		
		// If already rendered, throw the exception 
		if(this.isRendered())
			return SetDataMacro_AfterRendered;
		
		// If already assigned, just return.
		if(this.IPIData.isDefaultValueAssigned())
			return Expression.TRUE;
		
		// Prepare the SetValueMacro
		return pEngine.getExecutableManager().newExpr(
				Inst_Exec.Name,
				new PortMacro(SetDataMacro_Port_Signature, this) {
                    private static final long serialVersionUID = -8114229438756330123L;
                    protected @Override Object run(Context pContext, Object[] pParams) {
						return setData(pContext, pData);
					}
				}
			);
	}
	
	/** Set data */
	Object setData(Context pContext, Object pData) {
		if(!this.PData.getPortInfo().isWritable())
			return SetDataMacro_NotWritable;
		
		// Just in case
		if(this.isRendered())
			throw new CurryError("The Port is already rendered.", pContext);
		
		PortData ThePData = this.PData;
		Engine   $Engine  = ThePData.getEngine();
		TypeRef  PTRef    = ThePData.getTypeRef();
			
		// Check the value type
		if((pData != null) &&
		   !(pData instanceof PAAssignment.Simple) &&
		   !MType.CanTypeRefByAssignableBy(pContext, $Engine, PTRef, pData)) {
			throw new CurryError(
				String.format("Invalid port default value: `%s` for type '%s' <Port:331>", Result, PTRef),
				pContext
			);
		}
		
		// Prepare the default value list
		ActionRecord ARecord = ExternalContext.newCallerActionRecord(pContext);
		if(ARecord == null) ARecord = ExternalContext.newActionRecord(pContext);
		
		PAAssignment PAction;
		if(pData instanceof PAAssignment.Simple)
			 PAction = (PAAssignment.Simple)pData;
		else PAction = new PAAssignment.Simple(null, Expression.toExpr(pData));
		
		PAction.ARecord = ARecord;
		
		this.IPIData.setDefaultValueAction(PAction);
		
		return true;
	}
	
	// GetData ------------------------------------------------------------------------------------

	static final ExecSignature GetDataES = ExecSignature.newEmptySignature("GetDataPort", null, null);
		
	/** Returns the value that this data holder holds. */
	final public Object getData() {
		if(this.Result == null) {
			Engine     $Engine = this.getPortData().getEngine();
			Executable $Exec   = this.getExpr_getData($Engine);
			this.executeExecutable($Engine, $Exec);
		}
		return this.Result.getData();
	}
	
	/**{@inheritDoc}*/ @Override
	final public Executable getExpr_getData(Engine pEngine) {
		if(this.Result == null) {
			if(BeingRendered.contains(this))
				this.throwPortRenderRecursiveDetected(null, 427);
						
			// Returns the Macro for the renderer
			return new PortMacro(GetDataES, this) {
                        private static final long serialVersionUID = 922726783972271453L;
                        
                        /** Executing this */ @Override
						protected Object run(Context $Context, Object[] pParams) {
							// Ensure the port is rendered
							this.Port.render($Context);
							// Returns the value
							return this.Port.Result.getData();
						}
					};
		}
		return this.Result;
	}
	
	// Config -------------------------------------------------------------------------------------

	static final ExecSignature ConfigPortSignature = ExecSignature.newSignature(
			"ConfigPort",
			new TypeRef[] { TKJava.TString.getTypeRef(), TKJava.TAny.getTypeRef() },
			new String[]  { "ConfigName",                "ConfigParams"           },
			true,
			TKJava.TAny.getTypeRef(),
			null, null);
	
	static final ConfigMacro_AfterRendered   ConfigMacro_AfterRendered = new ConfigMacro_AfterRendered();
	static final HashMap<Engine, Expression> ConfigExprs_NotWritable   = new HashMap<Engine, Expression>();
	
	static final ConfigMacro_NotWritable ConfigMacro_NotWritable = new ConfigMacro_NotWritable();

	static final class ConfigMacro_AfterRendered extends JavaExecutable.JavaMacro_Complex {
		
        private static final long serialVersionUID = 1301782108981814898L;
        
        ConfigMacro_AfterRendered() { super(null, ConfigPortSignature, null, null); }
		protected @Override Object run(Context pContext, Object[] pParams) {
			throw new CurryError("The Port is already rendered.", pContext);
		}
	}
	
	/** A macro to determine the value of port */
	static final class ConfigMacro_NotWritable extends JavaExecutable.JavaMacro_Complex {
		
        private static final long serialVersionUID = 4773000397744474319L;
        
        static final ExecSignature ES = ExecSignature.newEmptySignature("ConfigPort_NotWritable", null, null);
		/** Construct a new native macro */
		ConfigMacro_NotWritable() { super(null, ES, null, null); }
		/** Executing this */ @Override
		protected Object run(Context pContext, Object[] pParams) {
			throw new CurryError("The port is not writable.", pContext);
		}
	}
	
	/** Returns the expression for an error of not writable port as an expression  */
	static Expression GetConfigExpr_NotWritable(Engine $Engine) {
		Expression Expr = ConfigExprs_NotWritable.get($Engine);
		if(Expr != null) return Expr;
		
		Expr = $Engine.getExecutableManager().newExpr(Inst_Call.Name, ConfigMacro_NotWritable);
		ConfigExprs_NotWritable.put($Engine, Expr);
		return Expr;
	}
	
	/**{@inheritDoc}*/ @Override
	final public Object config(String pName, Object[] pParams) {

		if(UPattern.CONFIG_NAME_IS_RENDERED.equals(pName))
			return this.isRendered();
		
		if(UPattern.CONFIG_NAME_GETDATA.equals(pName)) {
			if(this.isRendered()) return this.Result.getData();
			return Expression.toExpr(this.getData());
		}
		
		if(UPattern.CONFIG_NAME_GETFINALVALUE.equals(pName))
			return this.IPIFinal;
		
		if(((pParams == null) || (pParams.length == 0))){
			if(UPattern.OPERNAME_RENDER.equals(pName)) {
				this.getData();
				return null;
				
			} else if("getData".equals(pName)) {
				return this.getData();
				
			}
		}
		
		Engine     $Engine = this.getPortData().getEngine();
		Executable $Exec   = this.getExpr_config($Engine, pName, pParams);
		return this.executeExecutable($Engine, $Exec);
	}

	/**{@inheritDoc}*/ @Override
	public Executable getExpr_config(Engine pEngine, final String pName, final Object[] pParams) {
		if(UPattern.CONFIG_NAME_IS_RENDERED.equals(pName))
			return Expression.toExpr(this.isRendered());
		
		if(UPattern.CONFIG_NAME_GETDATA.equals(pName)) {
			if(this.isRendered()) return this.Result;
			return pEngine.getExecutableManager().newExpr(Inst_GetDHValue.Name, this);
		}
		
		if(UPattern.CONFIG_NAME_GETFINALVALUE.equals(pName))
			return Expression.toExpr(this.IPIFinal);

		if(UPattern.CONFIG_NAME_HASNOVALUE.equals(pName))
			return Expression.toExpr(this.hasNoValue());
		
		if(UPattern.CONFIG_NAME_PATTERN_ACTION.equals(pName)) {
			Object Data = pParams[0];
			// Unwrapp it
			if(Data instanceof UnWritableAssignWrapper)
				Data = pParams[0] = ((UnWritableAssignWrapper)Data).Data;
			
			// If not a writable
			else if(!this.PData.getPortInfo().isWritable())
				return GetConfigExpr_NotWritable(pEngine);
			
			// If already rendered, throw the exception 
			if(!(Data instanceof PAPostRendering) && (Data instanceof Action_PostRender) && this.isRendered())
				return ConfigMacro_AfterRendered;
		}
		
		// Prepare the ConfigMacro
		return pEngine.getExecutableManager().newExpr(
				Inst_Exec.Name,
				new PortMacro(ConfigPortSignature, this) {
					
                    private static final long serialVersionUID = 5370653352531493894L;
                    
                    protected @Override Object run(Context pContext, Object[] Params) {
						return config(pContext, pName, pParams);
					}
				},
				pName,
				pParams
			);
	}
	
	/** Config - used for complex assigna, verification, ... */
	Object config(Context $Context, String pName, Object[] pParams) {
		if(pName == null)
			return null;
		
		if(((pParams == null) || (pParams.length == 0))){
			if(UPattern.OPERNAME_RENDER.equals(pName)) {
				this.executeExecutable($Context, this.getExpr_getData(this.PData.getEngine()));
				return null;
				
			} else if(UPattern.CONFIG_NAME_GETDATA.equals(pName)) {
				return this.executeExecutable($Context, this.getExpr_getData(this.PData.getEngine()));
			}
			
			if(UPattern.CONFIG_NAME_IS_RENDERED.equals(pName))
				return Expression.toExpr(this.isRendered());
			
			if(UPattern.CONFIG_NAME_GETFINALVALUE.equals(pName))
				return Expression.toExpr(this.IPIFinal);

			if(UPattern.CONFIG_NAME_HASNOVALUE.equals(pName))
				return Expression.toExpr(this.hasNoValue());
		}
		
		if(UPattern.CONFIG_NAME_PATTERN_ACTION.equals(pName)) {
			Object Data = pParams[0];
	
			// An independent post-rendering actions
			if((Data instanceof Action_PostRender) || (Data instanceof Action_PreRender)) {
				EE_Pattern EEP = (EE_Pattern)this.PData.getEngine().getExtension(EE_Pattern.Name);
				EEP.waitFor((Action_PostRender)Data, this);
				return null;
			}
			
			ActionRecord ARecord = null;
			if($Context != null) {
				ARecord = ExternalContext.newCallerActionRecord($Context);
				if(ARecord == null) ARecord = ExternalContext.newActionRecord($Context);
			}
			
			// Not a port action - default value
			if(!(Data instanceof PortAction)) {
				Data = Expression.toExpr(Data);
				Data = new PAAssignment.Simple(null, (Expression)Data);
			}
			
			// Assign the action record
			if(((PortAction)Data).ARecord ==  null)
				((PortAction)Data).ARecord = ARecord;
			
			// Already render
			if(this.isRendered()) {
				if(Data instanceof PAPreRendering) {
					// Do the action
					((PAPreRendering)Data).doAction($Context, this);
					return null;
				}
				
				if(Data instanceof PAPostRendering) {
					// Do the action
					((PAPostRendering)Data).doAction($Context, this, this.IPIFinal.Value);
					return null;
				}
				
				throw new CurryError(
						String.format(
							"The Port is already rendered: %s <Port:701>:\n    %s",
							this,
							this.IPIFinal.FinalAction.ARecord
						),
						$Context
					);
			}
			
			
			// Add to the list
			this.IPIData.addAction((PortAction)Data);
		}
	
		final PortKind aPKind  = this.getPortData().getKind();
		final Object   aResult = aPKind.config($Context, this, pName, (Object[])pParams);
		return aResult;
	}
	
	// GetMoreInfo --------------------------------------------------------------------------------

	/** GetMoreInfo executable signature */
	static protected final ExecSignature GetMoreInfoES = ExecSignature.newEmptySignature("GetMoreInfoPort", null, null);

	/**{@inheritDoc}*/ @Override
	final public Object getMoreInfo(String pName) {
		Engine     $Engine = this.getPortData().getEngine();
		Executable $Exec   = this.getExpr_getMoreInfo($Engine, pName);
		return this.executeExecutable($Engine, $Exec);
	}
	
	/** Returns more information about the dataholder. */
	public Executable getExpr_getMoreInfo(Engine pEngine, final String pName) {
        final Object aResult = this.getPortData().getKind().getMoreInfo(this, pName);
        if (aResult == null)
            return Expression.NULL;
        return Expression.newDataNoCare(aResult);
	}
	
	// DataHolder ---------------------------------------------------------------------------------

	/**{@inheritDoc}*/ @Override
	final public Type getType() {
		Engine     $Engine = this.getPortData().getEngine();
		Executable $Exec   = this.getExpr_getType($Engine);
		return (Type)this.executeExecutable($Engine, $Exec);
	}
	
	/**{@inheritDoc}*/ @Override
	final public boolean isReadable() {
		return true;
	}
	/**{@inheritDoc}*/ @Override
	final public boolean isWritable() {
		return this.getPortData().getPortInfo().isWritable() && this.isNotRendered();
	}
	
	/**{@inheritDoc}*/ @Override
	final public boolean isNoTypeCheck() {
		return false;
	}
	
	/**{@inheritDoc}*/ @Override
	final public DataHolder clone() {
		throw new CurryError("A Port is not clonable.");
	}

	// DataHolder_Curry ---------------------------------------------------------------------------

	/**{@inheritDoc}*/ @Override
	final public boolean isAlsoNormalDataHolder() {
		return false;
	}
	
	/**{@inheritDoc}*/ @Override
	final public Executable getExpr_getType(Engine pEngine) {
		return this.PData.getTypeAsExpression();
	}

	/**{@inheritDoc}*/ @Override
	final public Executable getExpr_isReadable(Engine pEngine) {
		return Expression.TRUE;
	}
	/**{@inheritDoc}*/ @Override
	final public Executable getExpr_isWritable(Engine pEngine) {
		return this.isWritable() ? Expression.TRUE : Expression.FALSE; 
	}
	
	/**{@inheritDoc}*/ @Override
	final public Executable getExpr_isNoTypeCheck(Engine pEngine) {
		return Expression.FALSE;
	}
	
	/**{@inheritDoc}*/ @Override
	final public Executable getExpr_clone(Engine pEngine) {
		this.clone();
		return null;
	}
	
	// Objectable ------------------------------------------------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	final public int hashCode() {
		return super.hashCode();
	}
	
	/**{@inheritDoc}*/ @Override
	public String toString() {
		PortData      PD = this.PData;
		StringBuilder SB = new StringBuilder(); 
		
		SB.append("Port<");
		
		// Port name 
		if((PD.getPortName() != null) && (PD.getPortName().length() != 0))
			SB.append(PD.getPattern().getTheType().getTypeRef()).append(".").append(PD.getPortName());
		else {
			MoreData MD = this.PData.getPortInfo().getMoreInfo();
			Object   Obj;
			// See if the port info have the Variable name entry
			if((MD != null) && ((Obj = MD.getData("VarName")) instanceof String)) 
				SB.append(Obj);
		}
		
		// Type
		SB.append(":").append(PD.getTypeRef());
		
		// Value if rendered
		if(this.isRendered())
			SB.append("=").append(this.getData());
		
		SB.append(">");
		
		return SB.toString();
	}
	
	// Internal services -----------------------------------------------------------------------------------------------
	
	/** Execute an executable */
    final protected Object executeExecutable(Engine $Engine, Executable $Exec, Object ... Params) {
		if($Exec instanceof Expression) return $Engine.execute((Expression)$Exec);
		else if($Exec.isFragment())     return $Engine.getExecutableManager().runFragment(   $Exec.asFragment());
		else if($Exec.isMacro())        return $Engine.getExecutableManager().execMacro(     $Exec.asMacro(),      (Object[])Params);
		else if($Exec.isSubRoutine())   return $Engine.getExecutableManager().callSubRoutine($Exec.asSubRoutine(), (Object[])Params);
		else {
			throw new CurryError(
				String.format("Invalid executable-type error: '%s' <Port:271>", this)
			);
		}
	}
	
	/** Execute an executable */
    final protected Object executeExecutable(Context $Context, Executable $Exec, Object ... Params) {
		if($Exec instanceof Expression) return ExternalContext.execute(       $Context, (Expression)$Exec);
		else if($Exec.isFragment())     return ExternalContext.runFragment(   $Context, $Exec.asFragment());
		else if($Exec.isMacro())        return ExternalContext.execMacro(     $Context, $Exec.asMacro(),      (Object[])Params);
		else if($Exec.isSubRoutine())   return ExternalContext.callSubRoutine($Context, $Exec.asSubRoutine(), (Object[])Params);
		else {
			throw new CurryError(
				String.format("Invalid executable-type error: '%s' <Port:271>", this),
				$Context
			);
		}
	}
	
	// Utilities class -------------------------------------------------------------------------------------------------
	
	static final class UnWritableAssignWrapper {
		final Object Data;
		UnWritableAssignWrapper(Object pData) {
			this.Data = pData;
		}
	} 
	
}
