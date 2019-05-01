package net.nawaman.pattern;

import java.util.HashMap;
import java.util.Vector;

import net.nawaman.curry.Context;
import net.nawaman.curry.Engine;
import net.nawaman.curry.EngineExtension;
import net.nawaman.curry.Instruction;
import net.nawaman.curry.extra.type_object.EE_Object;

/** EngineExtension of Pattern */
public class EE_Pattern extends EngineExtension { 
		
	/** Name of this extension */
	static public final String Name = "Pattern";
	
		
	/** Constructs an engine extension. */
	public EE_Pattern() {}
		
	/**{@inheritDoc}*/ @Override
	protected String getExtName() {
		return Name;
	}
	/**{@inheritDoc}*/ @Override
	protected String[] getRequiredExtensionNames() {
		return new String[] { EE_Object.Name };
	}
	/**{@inheritDoc}*/ @Override
	protected String initializeThis() {
		// Register Pattern TypeKind
		this.regTypeKind(new TKPattern(this.getEngine()));
		
		// Register Port DataHolderFactory
		this.regDataHolderFactory(new PortFactory());
				
		// Register instruction
		this.regInst( -370849); // Inst_AddOtherAction
		this.regInst( -584001); // Inst_AddPostRenderAction
		this.regInst( -529697); // Inst_AddPreRenderAction
		this.regInst(-1085361); // Inst_NewWaitedAttributeAsRenderer
        this.regInst( -497825); // Inst_GetMapPortElement
        this.regInst( -684817); // Inst_GetAttrMapPortElement
        this.regInst( -436065); // Inst_GetDHValueOrNull
        this.regInst( -637873); // Inst_IsMapPortContainsKey
		
		return null;
	}
	/**{@inheritDoc}*/ @Override
	protected Instruction getNewInstruction(int hSearch) {
		Engine E = this.getEngine();
		
		switch(hSearch) {
			case  -370849: return new Instructions_Pattern.Inst_AddOtherAction              (E);
			case  -584001: return new Instructions_Pattern.Inst_AddPostRenderAction         (E);
			case  -529697: return new Instructions_Pattern.Inst_AddPreRenderAction          (E);
			case -1085361: return new Instructions_Pattern.Inst_NewWaitedAttributeAsRenderer(E);
            case  -497825: return new Instructions_Pattern.Inst_GetMapPortElement           (E);
            case  -684817: return new Instructions_Pattern.Inst_GetAttrMapPortElement       (E);
            case  -436065: return new Instructions_Pattern.Inst_GetDHValueOrNull            (E);
            case  -637873: return new Instructions_Pattern.Inst_IsMapPortContainsKey        (E);
		}
		
		return null;
	}

	// Ensure render ---------------------------------------------------------------------------------------------------

	private final HashMap<Integer, Pattern> Patterns = new HashMap<Integer, Pattern>();
	
	/** Register a pattern to ensure that it will be rendered */
	public void registerPattern(Pattern P) {
		if(P == null) return;
		
		// Add to the hash
		this.Patterns.put(P.PNUM, P);
	}
	
	/** Ensure all the registed patterns are rendered. */
	public void renderAllRegisteredPatterns() {
		Integer[] Is = this.Patterns.keySet().toArray(new Integer[this.Patterns.size()]);
		for(Integer I : Is) {
			// Get the pattern from the PNUM
			Pattern P = this.Patterns.get(I);
			
			// Render the pattern
			if(P != null)
				P.render();
			
			// Remove the pattern from the list
			this.Patterns.remove(I);
		}
	}
	
	// Post Render Action ----------------------------------------------------------------------------------------------
	
	private final HashMap<Renderer,    Vector<OtherAction>> Waited_Actions   = new HashMap<Renderer,    Vector<OtherAction>>();
	private final HashMap<OtherAction, Vector<Renderer>>    Action_Renderers = new HashMap<OtherAction, Vector<Renderer>>();
	private final HashMap<Renderer,    Vector<Renderer>>    AttrRenderers    = new HashMap<Renderer,    Vector<Renderer>>();
	
