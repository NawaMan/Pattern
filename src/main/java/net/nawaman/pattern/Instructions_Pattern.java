package net.nawaman.pattern;

import net.nawaman.curry.*;
import net.nawaman.curry.Instructions_DataHolder.*;
import net.nawaman.curry.compiler.*;
import net.nawaman.curry.util.DataHolder;
import net.nawaman.util.*;

/** Collections of instructions for pattern */
public class Instructions_Pattern {

	/** The instruction to add PostRender Action*/
	static public final class Inst_AddOtherAction extends Inst_AbstractSimple {
	    
		static public final String Name = "addOtherAction";
		
		Inst_AddOtherAction(Engine pEngine) {
			super(
				pEngine,
				String.format(
					"%s(%s,~...):^",
					Name,
					OtherAction.class.getCanonicalName()
				)
			);
		}
		EE_Pattern $EEP = null;
		
		/**{@inherDoc}*/ @Override
		protected Object run(Context pContext, Object[] pParams) {
			if(this.$EEP == null)
				this.$EEP = (EE_Pattern)(ExternalContext.getEngine(pContext).getExtension(EE_Pattern.Name));
			
			// Wait for the action
			if((pParams[0] instanceof Action_PostRender) || (pParams[0] instanceof Action_PreRender)) {
				Object[]   RObjs     = UArray.getObjectArray(pParams[1]);
				Renderer[] Renderers = new Renderer[(RObjs == null) ? 0 : RObjs.length];
				for(int i = 0; i < Renderers.length; i++)
					Renderers[i] = (Renderer)RObjs[i];
				
				this.$EEP.waitFor(
					(OtherAction)pParams[0],
					Renderers
				);
				
			} else if(pParams[0] != null){
				throw new CurryError("Unknown action: " + ExternalContext.getEngine(pContext).toDetail(pParams[0]));
			}
			return null;
		}
	}

	/** The instruction to add PreRender Action */
	static public final class Inst_AddPreRenderAction extends Inst_AbstractSimple {
	    
		static public final String Name = "addPreRenderAction";
		
		Inst_AddPreRenderAction(Engine pEngine) {
			super(
				pEngine,
				String.format(
					"%s(%s,%s,~...):^",
					Name,
					Executable.class.getCanonicalName(),
					Executable.class.getCanonicalName()
				)
			);
		}
		EE_Pattern $EEP = null;
		
		/**{@inherDoc}*/ @Override
		protected Object run(Context pContext, Object[] pParams) {
			if(this.$EEP == null)
				this.$EEP = (EE_Pattern)(ExternalContext.getEngine(pContext).getExtension(EE_Pattern.Name));
			
			Object[] Params = UArray.getObjectArray(pParams[2]);
			if((Params == null) || (Params.length == 0))
				return null;
			
			Renderer[] Renders = new Renderer[Params.length];
			for(int i = 0; i < Params.length; i++)
				Renders[i] = (Renderer)Params[i];
				
			// Wait for the action
			// TODO - Do something with this
			this.$EEP.waitFor(
				new Action_PreRender.Simple(
					(Executable)pParams[0],
					(Executable)pParams[1]
				),
				Renders
			);	
			return null;
		}
	}

	/** The instruction to add PostRender Action */
	static public final class Inst_AddPostRenderAction extends Inst_AbstractSimple {
	    
		static public final String Name = "addPostRenderAction";
		
		Inst_AddPostRenderAction(Engine pEngine) {
			super(
				pEngine,
				String.format(
					"%s(%s,%s,~...):^",
					Name,
					Executable.class.getCanonicalName(),
					Executable.class.getCanonicalName()
				)
			);
		}
		EE_Pattern $EEP = null;
		
		/**{@inherDoc}*/ @Override
		protected Object run(Context pContext, Object[] pParams) {
			if(this.$EEP == null)
				this.$EEP = (EE_Pattern)(ExternalContext.getEngine(pContext).getExtension(EE_Pattern.Name));
			
			Object[] Params = UArray.getObjectArray(pParams[2]);
			if((Params == null) || (Params.length == 0))
				return null;
			
			Renderer[] Renders = new Renderer[Params.length];
			for(int i = 0; i < Params.length; i++)
				Renders[i] = (Renderer)Params[i];
				
			// Wait for the action
			this.$EEP.waitFor(
				new Action_PostRender.Simple(
					(Executable)pParams[0],
					(Executable)pParams[1]
				),
				Renders
			);	
			return null;
		}
	}

