package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.LocationSnapshot;

/** Exception about PatternAction */
public class PatternActionError extends RuntimeException {
    
    private static final long serialVersionUID = 3182565639154765960L;

    // Public constructors -------------------------------------------------------------------------

    /** Constructs a new PatternActionError with <code>null</code> as its detail message. */
    public PatternActionError() {
    	super();
    	this.Locations = null;
    }

    /**
     * Constructs a new PatternActionError with the specified detail message.
     * @param	pMessage	the detail message. The detail message is saved for later retrieval by
     *                         the {@link #getMessage()} method.
     */
    public PatternActionError(String pMessage) {
    	super(pMessage);
    	this.Locations = null;
    }

    /**
     * Constructs a new PatternActionError with the specified detail message and cause.
     * 
     * @param   pMessage   the detail message (which is saved for later retrieval by the
     *                        {@link #getMessage()} method).
     * @param   pCause      the cause (which is saved for later retrieval by the {@link #getCause()}
     *                        method).  (A <tt>null</tt> value is permitted, and indicates that the
     *                        cause is nonexistent or unknown.)
     */
    public PatternActionError(String pMessage, Throwable pCause) {
    	super(pMessage, pCause);
    	this.Locations = null;
    }
    
    // Local constructors --------------------------------------------------------------------------
    
    static String getLocationsToStringPrefix(Context pContext) {
    	LocationSnapshot[] Locations = Context.getLocationsOf(pContext);
    	return (Locations != null)?"\nLocation(s):" + Context.getLocationsToString(Locations) + "\nError: ":"";
    }
    
    /**
     * Constructs a new PatternActionError with the specified detail message and cause.
     */
    public PatternActionError(Context pContext) {
    	super(getLocationsToStringPrefix(pContext));
    	this.Locations = Context.getLocationsOf(pContext);
    }
    
    /**
     * Constructs a new PatternActionError with the specified detail message and cause.
     * 
     * @param   pMessage   the detail message (which is saved for later retrieval by the
     *                        {@link #getMessage()} method).
     * @param   pCause      the cause (which is saved for later retrieval by the {@link #getCause()}
     *                        method).  (A <tt>null</tt> value is permitted, and indicates that the
     *                        cause is nonexistent or unknown.)
     */
    public PatternActionError(String pMessage, Context pContext) {
    	super(getLocationsToStringPrefix(pContext) + pMessage);
    	this.Locations = Context.getLocationsOf(pContext);
    }
    
    /**
     * Constructs a new PatternActionError with the specified detail message and cause.
     * 
     * @param   pMessage   the detail message (which is saved for later retrieval by the
     *                        {@link #getMessage()} method).
     * @param   pCause      the cause (which is saved for later retrieval by the {@link #getCause()}
     *                        method).  (A <tt>null</tt> value is permitted, and indicates that the
     *                        cause is nonexistent or unknown.)
     */
    public PatternActionError(String pMessage, Context pContext, Throwable pCause) {
    	super(getLocationsToStringPrefix(pContext) + pMessage, pCause);
    	this.Locations = Context.getLocationsOf(pContext);
    }
    
    // Location ------------------------------------------------------------------------------------

    /** The locations in which the curry error was create. */
    final LocationSnapshot[] Locations;
    
    /** Returns the locations in which the curry error was create. */
    public LocationSnapshot[] getLocations() {
    	return (this.Locations == null)?null:this.Locations.clone();
    }
}
