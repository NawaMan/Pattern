package net.nawaman.pattern;

import java.util.HashMap;
import java.util.Vector;

import net.nawaman.curry.ActionRecord;
import net.nawaman.curry.AttributeInfo;
import net.nawaman.curry.Context;
import net.nawaman.curry.CurryError;
import net.nawaman.curry.DObject;
import net.nawaman.curry.Engine;
import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.Expression;
import net.nawaman.curry.ExternalContext;
import net.nawaman.curry.JavaExecutable;
import net.nawaman.curry.Location;
import net.nawaman.curry.MExecutable;
import net.nawaman.curry.OperationInfo;
import net.nawaman.curry.TKInterface;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.Type;
import net.nawaman.curry.Executable.Macro;
import net.nawaman.curry.Executable.SubRoutine;
import net.nawaman.curry.Instructions_Executable.Inst_Run;
import net.nawaman.curry.util.DataHolder;
import net.nawaman.util.UObject;

/** A pattern */
public class Pattern extends DObject implements Renderer {

	static int PatternNumber = 0;
	
	/** Returns the current pattern number */
	static public int getCurrentPatternNumber() {
		return PatternNumber;
	}
	
	/** Constructs a pattern */
	protected Pattern(Context pContext, Type pTheType) {
		super(pTheType);
		
		// Record the pattern number
		this.PNUM = PatternNumber++;
		
		// Record the creator
		this.CreatorAction = ExternalContext.newCallerActionRecord_Hack(pContext);
	}
	
	/** The pattern number of this pattern */
	public final int PNUM;
	
	/** Action record of the creation of this pattern */
	public final ActionRecord CreatorAction;

	// Ensure that the dataholder is a port and link to this pattern ----------------------------------------

	/** {@inheritDoc} */ @Override
	protected void initializeDH(Context pContext, Engine pEngine, DataHolder.AccessKind DHAK, AttributeInfo.AIDirect AI) {
		if (!(AI.getDHInfo() instanceof PortInfo)) {
			
			if(TKInterface.isTypeInterface(AI.getOwnerAsType())) {
				throw new CurryError(
					String.format(
						"Pattern '%s' fails to implement the interface `%s` as it does not implement the port `%s` <Pattern:62>",
						this.getTheType(), AI.getOwnerAsType(), AI.getName()
					),
					pContext
				);
			}
			
			throw new CurryError(
				String.format(
					"All non-static attribute of a pattern must be a port (`%s`) <Pattern:24>",
					AI.getName()
				),
				pContext
			);
		}

		// Do what needed to be done
		super.initializeDH(pContext, pEngine, DHAK, AI);

		// Ensure the data-holder is a port
		DataHolder DH = this.getDHAt(pContext, DHAK, AI);
		if (!(DH instanceof Port)) {
			throw new CurryError(
				String.format(
					"All non-static attribute of a pattern must be a port (`%s`) <Pattern:42>",
					AI.getName()
				),
				pContext
			);
		}

		// Set it to this host and AI
		((Port)DH).setToHostAndName(this, AI);
	}
	
	Port getPort(int Index) {
		DataHolder DH = this.getDHAt_RAW(Index);
		return (DH instanceof Port) ? (Port)DH : null;
	}

	// Rendering -------------------------------------------------------------------------------------------------------
	
	static final HashMap<Engine, SubRoutine> RenderFunctions = new HashMap<Engine, SubRoutine>();
	
	/** Signature for render function */
	static final ExecSignature RenderMacro_Signature =
					ExecSignature.newProcedureSignature(
						"render",
						TKJava.TVoid.getTypeRef(),
						new Location("net/nawaman/pattern/Patten.java", 0, 80),
						null
					);
	
