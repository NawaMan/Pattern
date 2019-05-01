package net.nawaman.pattern;

import java.io.Serializable;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.Context;
import net.nawaman.curry.Executable;
import net.nawaman.curry.Expression;
import net.nawaman.curry.ExternalContext;
import net.nawaman.curry.LocationSnapshot;

/** Action of a Port */
abstract public class Action implements Serializable {
	
	static private final long serialVersionUID = 56846516513515165L;
	
	/** Constructs a Port action */
	protected Action(Executable pCondition) {
		this.Condition = pCondition;
	}
	
	transient protected ActionRecord ARecord;
	/** Returns the action record of this action */
	final public ActionRecord getActionRecord() {
		return this.ARecord;
	}
	
	protected Executable Condition;
	/** Returns the condition needed to be satisfy before executing the action */
	final public Executable getCondition() {
		return this.Condition;
	}

	/** Checks if this action should be run */	
	final protected boolean isToRun(Context $Context, Port Port) {
		if(this.Condition == null) return true;
		
		// Data Expression
		if((this.Condition instanceof Expression) &&
		   ((Expression)this.Condition).isData() &&
		   Boolean.TRUE.equals(((Expression)this.Condition).getData()))
			return true;
		
		Object Return = this.executeExecutable($Context, this.Condition, Port);
		if(Boolean.TRUE.equals(Return))
		    return (Boolean)Return;
		
        ActionRecord     AR = this.ARecord;
        LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
		throw new PatternActionError(
				String.format(
					"\nExpect boolean value for action condition:%s<Action:44>:\n    %s",
					(Port != null) ? Port.toString() + " " : "", LS
				)
		);
	}
	
	// Services --------------------------------------------------------------------------------------------------------
	
	/** Execute an action */
	final protected Object executeExecutable(Context $Context, Executable $Exec, Port Port) {
		if($Exec == null) return null;
		
		
		if($Exec instanceof Expression) return ExternalContext.execute(       $Context, (Expression)$Exec);
		else if($Exec.isFragment())     return ExternalContext.runFragment(   $Context, $Exec.asFragment());
		else if($Exec.isMacro())        return ExternalContext.execMacro(     $Context, $Exec.asMacro());
		else if($Exec.isSubRoutine())   return ExternalContext.callSubRoutine($Context, $Exec.asSubRoutine());
		else {
	        ActionRecord     AR = this.ARecord;
	        LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
			throw new PatternActionError(
				String.format(
					"\nInvalid executable error: %s<Action:64>:\n    %s",
					(Port != null) ? Port.toString() + " " : "", LS
				)
			);
		}
	}
	
	/** Execute an action */
    final protected Object executeExecutable(Context $Context, Executable $Exec, Port Port, Object ... Params) {
		if($Exec instanceof Expression) return ExternalContext.execute(       $Context, (Expression)$Exec);
		else if($Exec.isFragment())     return ExternalContext.runFragment(   $Context, $Exec.asFragment());
		else if($Exec.isMacro())        return ExternalContext.execMacro(     $Context, $Exec.asMacro(),      (Object[])Params);
		else if($Exec.isSubRoutine())   return ExternalContext.callSubRoutine($Context, $Exec.asSubRoutine(), (Object[])Params);
		else {
            ActionRecord     AR = this.ARecord;
            LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
			throw new PatternActionError(
				String.format(
					"\nInvalid executable error: %s<Action:43>:\n    %s",
					(Port != null) ? Port.toString() + " " : "", LS
				)
			);
		}
	}
	
}