	void waitFor(OtherAction Action, Renderer ... Renderers) {
		// No action
		if(Action == null) return;
		
		// No renderer
		if((Renderers == null) || (Renderers.length == 0)) {
			this.performAction(Action);
			return;
		}
		
		boolean IsAllRendererRenderered = true;
		for(Renderer Renderer : Renderers) {
			// If null or rendered, skip
			if((Renderer == null) || Renderer.isRendered()) continue;
			
			IsAllRendererRenderered = false;
			this.waitForRenderer(Action, Renderer);
		}
		
		// Perform the action, if all are rendered (no more to wait)
		if(IsAllRendererRenderered)
			this.performAction(Action);
	}
    /** Notify that a renderer is just about to be rendered */
    void notifyRendering(Renderer Renderer) {
        this.notifyRendered(Renderer, true);
    }
    /** Notify that a renderer is rendered */
    void notifyRendered(Renderer Renderer) {
        this.notifyRendered(Renderer, false);
    }
	/** Notify that a renderer is rendered */
	private void notifyRendered(Renderer Renderer, boolean IsBefore) {
		try {
			// Get all the action waited by this renderer
			Vector<OtherAction> Actions = this.Waited_Actions.get(Renderer);
			
			// No action is waiting for this renderer, so done
			if(Actions == null)
				return;
			
			// Remove this renderer from all the Action
			// And see if, no more renderers left for this action to be waited, perform the action
			for(int a = Actions.size(); --a >= 0; ) {
				OtherAction Action = Actions.get(a);
				
				if (IsBefore && !(Action instanceof Action_PreRender))
				    continue;
				
				// Get the renderers that the action is waited
				Vector<Renderer> Renderers = this.Action_Renderers.get(Action);
				
				// This should not happend but just in case
				if(Renderers != null) {
					// Remove the renderer from the renderer list of the action
					Renderers.remove(Renderer);
					Actions  .remove(a);
					
					if(Action instanceof Action_PreRender) {
						// Remove Action from all waited for each render of this action
						for(int r = Renderers.size(); --r >= 0; ) {
							Renderer R = Renderers.get(r);
							Vector<OtherAction> RActions = this.Waited_Actions.get(R);
							RActions.remove(Action);
							
							if(RActions.size() == 0)
								this.Waited_Actions.remove(R);
						}
						
					} else if(Action instanceof Action_PostRender) {
						// If there are more renderer, keep going
						if(Renderers.size() != 0)
							continue;
					}
					
					// Remove the renderers list of the action
					this.Action_Renderers.remove(Action);
				}
					
				// Perform the action
				this.performAction(Action);
			}
			
			// If no more action waiting, remove the action
			if(Actions.size() == 0)
				this.Waited_Actions.remove(Renderer);
			
		} finally {
			Vector<Renderer> ARenderers = this.AttrRenderers.get(Renderer);
			if(ARenderers != null) {
				for(Renderer R : ARenderers)
					this.notifyRendered(R);
			}
		}
	}
	
	/** Set a pair of Action and Renderer */
	private void waitForRenderer(OtherAction Action, Renderer Renderer) {
		try {
			// Add Action into the Renderer list
			Vector<OtherAction> Actions = this.Waited_Actions.get(Renderer);
			if(Actions == null) this.Waited_Actions.put(Renderer, Actions = new Vector<OtherAction>());
			if (!Actions.contains(Action)) Actions.add(Action); 
			
			// Add Renderer into the Action
			Vector<Renderer> Renderers = this.Action_Renderers.get(Action);
			if(Renderers == null) this.Action_Renderers.put(Action, Renderers = new Vector<Renderer>());
			if (!Renderers.contains(Renderer)) Renderers.add(Renderer);
		} finally {
			// Add the temp attribute renderer
			if(Renderer instanceof WaitedAttributeAsRenderer) {
				Renderer AttrRenderer = ((WaitedAttributeAsRenderer)Renderer).TheAttr;
				if(AttrRenderer != null) {
					this.waitForRenderer(Action, AttrRenderer);
					
					Vector<Renderer> Renderers = this.AttrRenderers.get(AttrRenderer);
					if(Renderers == null) this.AttrRenderers.put(AttrRenderer, Renderers = new Vector<Renderer>());
					Renderers.add(Renderer);
				}
			}
		}
	}
	/** Perform a pattern action */
	private void performAction(OtherAction Action) {
		Engine  $Engine = this.getEngine();
		Context $Context = $Engine.newRootContext();
		if(!Action.isToRun($Context, null))
			return;
		
		if(Action instanceof Action_PostRender)
			((Action_PostRender)Action).doAction($Context);

		if(Action instanceof Action_PreRender)
			((Action_PreRender)Action).doAction($Context);
	}
}
