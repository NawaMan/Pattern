package net.nawaman.pattern;

import net.nawaman.curry.Engine;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.AttributeInfo.AIDirect;

/** Source of a port */
class PDHost extends PortData {
	
    private static final long serialVersionUID = 4147485644727276319L;

    /** Creates a with source */
	PDHost(Pattern Host, AIDirect AInfo, PortKind pKind) {
		super(pKind);
		this.Host     = Host;
		this.AttrInfo = AInfo;
	}
	
	final Pattern  Host;
	final AIDirect AttrInfo;

	/**{@inheritDoc}*/ @Override
	public Pattern getPattern() {
		return this.Host;
	}
	/**{@inheritDoc}*/ @Override
	public String getPortName() {
		return this.AttrInfo.getName();
	}
	/**{@inheritDoc}*/ @Override
	public PortInfo getPortInfo() {
		return (PortInfo)this.AttrInfo.getDHInfo();
	}
	/**{@inheritDoc}*/ @Override
	public TypeRef getTypeRef() {
		return this.AttrInfo.getTypeRef();
	}
	/**{@inheritDoc}*/ @Override
	public Engine getEngine() {
		return this.Host.getEngine();
	}

	// Lock --------------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	Pattern.LocalLock getLocalInterface(Pattern.LocalLock pLocalInterface) {
		return pLocalInterface;
	}
}
