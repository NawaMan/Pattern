package net.nawaman.pattern;

import java.util.Vector;

import net.nawaman.curry.Instructions_Context.Inst_GetVarValue;
import net.nawaman.curry.Instructions_Core.Inst_NewInstance;
import net.nawaman.curry.Instructions_DataHolder.Inst_GetDHValue;
import net.nawaman.curry.Instructions_StackOwner.Inst_GetAttrValue;
import net.nawaman.curry.Instructions_StackOwner.Inst_GetAttrValueAsType;
import net.nawaman.curry.Instructions_StackOwner.Inst_thisGetAttrValue;
import net.nawaman.curry.compiler.CompileProduct;
import net.nawaman.curry.compiler.Util_Operation;
import net.nawaman.curry.Engine;
import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.Executable;
import net.nawaman.curry.Expression;
import net.nawaman.curry.Location;
import net.nawaman.curry.MExecutable;
import net.nawaman.curry.MType;
import net.nawaman.curry.TKJava;
import net.nawaman.curry.TLParametered;
import net.nawaman.curry.TypeRef;
import net.nawaman.pattern.Instructions_Pattern.Inst_AddOtherAction;
import net.nawaman.pattern.Instructions_Pattern.Inst_AddPostRenderAction;
import net.nawaman.pattern.Instructions_Pattern.Inst_AddPreRenderAction;
import net.nawaman.pattern.Instructions_Pattern.Inst_GetAttrMapPortElement;
import net.nawaman.pattern.Instructions_Pattern.Inst_GetDHValueOrNull;
import net.nawaman.pattern.Instructions_Pattern.Inst_GetMapPortElement;
import net.nawaman.pattern.Instructions_Pattern.Inst_NewWaitedAttributeAsRenderer;
import net.nawaman.regparser.result.Coordinate;
import net.nawaman.regparser.result.ParseResult;
import net.nawaman.regparser.typepackage.PTypePackage;
import net.nawaman.util.UString;

public class Util_Action {
	
	/** An object containing information about the renderer */
	static public class RendererInfo {
		
		static public final RendererInfo[] EmptyRendererInfoArray = new RendererInfo[0];
		
		RendererInfo(Expression $Renderer, boolean  $IsPattern) {
			this.Renderer  = $Renderer;
			this.IsPattern = $IsPattern;
			
		}
		public final Expression Renderer;
		public final boolean    IsPattern;
	}
	
	/** Parse and Compile a RenderInfo */
	static public RendererInfo ParseCompileRenderInfo(ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		Expression Renderer     = (Expression)$Result.valueOf("#Operand", $TPackage, $CProduct);
		boolean    IsPattern    = ($Result.textOf("$IsPattern") != null);
		Expression RendererExpr = null;

		// There is an error
		if(Renderer == null) return null;
		
		Engine $Engine = $CProduct.getEngine();
		
		// These part ensure that the Renderer is a valid expression for accessing a renderer ---------------
		
		// Local variable that is not Transparential DataHolder -----------------------------------
		if(Renderer.isInstruction($Engine, Inst_GetVarValue.Name)) {
			TypeRef VTREf = $CProduct.getReturnTypeRefOf(Renderer);
			if(MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Pattern, VTREf)) {
				RendererExpr = Renderer;
				IsPattern    = true;
			}
		}
		
