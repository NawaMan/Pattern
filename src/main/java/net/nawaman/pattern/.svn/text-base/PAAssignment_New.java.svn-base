package net.nawaman.pattern;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.Context;
import net.nawaman.curry.Engine;
import net.nawaman.curry.Executable;
import net.nawaman.curry.LocationSnapshot;
import net.nawaman.curry.MType;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.Instructions_Core.Inst_NewInstance;

public class PAAssignment_New extends PAAssignment {
	
	static private final long serialVersionUID = 23485256545574555L;
	
	/** Constructs a Port action */
	public PAAssignment_New(Executable pCondition, TypeRef pTRef) {
		super(pCondition, false);
		this.TRef = pTRef;
	}
	
	TypeRef TRef;
	
	/**{@inheritDoc}*/ @Override
	protected FinalValue doAction(Context $Context, Port Port) {
		Engine $Engine = Port.PData.getEngine();
		
		// Get the value
		Object    Value = null;
		Exception Cause = null; 
		try { Value = $Engine.execute(Inst_NewInstance.Name, $Engine.getExecutableManager().newType(this.TRef)); }
		catch (Exception e) { Cause = e; }
		
		if((Value == null) || (Cause != null)) {
	        ActionRecord     AR = this.ARecord;
	        LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
			throw new PatternActionError(
					String.format(
						"\nInvalid executable error: %s<PAAssignment_New:32>:\n    %s",
						Port.toString() + " ", LS
					),
					Cause
				);
		}
		
		if(!MType.CanTypeRefByAssignableBy(null, $Engine, Port.PData.getTypeRef(), Value)) {
            ActionRecord     AR = this.ARecord;
            LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
			throw new PatternActionError(
				String.format(
					"\nInvalid assignment type error: `%s` for '%s' %s<Action:44>:\n    %s",
					Value, Port.PData.getTypeRef(),
					"of " + Port.toString() + " ", LS
				)
			);
		}
		
		// Returns the the final-value object
		return Port.PData.getKind().newFinalValue(
		        $Engine,
		        Value,
		        this,
		        Port);
	}
}