	/** The instruction to add PostRender Action */
	static public final class Inst_NewWaitedAttributeAsRenderer extends Inst_AbstractSimple {
	    
		static public final String Name = "newWaitedAttributeAsRenderer";
		
		Inst_NewWaitedAttributeAsRenderer(Engine pEngine) {
			super(
				pEngine,
				String.format(
					"%s(+%s,!,+$):%s",
					Name,
					Pattern .class.getCanonicalName(),
					Renderer.class.getCanonicalName()
				)
			);
		}
		
		/**{@inherDoc}*/ @Override
		protected Object run(Context pContext, Object[] pParams) {
			Pattern thePattern  = (Pattern)pParams[0];
			Type    theAsType   = (Type)   pParams[1];
			String  theAttrName = (String) pParams[2];
			return new WaitedAttributeAsRenderer(pContext, thePattern, theAsType, theAttrName);
		}
	}
	
    /** The instruction to get map-port element */
    static public final class Inst_GetMapPortElement extends Inst_AbstractSimple {
        
        static public final String Name = "getMapPortElement";
        
        Inst_GetMapPortElement(Engine pEngine) {
            super(
                pEngine,
                String.format(
                    "%s(%s,~):%s",
                    Name,
                    DataHolder.class.getCanonicalName(),
                    DataHolder.class.getCanonicalName()
                )
            );
        }
        
        /**{@inherDoc}*/ @Override
        protected Object run(Context pContext, Object[] pParams) {
            //final Port     aPort = (Port)aDH;
            final Port     aPort = (Port)pParams[0];
            final PortKind aKind = aPort.PData.getKind();
            if (!(aKind instanceof PKMap))
                throw new PatternActionError("Map-Port is need for parameter #0", pContext);
            
            final Engine  $Engine       = this.getEngine();
            final TypeRef aKeyType      = aPort.PData.getTypeRef().getParameters($Engine)[0];
            final Object  aKey          = pParams[1];
            final boolean aIsCompatible = aKeyType.canBeAssignedBy($Engine, aKey);
            if (!aIsCompatible)
                throw new PatternActionError(String.format(
                        "Incompatible Map-Port key: `%s` is needed by '%s' found.",
                        aKeyType,
                        $Engine.toString(aKey)
                    ),
                    pContext);
            
            final Port aElement = (Port)aPort.config(
                    PKMap.CONFIG_NAME_GET_MAP_ELEMENT_PORT,
                    new Object[] { aKey }
                );
            return aElement;
        }
        
        TKDataHolder $TKData = null;
        
        /**{@inherDoc}*/ @Override
        public TypeRef getReturnTypeRef(
                final Expression     pExpr,
                final CompileProduct pCProduct) {
            final Engine $Engine = pCProduct.getEngine();
            if(this.$TKData == null)
                this.$TKData = (TKDataHolder)$Engine.getTypeManager().getTypeKind(TKDataHolder.KindName);
            
            final TypeRef aValueTRef;
            
            final TypeRef aTRef         = pCProduct      .getReturnTypeRefOf(pExpr.getParam(0));
            final TypeRef aCurryMapTRef = $Engine.getTypeManager().getPrefineTypeRef("Map");
            final boolean aIsTRefMap    = aCurryMapTRef.canBeAssignedByInstanceOf($Engine, aTRef);
            if (aIsTRefMap) {
                final TypeRef[] aParamsTRefs = aTRef.getParameters($Engine);
                aValueTRef = aParamsTRefs[1];
            } else {
                final TypeRef[] aParamsTRefs = aTRef          .getParameters($Engine);
                final TypeRef[] aPortTRefs   = aParamsTRefs[0].getParameters($Engine);
                aValueTRef = aPortTRefs[1];
            }
            final TypeRef aDHTRef = this.$TKData.getNoNameTypeRef(aValueTRef, true, true, null);
            return aDHTRef;
        }
        