	/** Returns a sub-routine of the render function used for each engine */
	static SubRoutine GetRenderFunction(Engine E) {
		if(E == null) return null;
		
		// Try to get from the cache
		SubRoutine RenderSub = RenderFunctions.get(E);
		if(RenderSub != null) return RenderSub;

		// Create
		MExecutable ME = E.getExecutableManager();		
		RenderSub = ME.newSubRoutine(RenderMacro_Signature,
			ME.newExpr(
				Inst_Run.Name,
				new RenderFragment()
			)
		);
		
		// Save
		RenderFunctions.put(E, RenderSub);
		
		// Return
		return RenderSub;
	}
	
	/** Fragment that redirect the execution to doRender method in Pattern.java */
	static class RenderFragment extends JavaExecutable.JavaFragment_Complex {
	    
        private static final long serialVersionUID = -6386610002866557610L;
        
        /** Construct a new native sub-routine */
		public RenderFragment() {
			super(RenderMacro_Signature);
		}
		// Executing -----------------------------------------------------------
		/**{@inheritDoc*/ @Override
		protected Object run(Context $Context) {
			Object O = ExternalContext.getStackOwner($Context);
			if(!(O instanceof Pattern)) {
				throw new CurryError(
					"Fatal error: RenderMacro must only be called with a Pattern stack <Pattern:102>",
					$Context
				);
			}
			
			// Do the rendering
			Pattern P = (Pattern)O;
			P.render($Context);
			
			return null;
		}
	}
	
	static private Vector<Pattern> BeingRendered = new Vector<Pattern>() {
        private static final long serialVersionUID = 5493551089480421000L;
        public @Override boolean contains(Object pPattern) {
	        for(int i = this.size(); --i >= 0; ) {
	            final Pattern aElement = this.get(i);
	            final Pattern aPattern = (Pattern)pPattern;
	            if (aElement.PNUM == aPattern.PNUM)
	                return true;
	        }
	        return false;
	    }
        public @Override boolean remove(Object pPattern) {
            for(int i = this.size(); --i >= 0; ) {
                final Pattern aElement = this.get(i);
                final Pattern aPattern = (Pattern)pPattern;
                if (aElement.PNUM == aPattern.PNUM) {
                    this.remove(i);
                    return true;
                }
            }
            return false;
        }
	};
	
	boolean IsRendered = false;
	
	/** Checks if this port has not been asked to render */
	public boolean isNotRendered() {
		return !this.isRendered() && !this.isBeingRendered();
	}
	
	/** Checks if this port has been rendered */
	public boolean isRendered() {
		return this.IsRendered;
	}
	
	/** Checks if this port is currently being rendering */
	public boolean isBeingRendered() {
		return BeingRendered.contains(this);
	}
	
	// Detect method invocation ----------------------------------------------------------------------------------------


	/**{@inheritDoc}*/ @Override
	protected OperationInfo getOperationLocal(Context pContext, Type pAsType, ExecSignature pSignature) {
		// If this pattern is not yet rendered, detect if a method is called (an not a function or a procedure)
		// If so, render this
		if(!this.isRendered()) {
			OperationInfo OI = super.getOperationLocal(pContext, pAsType, pSignature);
			if((OI != null) && UPattern.CheckMethodFromMoreData(OI.getMoreData())) {
				// The OI is a method, force render this pattern
				// NOTE: Must use "invoke" as we can be sure that the context used in render belong to this pattern.
				ExecSignature ES = this.searchOperation(this.getEngine(), "render", UObject.EmptyObjectArray, null);
				Expression CurrentExpression = ExternalContext.getCurrentExpression(pContext);
				this.invokeDirect(pContext, CurrentExpression, false, pAsType, ES, UObject.EmptyObjectArray);
			}
			return OI;
		}
		return super.getOperationLocal(pContext, pAsType, pSignature);
	}
	
	// Pattern specific ------------------------------------------------------------------------------------------------
	
	/** Ensure all port of the specified type are rendered */
	private void renderPorts(Context $Context, boolean IsAllInPorts) {
		for(int i = 0; i < this.getMaxDHIndex(); i++){
			DataHolder DH = this.getDHByIndex($Context, DataHolder.AccessKind.Get, i);
			if(!(DH instanceof Port)) continue;
			
			AttributeInfo AI = this.getAttrInfoAt(i);
			if(AI == null) continue;
			
			// Filter out the port
			if(IsAllInPorts != UPattern.CheckInPortFromMoreData(AI.getMoreData()))
				continue;
			
			Port Port = (Port)DH;
			if(Port.isRendered()) continue;
			
			Port.render($Context);
		}
	}
	