		else if(CheckIfGetDHValue($Engine, Renderer)
		    && (Renderer.getParam(0) instanceof Expression)
			&& ((Expression)Renderer.getParam(0)).isInstruction($Engine, Inst_GetVarValue.Name)
			&& Util_Compiler.IsVarTransparentialDataHolder((String)((Expression)Renderer.getParam(0)).getParam(0), $CProduct)) {
			RendererExpr = Renderer;
		} else if(
			// Local variable that is Transparential DataHolder
		       CheckIfGetDHValue($Engine, Renderer)
			// Other pattern
			|| Renderer.isInstruction($Engine, Inst_GetAttrValue.Name)
            // Other object - AsType
			|| Renderer.isInstruction($Engine, Inst_GetAttrValueAsType.Name)
		) {
			Object  P0   = Renderer.getParam(0);
			TypeRef TRP0 = $CProduct.getReturnTypeRefOf(P0); 
			if(MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Pattern, TRP0))
				RendererExpr = Renderer;
			else if((P0 instanceof Expression)
			     && (
			             ((Expression)P0).isInstruction($Engine, Inst_GetMapPortElement    .Name)
			          || ((Expression)P0).isInstruction($Engine, Inst_GetAttrMapPortElement.Name)
			    ))
			    RendererExpr = (Expression)P0;
		}
		
		// This
		else if(Renderer.isInstruction($Engine, Inst_thisGetAttrValue.Name)) {
			// Checks if the
			TypeRef ThisTR = $CProduct.getOwnerTypeRef(); 
			if(MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Pattern, ThisTR))
				RendererExpr = Renderer;
		}
		
		// None of the above
		if(RendererExpr == null) {
			$CProduct.reportError(
				"The expression does not seems to be a renderer (Port/Pattern) <Util_BeforeAfter>",
				null, $Result.startPositionOf(0)
			);
			return null;
		}
		// Return it
		return new RendererInfo(RendererExpr, IsPattern);
	}
	
	static boolean CheckIfGetDHValue(
	        final Engine     $Engine,
	        final Expression pExpr) {
	    final boolean aIsGetDHValue = pExpr.isInstruction($Engine, Inst_GetDHValue.Name);
	    if (aIsGetDHValue)
	        return true;
	    
        final boolean aIsGetDHValueOrNull = pExpr.isInstruction($Engine, Inst_GetDHValueOrNull.Name);
        if (aIsGetDHValueOrNull)
            return true;
        
        return false;
	}
	
	/** Parse and Compile a set of RenderInfo */
	static public RendererInfo[] ParseCompileRenderInfos(ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {

		// Get the Renderers
		Object[]             RendererObjs  = $Result.valuesOf("#Renderer", $TPackage, $CProduct);
		Vector<RendererInfo> RendererInfos = new Vector<RendererInfo>();
		for(int i = 0; i < ((RendererObjs == null) ? 0 : RendererObjs.length); i++) {
			Object RendererObj = RendererObjs[i];
			if(RendererObj == null) continue;
			RendererInfos.add((RendererInfo)RendererObj);
		}
		if(RendererInfos.size() == 0) return RendererInfo.EmptyRendererInfoArray;
		return RendererInfos.toArray(new RendererInfo[RendererInfos.size()]);
	}

	/** Extract the super renderer from the renderer expression */
	static RendererInfo ExtractSuperRenderer(CompileProduct $CProduct, Engine $Engine, MExecutable $ME, RendererInfo RendererInfo) {
		if(RendererInfo == null)
			return null;
		
		final Expression Renderer = RendererInfo.Renderer;
        if(Renderer == null)
            return null;
        
        if (RendererInfo.IsPattern) {
            if (!Renderer.isInstruction($Engine, Inst_GetDHValue.Name))
                return null;
            
            Object P0 = Renderer.getParam(0);
            if (!(P0 instanceof Expression) ||
                !Util_Compiler.IsOperandPort(P0, $CProduct))
                return null;
            
            return new RendererInfo((Expression)P0, false);
        } else {
    		if(!Renderer.isInstruction($Engine, Inst_GetAttrValue      .Name) &&
    		   !Renderer.isInstruction($Engine, Inst_GetAttrValueAsType.Name)) {
    			return null;
    		}
    		
    		Object P0 = Renderer.getParam(0);
    		if(!(P0 instanceof Expression) ||
    		   !MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Pattern, $CProduct.getReturnTypeRefOf(P0)))
    			return null;
    		
    		Expression P0Expr = (Expression)P0;
    		if(!P0Expr.isInstruction($Engine, Inst_GetAttrValue      .Name) &&
    		   !P0Expr.isInstruction($Engine, Inst_GetAttrValueAsType.Name) &&
    		   !P0Expr.isInstruction($Engine, Inst_thisGetAttrValue  .Name)) {
    			return null;
    		}
    		
    		return new RendererInfo((Expression)P0, true);
        }
	}
	
	/** Prepare After in each level */
	static Executable PrepareAction_Each(CompileProduct $CProduct, TypeRef ActionTRefSingle, TypeRef ActionTRefMultiple,
			Coordinate CR, Vector<RendererInfo> Renderers, String[] VNames, ExecSignature Signature, Executable ActionBody) {
		
		if((Renderers == null) || (Renderers.size() == 0))
			return ActionBody;

		Engine      $Engine = $CProduct.getEngine();
		MExecutable $ME     = $Engine.getExecutableManager();

		int     Size     = Renderers.size();
		boolean IsSingle = (Size == 1);
		if(Size == 0) return ActionBody;

		Expression ActionExpr = $ME.newExpr(
			CR,
			Inst_NewInstance.Name,
			$ME.newType(
				CR,
				// If there is only one renderer to wait for and it is not a pattern, do it as port
				IsSingle ? ActionTRefSingle : ActionTRefMultiple
			),
			null,
			ActionBody
		);

		Expression Expr = null;
		if(Size == 1) {
			
			Expr = Util_Compiler.NewConfigPortExpr(
				$CProduct,
				Renderers.get(0),
				CR,
				UPattern.CONFIG_NAME_PATTERN_ACTION,
				new Object[] { ActionExpr }
			);
			
			if (Expr == null) {
			    final Expression aOperand     = Renderers.get(0).Renderer;
                final TypeRef    aOperandTRef = $CProduct.getReturnTypeRefOf(aOperand);
                final boolean    aIsPattern   = UPattern.TREF_Pattern.canBeAssignedByInstanceOf($Engine, aOperandTRef);
                if (aIsPattern) {
                    final String   aInstName = Signature.getName().equals("after")
                                                ? Inst_AddPostRenderAction.Name
                                                : Inst_AddPreRenderAction.Name;
                    final Object[] aParams = new Object[3];
                    aParams[0] = null;
                    aParams[1] = ActionBody;
                    aParams[2] = aOperand;
                    Expr = $ME.newExpr(CR, aInstName, aParams);
                }
			}
			
		} else {

			Object[] Params = new Object[1 + Renderers.size()];
			Params[0] = ActionExpr;
			for(int i = 0; i < Renderers.size(); i++) {
				Expression Renderer = Renderers.get(i).Renderer;
				Object     P0;
				Expression P0Expr;
				if(CheckIfGetDHValue($Engine, Renderer) &&
				   ((P0 = Renderer.getParam(0)) instanceof Expression) &&
				   (P0Expr = (Expression)P0).isInstruction($Engine, Inst_GetVarValue.Name) &&
				   (P0Expr.getParam(0) instanceof String) &&
				   Util_Compiler.IsVarTransparentialDataHolder((String)P0Expr.getParam(0), $CProduct)
				)
				Renderer = P0Expr;
				
				// Make an attribute renderer
				boolean IsAsType = false;
				if(           Renderer.isInstruction($Engine, Inst_GetAttrValue.Name) ||
				  (IsAsType = Renderer.isInstruction($Engine, Inst_GetAttrValueAsType.Name))) {
					Object   Pattern =            Renderer.getParam(0);
					Object   AsType  = IsAsType ? Renderer.getParam(1) : null;
					Object   AName   = IsAsType ? Renderer.getParam(2) : Renderer.getParam(1);
					Object[] RParams = new Object[] { Pattern, AsType, AName };
					
					int[] RCR = (Renderer.getCoordinate() < 0) ? null : new int[] { Renderer.getColumn(), Renderer.getLineNumber() };
					
					Renderer = $ME.newExpr(RCR, Inst_NewWaitedAttributeAsRenderer.Name, (Object[])RParams);
				}
				
				Params[i + 1] = Renderer;
			}
			
			Expr = $ME.newExpr(CR, Inst_AddOtherAction.Name, Params);
			
		}
		
		if((Expr != null) && !Expr.ensureParamCorrect($CProduct))
			return null;
		
		return Expr;
	}
	
	/** Prepare After operation */
	static Executable PrepareAction(CompileProduct $CProduct, TypeRef ActionTRefSingle, TypeRef ActionTRefMultiple,
			Coordinate CR, RendererInfo[] RendererInfos, String[] VNames, ExecSignature Signature, Executable ActionExec) {
		
		if((RendererInfos == null) || (RendererInfos.length == 0))
			return ActionExec;

		Engine      $Engine = $CProduct.getEngine();
		MExecutable $ME     = $Engine.getExecutableManager();
		
		Vector<RendererInfo> Renderers = new Vector<RendererInfo>();
		for(RendererInfo RInfo : RendererInfos) {
			//Expression Renderer;
			if((RInfo == null) || ((/*Renderer = */RInfo.Renderer) == null))
				continue;
			
			// TODO - FIX this problem - Now after/before/assert for patterns does not work (only ports).
			//if(!RInfo.IsPattern)
			//    Renderer = Util_Compiler.NewConfigPortExpr($CProduct, Renderer, CR, UPattern.CONFIG_NAME_GETDATA, (Object[])null);
			//if(RInfo.IsPattern)
			//    Renderer = $ME.newExpr(CR, Inst_GetDHValue.Name, new Object[] { Renderer });
			
			Renderers.add(RInfo);
		}
		
		Executable Expr = ActionExec;
		while(Renderers.size() != 0) {
			Expr = PrepareAction_Each($CProduct, ActionTRefSingle, ActionTRefMultiple, CR, Renderers, VNames, Signature, ActionExec);
			if(Expr == ActionExec)
				return Expr;

			Vector<RendererInfo> NewRenderers = new Vector<RendererInfo>();
			for(RendererInfo Renderer : Renderers) {
				if(Renderer == null)
					continue;
				
				Renderer = ExtractSuperRenderer($CProduct, $Engine, $ME, Renderer);
				if ((Renderer          == null)
				 || (Renderer.Renderer == null))
					continue;
				
				NewRenderers.add(Renderer);
			}

			Renderers = NewRenderers;
			if(Renderers.size() != 0) {
			    if (Signature.getName().equals("before")) {
			        Signature = ExecSignature.newSignature(
			                        "after",
			                        Signature.getInterface(),
			                        Signature.getLocation(),
			                        Signature.getExtraData());
			        
			        ActionTRefSingle   = UPattern.TREF_PAPostRendering_Simple;
			        ActionTRefMultiple = UPattern.TREF_Action_PostRender_Simple;
			    }
			    
				ActionExec = (VNames.length == 0) ? Expr : Util_Compiler.GetPatternExecutable($CProduct, VNames, Signature, Expr);
				ActionExec = Util_Compiler.GetWrappedExecutableValue($ME, ActionExec, CR);
			}
		}
		return Expr;
	}

	/** Parse and compile After statement */
	static public Executable ParseCompileActionStatement(
	        final Expression     ValueExpr,
	        final TypeRef        ActionTRefSingle,
	        final TypeRef        ActionTRefMultiple,
	        final String         AName,
	        final TypeRef        ReturnTypeRef,
	        final ParseResult    $Result,
	        final PTypePackage   $TPackage,
	        final CompileProduct $CProduct) {
		
		RendererInfo[] RendererInfos = (RendererInfo[])$Result.valueOf("#Renderers", $TPackage, $CProduct);
		if(RendererInfos.length == 0) {
			if($CProduct.isCompileTimeCheckingFull()) {
				$CProduct.reportWarning(
					String.format(
						"`%s` statement without a valid renderer <Util_BeforeAfter:81>",
						AName
					),
					null,
					$Result.startPositionOf(0)
				);
			}	
			return null;
		}

		Engine      $Engine = $CProduct.getEngine();
		MExecutable $ME     = $Engine.getExecutableManager();
		Coordinate  ValueCR = $Result.coordinateOf("#Body");
		
		try {
			// Prepare the signature
			ExecSignature Signature = ExecSignature.newSignature(
							AName,
			                TypeRef.EmptyTypeRefArray,
			                UString.EmptyStringArray,
			                false,
			                ReturnTypeRef,
			                new Location($CProduct.getCurrentCodeName(), ValueCR.col(), ValueCR.row()),
			                null
			            );
			$CProduct.newMacroScope(Signature);
			
			String[] VNames = Util_Compiler.GetAllUsedLocalVariableNames($CProduct, false, ValueExpr);
			if(VNames == null) VNames = UString.EmptyStringArray;
			
			Coordinate  ZeroCR    = $Result.coordinateOf(0);
			Executable  ValueExec = (VNames.length == 0)
			                            ? ValueExpr
			                            : Util_Compiler.GetPatternExecutable($CProduct, Signature, ValueExpr);

			Expression VExpr = Util_Compiler.GetWrappedExecutableValue($ME, ValueExec, ValueCR);
			
			Executable Exec = PrepareAction($CProduct, ActionTRefSingle, ActionTRefMultiple, ZeroCR, RendererInfos, VNames, Signature, VExpr);
			return Exec;
			
		} finally {
			$CProduct.exitScope();
		}
	}
	
	// Parsing/Compiling -----------------------------------------------------------------------------------------------
	
	/** Compile a cast expression */
	static public Expression CompileAssign(Expression Condition, Expression Operand, String OperandStr,
			String OperatorStr, String ValueStr, Object Value, Coordinate ValueCR, int OperandPos, int ValuePos,
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		
		Engine      $Engine = $CProduct.getEngine(); 
		MExecutable $ME     = $Engine.getExecutableManager();
		Coordinate  ZeroCR  = $Result.coordinateOf(0);
		
		// If a regular (non-port) assignment, delegate to a regular compilation
		if(!Util_Compiler.IsOperandPort(Operand, $CProduct)) {
			if(OperatorStr.equals(":")) {
				$CProduct.reportError(
					"Assignement operator ':=' can only be used with a port <Util_Action:90>.",
					null, OperandPos	
				);
				return null;
			}
			return Util_Operation.CompileAssign(
				Operand, OperandStr, OperatorStr, ValueStr, Value, OperandPos, ValuePos,
				$Result, $TPackage, $CProduct
			);
		}
		// Only these two operators that this method can compile
		if(!OperatorStr.equals("") && !OperatorStr.equals(":")) {
			$CProduct.reportError(
				String.format(
					"Port assignment can only be '=' or ':=': (%s is not support) <Util_Action:102>.",
				OperatorStr),
				null, OperandPos
			);
			return null;
		}

		boolean IsDefault = OperatorStr.equals("");

		if($Result.textOf("$NewOfType") != null) {
			Expression ActionType = $ME.newType(ZeroCR, UPattern.TREF_PAAssignment_New);
			TypeRef    NewTypeRef = (TypeRef)$Result.valueOf("#NewTypeRef", $TPackage, $CProduct);
			Expression ValueExpr  = $ME.newExpr(ZeroCR, Inst_NewInstance .Name, ActionType, null, NewTypeRef);
			return Util_Compiler.NewConfigPortExpr($CProduct, Operand, ZeroCR, UPattern.CONFIG_NAME_PATTERN_ACTION, new Object[] { ValueExpr });
		}

		// Default assignment
		TypeRef RequireTR = $CProduct.getReturnTypeRefOf(Operand);
		TypeRef ValueTR   = $CProduct.getReturnTypeRefOf(Value);
		
		// New
		if($Result.textOf("$New") != null) {
			ValueCR  = $Result.coordinateOf("$New");
			ValuePos = $Result.startPositionOf(       "$New");
			ValueTR  = RequireTR;
			
			boolean    HasParam     = $Result.textOf("#NewParam") != null;
			Object     Param        = HasParam ? $Result.valueOf("#NewParam", $TPackage, $CProduct) : null;
			Expression RequiredType = $ME.newType(ValueCR, RequireTR);
			
			if(HasParam)
				 Value = $ME.newExpr(ValueCR, Inst_NewInstance.Name, RequiredType,  Param);
			else Value = $ME.newExpr(ValueCR, Inst_NewInstance.Name, RequiredType);
		}
		
		// Checks the type compatibility
		if((Value != null) && !MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, RequireTR, ValueTR)) {
			$CProduct.reportError(
				String.format("Incompatible assignment type: '%s' into '%s' <Util_Action:116>.", ValueTR, RequireTR),
				null, ValuePos
			);
			return null;
		}

		// Prepare the value ------------------------------------------------------------
		Executable ValueExec = Util_Compiler.GetPatternExecutableAssignment($CProduct, Value, ValueCR);
		Expression VExpr     = Util_Compiler.GetWrappedExecutableValue($ME, ValueExec, ValueCR);
		// Create the expression
		return Util_Compiler.NewAddActionToPortExpr(
				$CProduct, Operand, ZeroCR, UPattern.TREF_PAAssignment_Simple, ValueCR, null, VExpr, IsDefault
			);
	}

	/** Parse and Compile Assertion block */
	static public Object ParseCompileAssertion(Expression ValueExpr, Coordinate ValueCR,
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {

		TypeRef    ActionTRefSingle   = UPattern.TREF_PAAssertion_Simple;
		TypeRef    ActionTRefMultiple = UPattern.TREF_AAssertion_Simple;
		String     AName              = "Assert";
		Executable Exec = ParseCompileActionStatement(ValueExpr, ActionTRefSingle, ActionTRefMultiple, AName,
				TKJava.TBoolean.getTypeRef(), $Result, $TPackage, $CProduct);
		return Exec;
	}

	/** Parse and Compile Assertion block */
	static public Object ParseCompileAppend(ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {

		Engine      $Engine = $CProduct.getEngine(); 
		MExecutable $ME     = $Engine.getExecutableManager();

		int    OperandIdx = $Result.indexOf        ("#Operand");
		Object Operand    = $Result.valueOf        (OperandIdx, $TPackage, $CProduct);
		int    OperandPos = $Result.startPositionOf(OperandIdx);
		if(!Util_Compiler.EnsureOperandAppendablePort(Operand, OperandPos, $CProduct)) return null;

		Coordinate ZeroCR    = $Result.coordinateOf(0);
		Coordinate ValueCR   = $Result.coordinateOf("#Value");
		Expression ValueExpr = Util_Compiler.CompileValueExpr("#Value", $Result, $TPackage, $CProduct);
		Executable ValueExec = Util_Compiler.GetPatternExecutableAssignment($CProduct, ValueExpr, ValueCR);
		Expression VExpr     = Util_Compiler.GetWrappedExecutableValue($ME, ValueExec, ValueCR);

		// Create the expression
		return Util_Compiler.NewAddActionToPortExpr($CProduct, Operand, ZeroCR, UPattern.TREF_PAAppend_Simple, ValueCR, null, VExpr);
	}
	
	/** Parse and Compile Appendable Sort Action */
	static public Object ParseCompileSortAppendable(ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {

		Engine      $Engine = $CProduct.getEngine(); 
		MExecutable $ME     = $Engine.getExecutableManager();

		int    OperandIdx = $Result.indexOf        ("#Operand");
		Object Operand    = $Result.valueOf        (OperandIdx, $TPackage, $CProduct);
		int    OperandPos = $Result.startPositionOf(OperandIdx);
		if(!Util_Compiler.EnsureOperandAppendablePort(Operand, OperandPos, $CProduct)) return null;

		TypeRef OperTRef = $CProduct.getReturnTypeRefOf(Operand);
		TypeRef DataTRef = ((TLParametered.TRParametered)OperTRef).getParameterTypeRef(0);
		
		// Compile the body -----------------------------------------------------------------------
		Coordinate    ZeroCR  = $Result.coordinateOf(0);
		Coordinate    BodyCR  = $Result.coordinateOf("$BodyStart");
		boolean       IsShort = $Result.textOf("$IsShort") != null;
		boolean       IsHash  = false;
		String[]      PNames;
		TypeRef[]     PTRefs;
		Object[]      Bodies;
		Expression    ValueExpr;
		Executable    ValueExec;
		ExecSignature Signature;
			
		try {
			// Prepare the variables
			String EachName = $Result.textOf("$Each");
			if(IsShort) {				
				// By hash
				IsHash = true;
				PNames = new String[]  { "$$"     };
				PTRefs = new TypeRef[] { DataTRef };
				
			} else if(EachName != null) {
				// By hash
				IsHash = true;
				PNames = new String[]  { EachName };
				PTRefs = new TypeRef[] { DataTRef };
				
			} else {
				// By comparator
				String Each1Name = $Result.textOf("$Each1");
				String Each2Name = $Result.textOf("$Each2");
				PNames = new String[]  { Each1Name, Each2Name };
				PTRefs = new TypeRef[] { DataTRef, DataTRef };
			}

			// Prepare the signature
			Signature = ExecSignature.newSignature(
			                "sort",
			                PTRefs,
			                PNames,
			                false,
			                TKJava.TInteger.getTypeRef(),
			                new Location($CProduct.getCurrentCodeName(), BodyCR.col(), BodyCR.row()),
			                null
			            );
			$CProduct.newMacroScope(Signature);
			
			// Compile the body
			Bodies = $Result.valuesOf("#Statement", $TPackage, $CProduct);

			Expression[] Body = new Expression[Bodies.length];
			for(int i = 0; i < Bodies.length; i++) 
				Body[i] = Expression.toExpr(Bodies[i]);
			
			ValueExpr = $ME.newStack(ZeroCR, Body);
			ValueExec = Util_Compiler.GetPatternExecutable($CProduct, Signature, ValueExpr);
		} finally {
			$CProduct.exitScope();
		}

		TypeRef    ActionTRef = IsHash ? UPattern.TREF_PASortAppendable_Hash_Simple : UPattern.TREF_PASortAppendable_Compare_Simple;
		Expression VExpr      = Util_Compiler.GetWrappedExecutableValue($ME, ValueExec, BodyCR);

		// Create the expression
		return Util_Compiler.NewAddActionToPortExpr($CProduct, Operand, ZeroCR, ActionTRef, BodyCR, null, VExpr);
	}
	
	/** Parse and Compile Appendable Filter Action */
	static public Object ParseCompileFilterAppendable(ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {

		Engine      $Engine = $CProduct.getEngine(); 
		MExecutable $ME     = $Engine.getExecutableManager();

		int    OperandIdx = $Result.indexOf        ("#Operand");
		Object Operand    = $Result.valueOf        (OperandIdx, $TPackage, $CProduct);
		int    OperandPos = $Result.startPositionOf(OperandIdx);
		if(!Util_Compiler.EnsureOperandAppendablePort(Operand, OperandPos, $CProduct)) return null;
		
		TypeRef OperTRef = $CProduct.getReturnTypeRefOf(Operand);
		TypeRef DataTRef = ((TLParametered.TRParametered)OperTRef).getParameterTypeRef(0);
		
		// Compile the body -----------------------------------------------------------------------
		Coordinate    ZeroCR = $Result.coordinateOf(0);
		Coordinate    BodyCR = $Result.coordinateOf("$BodyStart");
		boolean       IsShort = $Result.textOf("$IsShort") != null;
		String[]      PNames;
		TypeRef[]     PTRefs;
		Object[]      Bodies;
		Expression    ValueExpr;
		Executable    ValueExec;
		ExecSignature Signature;
			
		try {
			// Prepare the variables
			String EachName = IsShort ? "$$" : $Result.textOf("$Each");
			PNames = new String[]  { EachName };
			PTRefs = new TypeRef[] { DataTRef };

			// Prepare the signature
			Signature = ExecSignature.newSignature(
			                "filter",
			                PTRefs,
			                PNames,
			                false,
			                TKJava.TBoolean.getTypeRef(),
			                new Location($CProduct.getCurrentCodeName(), BodyCR.col(), BodyCR.row()),
			                null
			            );
			$CProduct.newMacroScope(Signature);
			
			// Compile the body
			Bodies = $Result.valuesOf("#Statement", $TPackage, $CProduct);

			Expression[] Body = new Expression[Bodies.length];
			for(int i = 0; i < Bodies.length; i++) 
				Body[i] = Expression.toExpr(Bodies[i]);
			
			ValueExpr = $ME.newStack(ZeroCR, Body);
			ValueExec = Util_Compiler.GetPatternExecutable($CProduct, Signature, ValueExpr);
		} finally {
			$CProduct.exitScope();
		}

		Expression VExpr = Util_Compiler.GetWrappedExecutableValue($ME, ValueExec, BodyCR);

		// Create the expression
		return Util_Compiler.NewAddActionToPortExpr($CProduct, Operand, ZeroCR, UPattern.TREF_PAFilter_Simple, BodyCR, null, VExpr);
	}

    /** Parse and Compile Assertion block */
    static public Object ParseCompileAssociate(
            final ParseResult    $Result,
            final PTypePackage   $TPackage,
            final CompileProduct $CProduct) {
        
        final Engine      $Engine = $CProduct.getEngine(); 
        final MExecutable $ME     = $Engine.getExecutableManager();
        
        final int    OperandIdx = $Result.indexOf        ("#Operand");
        final Object Operand    = $Result.valueOf        (OperandIdx, $TPackage, $CProduct);
        final int    OperandPos = $Result.startPositionOf(OperandIdx);
        // TODO - Change to Ensure Map Operand
        if(!Util_Compiler.EnsureOperandMapPort(Operand, OperandPos, $CProduct))
            return null;
        
        final Coordinate ZeroCR    = $Result.coordinateOf(0);
        
        final Coordinate KeyCR   = $Result.coordinateOf("#Key");
        final Expression KeyExpr = Util_Compiler.CompileValueExpr("#Key", $Result, $TPackage, $CProduct);
        final Executable KeyExec = Util_Compiler.GetPatternExecutableAssignment($CProduct, KeyExpr, KeyCR);
        final Expression KExpr   = Util_Compiler.GetWrappedExecutableValue($ME, KeyExec, KeyCR);
        
        final Coordinate ValueCR   = $Result.coordinateOf("#Value");
        final Expression ValueExpr = Util_Compiler.CompileValueExpr("#Value", $Result, $TPackage, $CProduct);
        final Executable ValueExec = Util_Compiler.GetPatternExecutableAssignment($CProduct, ValueExpr, ValueCR);
        final Expression VExpr     = Util_Compiler.GetWrappedExecutableValue($ME, ValueExec, ValueCR);
        
        // Create the expression
        return Util_Compiler.NewAddActionToPortExpr(
                $CProduct,
                Operand,
                ZeroCR,
                UPattern.TREF_PAAssociate_Simple,
                ValueCR,
                null,
                KExpr,
                VExpr);
    }

	/** Parse and compile After statement */
	static public Executable ParseCompileBeforeAfter(
	        final boolean        IsBefore,
	        final Expression     ValueExpr,
	        final ParseResult    $Result,
	        final PTypePackage   $TPackage,
	        final CompileProduct $CProduct) {
		TypeRef ActionTRefSingle   = (IsBefore ? UPattern.TREF_PAPreRendering_Simple   : UPattern.TREF_PAPostRendering_Simple  );
		TypeRef ActionTRefMultiple = (IsBefore ? UPattern.TREF_Action_PreRender_Simple : UPattern.TREF_Action_PostRender_Simple);
		String  AName              = (IsBefore ? "before" : "after");
		return ParseCompileActionStatement(
		        ValueExpr,
		        ActionTRefSingle,
		        ActionTRefMultiple,
		        AName,
		        TKJava.TVoid.getTypeRef(),
		        $Result,
		        $TPackage,
		        $CProduct
		    );
	}
}
