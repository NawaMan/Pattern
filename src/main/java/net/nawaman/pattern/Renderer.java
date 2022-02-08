package net.nawaman.pattern;

/** Something that can render */
public interface Renderer {

	/** Checks if this renderer has not been asked to render */
	public boolean isNotRendered();
	
	/** Checks if this renderer has been rendered */
	public boolean isRendered();
	
	/** Checks if this renderer is currently being rendering */
	public boolean isBeingRendered();
	
}