	/** Ensure this pattern is rendered */
	final private void render(Context $Context) {
		// Detect recurive ------------------------------------------------------------------------
		if(BeingRendered.contains(this)) {
			if((this != BeingRendered.get(BeingRendered.size() - 1)))
				throw new CurryError(
					String.format(
						"Pattern recursive rendering is detected ('%s')",
						this.toString_Original()
					)
				);
		}
		
		// Already rendered, go on
		if(this.IsRendered)
			return;
		
		BeingRendered.add(this);
		
		
        // Pre Rendering Actions -----------------------------------------------------------------
		
        // Notify that this patter is about to be rendered
        EE_Pattern EEP = (EE_Pattern)this.getEngine().getExtension(EE_Pattern.Name);
        EEP.notifyRendering(this);
        
        
		
		// Perform the rendering ------------------------------------------------------------------
		
		TPattern PT = (TPattern)ExternalContext.getStackOwnerAsType($Context);
		
		// Forcefully render all in-port ------------------------------------------------
		this.renderPorts($Context, true);
		
		// Get the Macro ----------------------------------------------------------------
		TSPattern PTS    = (TSPattern)PT.getTypeSpec();
		Macro     Render = PTS.getRenderFunctionMacro();
		// If not exist, see if there is one in super
		if(Render == null) {
			Type Super = PT.getSuper();
			while((Render == null) && (Super instanceof TPattern)) {
				Render = ((TSPattern)Super.getTypeSpec()).getRenderFunctionMacro();
				Super = ((TPattern)Super).getSuper();
			}
		}
		// Execute the render macro
		if(Render != null)
			// TODO - The StackOwner as Type does not transmit there so super.render() cannot be called for now
			ExternalContext.execMacro($Context, Render);

		// Forcefully render all out-port -----------------------------------------------
		this.renderPorts($Context, false);
		
		
		
		// Remove from being rendered -------------------------------------------------------------
		BeingRendered.remove(this);
		this.IsRendered = true;
		
		
		
		// Post Rendering Actions -----------------------------------------------------------------

		// Notify that this pattern is rendered
		EEP.notifyRendered(this);
	}
	
	/** Ensure this pattern is rendered */
	final public void render() {
		// Do the rendering
		this.render(this.getEngine().newRootContext());
	}
	
	// Objectable ------------------------------------------------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	protected boolean doDefault_is(Object O) {
		return this == O;
	}
	/**{@inheritDoc}*/ @Override
	protected boolean doDefault_equals(Object O) {
		if(!(O instanceof Pattern))
		    return false;
		
		Pattern P = (Pattern)O;
		if(!this.getEngine().equals(this.PNUM,         P.PNUM))         return false;
		if(!this.getEngine().equals(this.getTheType(), P.getTheType())) return false;
		
		return true;
	}

	/** The original way of doing toString for pattern */
	public String toString_Original() {
		return this.getTheType() + "#" + this.PNUM;
	}
	
	/**{@inheritDoc}*/ @Override
	public String toString() {
		return this.toString_Original();
	}
	/**{@inheritDoc}*/ @Override
	public String toDetail() {
		return this.toString();
	}

	/**{@inheritDoc}*/ @Override
	public int hashCode() {
		return this.getEngine().hash(this.getTheType()) + this.PNUM;
	}
	/**{@inheritDoc}*/ @Override
	public int hash() {
		return this.hashCode();
	}
	/** Converts this pattern to a CharSequence */
	public CharSequence toCharSequence() {
		return this.toString();
	}

	// Lock ------------------------------------------------------------------------------------------------------------

	/** A lock to limit the interface to be used only internally */
	static class LocalLock {}
}
