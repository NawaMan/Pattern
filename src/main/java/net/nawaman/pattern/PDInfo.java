package net.nawaman.pattern;

import net.nawaman.curry.Engine;
import net.nawaman.curry.TypeRef;

/** Type of a port */
class PDInfo extends PortData {

    private static final long serialVersionUID = 5274633631345690788L;

    /** Creates a with type */
	PDInfo(Engine $Engine, PortInfo PInfo, PortKind pKind) {
		super(pKind);
		this.Info   = PInfo;
		this.Engine = $Engine;
	}
	
	PortInfo Info;
	Engine   Engine;

	/**{@inheritDoc}*/ @Override
	public Pattern getPattern() {
		return null;
	}
	/**{@inheritDoc}*/ @Override
	public String getPortName() {
		return null;
	}
	/**{@inheritDoc}*/ @Override
	public PortInfo getPortInfo() {
		return this.Info;
	}
	/**{@inheritDoc}*/ @Override
	public TypeRef getTypeRef() {
		return this.Info.getTypeRef();
	}
	/**{@inheritDoc}*/ @Override
	public Engine getEngine() {
		return this.Engine;
	}

	// Lock --------------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	Pattern.LocalLock getLocalInterface(Pattern.LocalLock pLocalInterface) {
		return pLocalInterface;
	}
}