        /**{@inherDoc}*/ @Override
        public boolean ensureParamCorrect(
                final Expression     pExpr,
                final CompileProduct pCProduct) {
            final boolean aEnsure = super.ensureParamCorrect(pExpr, pCProduct);
            if (!aEnsure)
                return false;
            
            final Object  aParam0        = pExpr.getParam(0);
            final TypeRef aExprPrm0TRef  = pCProduct.getReturnTypeRefOf(aParam0);
            final Engine  $Engine        = this.getEngine();
            final String  aExprPrm0TKind = aExprPrm0TRef.getTypeKindName($Engine);
            final boolean aIsDataHoder   = TKDataHolder.KindName.equals(aExprPrm0TKind);
            if (!aIsDataHoder) {
                final int    aPos = pCProduct.getPosition(pExpr);
                final String aMsg = "Map-port is required. <Inst_GetMapPortElement:252>";
                pCProduct.reportError(aMsg, null, aPos);
                return false;
            }
            final TypeRef[] aExprParamTRefs = aExprPrm0TRef.getParameters($Engine);
            final TypeRef   aDHDataTRef     = aExprParamTRefs[0];
            final TypeRef   aMapTRef        = $Engine.getTypeManager().getPrefineTypeRef("Map");
            // Check if Map port
            final boolean aIsMap   = aMapTRef.canBeAssignedByInstanceOf($Engine, aDHDataTRef);
            if (!aIsMap) {
                final int    aPos = pCProduct.getPosition(pExpr);
                final String aMsg = "Map-port is required. <Inst_GetMapPortElement:263>";
                pCProduct.reportError(aMsg, null, aPos);
                return false;
            }
            
            final Object aParam1 = pExpr.getParam(1);
            
            final TypeRef[] aMapParamTRefs   = aDHDataTRef.getParameters($Engine);
            final TypeRef   aMapKeyTRef      = aMapParamTRefs[0];
            final TypeRef   aParam1TRef      = pCProduct.getReturnTypeRefOf(aParam1);
            final boolean   aIsKeyCompatible = aMapKeyTRef.canBeAssignedByInstanceOf($Engine, aParam1TRef);
            if (!aIsKeyCompatible) {
                final int    aPos = pCProduct.getPosition(pExpr);
                final String aMsg = "The Key is required to be '%s' but '%s' is found. <Inst_GetMapPortElement:276>";
                pCProduct.reportError(String.format(aMsg, aMapKeyTRef, aParam1TRef), null, aPos);
                return false;
            }
            
            return true;
        }
    }
    
    /** The instruction to get map-port element */
    static public final class Inst_GetAttrMapPortElement extends Inst_AbstractSimple {
        
        static public final String Name = "getAttrMapPortElement";
        
        Inst_GetAttrMapPortElement(Engine pEngine) {
            super(
                pEngine,
                String.format(
                    "%s(%s,+$,~):%s",
                    Name,
                    Pattern   .class.getCanonicalName(),
                    DataHolder.class.getCanonicalName()
                )
            );
        }
        
        /**{@inherDoc}*/ @Override
        protected Object run(Context pContext, Object[] pParams) {
            final Pattern aPattern  = (Pattern)pParams[0];
            final String  aAttrName = (String) pParams[1];
            final Object  aKey      =          pParams[2];
            final Port aElement = (Port)aPattern.configAttr(
                    aAttrName,
                    PKMap.CONFIG_NAME_GET_MAP_ELEMENT_PORT,
                    new Object[] { aKey });
            return aElement;
        }
        
        TKDataHolder $TKData = null;
        
