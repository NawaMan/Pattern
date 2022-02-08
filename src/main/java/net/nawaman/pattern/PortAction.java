package net.nawaman.pattern;

import net.nawaman.curry.Executable;

/** Action for a port */
abstract public class PortAction extends Action {
	
	static private final long serialVersionUID = 56563213216356825L;
	
	/** Constructs a Port action */
	protected PortAction(Executable pCondition) {
		super(pCondition);
	}

}