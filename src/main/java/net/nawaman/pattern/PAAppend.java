package net.nawaman.pattern;

import net.nawaman.curry.*;
import net.nawaman.curry.Instructions_StackOwner.Inst_Invoke_ByParams;
import net.nawaman.util.*;

/** Action to append object to the list */
public abstract class PAAppend extends PAPostAssignment {
	
	static private final long serialVersionUID = 21554555556978655L;
	
	/** Constructs a Port action */
	protected PAAppend(Executable pCondition) {
		super(pCondition);
	}
	
	/**{@inheritDoc}*/ @Override
	protected void doAction(Context $Context, Port Port, Object PortValue) {
		if(!(Port.PData.getKind() instanceof PKCollection)) {
            ActionRecord     AR = this.ARecord;
            LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
			throw new PatternActionError(
				String.format(
					"\nIn compatible final value: only collection port can be append: %s <PAAppend:28>\n    %s",
					Port, LS
				)
			);
		}
		
		PKCollection  PKind  = (PKCollection)Port.PData.getKind();
		PFVCollection FVList = (PFVCollection)Port.IPIFinal;
		Object        FValue = FVList.getValue();
        
        if(FValue instanceof DObjectStandalone)
            FValue = ((DObjectStandalone)FValue).getAsDObject();
		
		Engine      $Engine = ExternalContext.getEngine($Context);
		MExecutable $ME     = $Engine.getExecutableManager();
		DObject     DValue  = (DObject)FValue;
		
		if(DValue == null) {
		    if (FVList.getActionRecord() == null) {
		        FValue = PKind.tryNonNullDefaultFinalValue(
		                $Context,
		                FVList,
		                Port.PData.getTypeRef(),
		                Port.IPIData);
		        
		        if(FValue instanceof DObjectStandalone)
		            FValue = ((DObjectStandalone)FValue).getAsDObject();
		        
		        DValue = (DObject)FValue;
		        
		    } else {
    			throw new PatternActionError(
    				String.format(
    					"Fail to append: The final value is null: %s <PAAppend:37>:\n    %s\n" +
    					"The null value is given at:\n    %s",
    					Port, this.ARecord, Port.IPIFinal.FinalAction.ARecord
    				),
    				$Context
    			);
		    }
		}
		
		Object DNative = DValue.getAsNative();
		if(((CanBeImmutable)DNative).isImmutable()) {
			throw new PatternActionError(
				String.format("The final value is immutable: %s <PAAppendable:37>:\n    %s", Port, this.ARecord),
				$Context
			);
		}
		
		Object Appended = this.getAppended($Context, Port);
		
		// Add the value
		ExternalContext.execute($Context, $ME.newExpr(Inst_Invoke_ByParams.Name, DValue, "add", Appended));
		// Add the action
		FVList.FinalValues.add(new FinalValue(Appended, this));
	}
	
	/** Performs the action */
	abstract protected Object getAppended(Context $Context, Port Port);
	
	// SubClasses ------------------------------------------------------------------------------------------------------
	
	/** Simple implementation of a Port Assignment Action */
	static public class Simple extends PAAppend {
		
		static private final long serialVersionUID = 52157852685565556L;

		/** Constructs a Port action */
		public Simple(Executable pCondition, Executable pExec) {
			super(pCondition);
			this.Exec = pExec;
		}
		
		Executable Exec;
		
		/**{@inheritDoc}*/ @Override
		protected Object getAppended(Context $Context, Port Port) {
			// Get the value
			Object Value = this.executeExecutable($Context, this.Exec, Port);
			return Value;
		}
	}
}