        /**{@inherDoc}*/ @Override
        public TypeRef getReturnTypeRef(
                final Expression     pExpr,
                final CompileProduct pCProduct) {
            final Engine $Engine = pCProduct.getEngine();
            if(this.$TKData == null)
                this.$TKData = (TKDataHolder)$Engine.getTypeManager().getTypeKind(TKDataHolder.KindName);
            
            final TypeRef aValueTRef;
            
            final TypeRef aTRef     = pCProduct      .getReturnTypeRefOf(pExpr.getParam(0));
            final String  aAttrName = (String)pExpr.getParam(1);
            final TypeRef aMapTref  = aTRef.searchObjectAttribute($Engine, aAttrName);
            if (aMapTref == null) {
                final int aExprPos = pCProduct.getPosition(pExpr);
                pCProduct.reportError(
                        String.format(
                            "Attrbute '%s' is not found in pattern of type '%s'. " +
                            "<Inst_GetAttrMapPortElement:346>",
                            aAttrName, aTRef
                        ), null, aExprPos);
                return null;
            }
            
            final TypeRef aCurryMapTRef = $Engine.getTypeManager().getPrefineTypeRef("Map");
            final boolean aIsTRefMap    = aCurryMapTRef.canBeAssignedByInstanceOf($Engine, aMapTref);
            if (aIsTRefMap) {
                final TypeRef[] aParamsTRefs = aMapTref.getParameters($Engine);
                aValueTRef = aParamsTRefs[1];
            } else {
                final TypeRef[] aParamsTRefs = aMapTref       .getParameters($Engine);
                final TypeRef[] aPortTRefs   = aParamsTRefs[0].getParameters($Engine);
                aValueTRef = aPortTRefs[1];
            }
            final TypeRef aDHTRef = this.$TKData.getNoNameTypeRef(aValueTRef, true, true, null);
            return aDHTRef;
        }
        
        /**{@inherDoc}*/ @Override
        public boolean ensureParamCorrect(
                final Expression     pExpr,
                final CompileProduct pCProduct) {
            final boolean aEnsure = super.ensureParamCorrect(pExpr, pCProduct);
            if (!aEnsure)
                return false;
            
            final Object  aParam0        = pExpr.getParam(0);
            final TypeRef aExprPrm0TRef  = pCProduct.getReturnTypeRefOf(aParam0);
            
            final Engine  $Engine   = this.getEngine();
            final String  aAttrName = (String)pExpr.getParam(1);
            final TypeRef aAttrTRef = aExprPrm0TRef.searchObjectAttribute($Engine, aAttrName);
            if (aAttrTRef == null) {
                final int aExprPos = pCProduct.getPosition(pExpr);
                pCProduct.reportError(
                        String.format(
                            "Attrbute '%s' is not found in pattern of type '%s'. " +
                            "<Inst_GetAttrMapPortElement:373>",
                            aAttrName, aExprPrm0TRef
                        ), null, aExprPos);
                return false;
            }
            
            // Check if Map port
            final TypeRef aMapTRef = $Engine.getTypeManager().getPrefineTypeRef("Map");
            final boolean aIsMap   = aMapTRef.canBeAssignedByInstanceOf($Engine, aAttrTRef);
            if (!aIsMap) {
                final int    aPos = pCProduct.getPosition(pExpr);
                final String aMsg = "Map-port is required. <Inst_GetMapPortElement:384>";
                pCProduct.reportError(aMsg, null, aPos);
                return false;
            }
            
            final Object    aParam2          = pExpr.getParam(2);
            final TypeRef[] aMapParamTRefs   = aAttrTRef.getParameters($Engine);
            final TypeRef   aMapKeyTRef      = aMapParamTRefs[0];
            final TypeRef   aParam1TRef      = pCProduct.getReturnTypeRefOf(aParam2);
            final boolean   aIsKeyCompatible = aMapKeyTRef.canBeAssignedByInstanceOf($Engine, aParam1TRef);
            if (!aIsKeyCompatible) {
                final int    aPos = pCProduct.getPosition(pExpr);
                final String aMsg = "The Key is required to be '%s' but '%s' is found. <Inst_GetMapPortElement:397>";
                pCProduct.reportError(String.format(aMsg, aMapKeyTRef, aParam1TRef), null, aPos);
                return false;
            }
            
            return true;
        }
    }
    
    static public class Inst_GetDHValueOrNull extends Inst_GetDHValue {
        
        static public final String Name = "getDHValueOrNull";
        
