package net.nawaman.pattern;

import net.nawaman.curry.Executable;

/** Action for a port */
abstract public class OtherAction extends Action {
	
	static private final long serialVersionUID = 35615465658872651L;
	
	/** Constructs a Port action */
	protected OtherAction(Executable pCondition) {
		super(pCondition);
	}

}