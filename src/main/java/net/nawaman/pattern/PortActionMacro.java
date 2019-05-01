package net.nawaman.pattern;

import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.JavaExecutable;

/** Macro used for Port Action */
abstract class PortActionMacro extends JavaExecutable.JavaMacro_Complex {
	
    private static final long serialVersionUID = -598766925266126214L;
    
    /** Construct a new macro */
	PortActionMacro(ExecSignature ES, Port pPort) {
		super(null, ES, null, null);
		this.Port = pPort;
	}
	Port Port;
}