        Inst_GetDHValueOrNull(Engine pEngine) {
            super(pEngine, Name + "(D):~");
        }
        
        /**{@inherDoc}*/ @Override
        protected Object run(Context pContext, Object[] pParams) {
            final DataHolder aDH = (DataHolder)pParams[0];
            if (aDH == null)
                return null;
            
            final Object aResult = super.run(pContext, pParams);
            return aResult;
        }
    }
    
    static public class Inst_IsMapPortContainsKey extends Inst_AbstractSimple {
        
        static public final String Name = "isMapPortContainsKey";
        
        Inst_IsMapPortContainsKey(Engine pEngine) {
            super(pEngine, Name + String.format("(%s,~):?", Port.class.getCanonicalName()));
        }
        
        /**{@inherDoc}*/ @Override
        protected Object run(Context pContext, Object[] pParams) {
            final Port aPort = (Port)pParams[0];
            if (aPort == null)
                return null;
            
            final PortKind aPKind = aPort.PData.getKind() ;
            if (!(aPKind instanceof PKMap)) {
                throw new PatternActionError(
                        "Map-port is required for `isMapPortContainsKey`.",
                        pContext);
            }
            
            final boolean aIsPortRendered = aPort.isRendered();
            if (!aIsPortRendered)
                aPort.render(pContext);
            
            final PFVMap  aFVMap        = ((PFVMap)aPort.IPIFinal);
            final Object  aKey          = pParams[1];
            final boolean aIsContainKey = aFVMap.containsKey(aKey);
            return aIsContainKey;
        }
        
        // TODO - Refactor this with the get element
        /**{@inherDoc}*/ @Override
        public boolean ensureParamCorrect(
                final Expression     pExpr,
                final CompileProduct pCProduct) {
            final boolean aEnsure = super.ensureParamCorrect(pExpr, pCProduct);
            if (!aEnsure)
                return false;
            
            final Object aParam0 = pExpr.getParam(0);
            
            final TypeRef aExprPrm0TRef  = pCProduct.getReturnTypeRefOf(aParam0);
            final Engine  $Engine        = this.getEngine();
            final String  aExprPrm0TKind = aExprPrm0TRef.getTypeKindName($Engine);
            final boolean aIsDataHoder   = TKDataHolder.KindName.equals(aExprPrm0TKind);
            if (!aIsDataHoder) {
                final int    aPos = pCProduct.getPosition(pExpr);
                final String aMsg = "Map-port is required. <Inst_IsMapPortContainsKey:347>";
                pCProduct.reportError(aMsg, null, aPos);
                return false;
            }
            final TypeRef[] aExprParamTRefs = aExprPrm0TRef.getParameters($Engine);
            final TypeRef   aDHDataTRef     = aExprParamTRefs[0];
            final TypeRef   aMapTRef        = $Engine.getTypeManager().getPrefineTypeRef("Map");
            // Check if Map port
            final boolean aIsMap   = aMapTRef.canBeAssignedByInstanceOf($Engine, aDHDataTRef);
            if (!aIsMap) {
                final int    aPos = pCProduct.getPosition(pExpr);
                final String aMsg = "Map-port is required. <Inst_IsMapPortContainsKey:358>";
                pCProduct.reportError(aMsg, null, aPos);
                return false;
            }
            
            final Object aParam1 = pExpr.getParam(1);
            
            final TypeRef[] aMapParamTRefs   = aDHDataTRef.getParameters($Engine);
            final TypeRef   aMapKeyTRef      = aMapParamTRefs[0];
            final TypeRef   aParam1TRef      = pCProduct.getReturnTypeRefOf(aParam1);
            final boolean   aIsKeyCompatible = aMapKeyTRef.canBeAssignedByInstanceOf($Engine, aParam1TRef);
            if (!aIsKeyCompatible) {
                final int    aPos = pCProduct.getPosition(pExpr);
                final String aMsg = "The Key is required to be '%s' but '%s' is found. <Inst_IsMapPortContainsKey:371>";
                pCProduct.reportError(String.format(aMsg, aMapKeyTRef, aParam1TRef), null, aPos);
                return false;
            }
            
            return true;
        }
    }
}
