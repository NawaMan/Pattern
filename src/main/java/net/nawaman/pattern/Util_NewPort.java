package net.nawaman.pattern;

import java.util.*;

import net.nawaman.curry.*;
import net.nawaman.curry.Instructions_Context.*;
import net.nawaman.curry.Instructions_Core.Inst_NewInstance;
import net.nawaman.curry.Instructions_DataHolder.Inst_ConfigDH;
import net.nawaman.curry.Instructions_DataHolder.Inst_NewDH;
import net.nawaman.curry.Instructions_Executable.Inst_ReCreate;
import net.nawaman.curry.TLParametered.TRParametered;
import net.nawaman.curry.compiler.CompileProduct;
import net.nawaman.curry.compiler.Util_Atomic;
import net.nawaman.curry.util.MoreData;
import net.nawaman.regparser.ParseResult;
import net.nawaman.regparser.typepackage.PTypePackage;

public class Util_NewPort {
	
	static HashMap<Engine, TypeRef> SimpleLists = new HashMap<Engine, TypeRef>();
	static HashMap<Engine, TypeRef> SimpleMaps  = new HashMap<Engine, TypeRef>();
	
	/** Returns the default value */
	static public Object GetDefaultValue(TypeRef TRef, ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		if($Result.textOf("$New") != null) {
			Engine      $Engine = $CProduct.getEngine();
			MExecutable $ME     = $Engine.getExecutableManager();
			ParseResult New     = $Result.subOf("#New");
			Object[]    Params  = (Object[])New.valueOf("#Params", $TPackage, $CProduct);
			
			if((TRef instanceof TRParametered) && (Params.length == 0)) {
				String TRefStr = ((TRParametered)TRef).getTargetTypeRef().toString();
				if(
				 (
				      "pattern~>data=>SimpleList".equals(TRefStr)
				   || "curry=>List".equals(TRefStr)
				 )
				 &&
				 (((TRParametered)TRef).getParameterCount() == 1)
				) {
					TypeRef SimpleListRef = SimpleLists.get($Engine);
					if(SimpleListRef == null) {
						SimpleListRef = new TLPackage.TRPackage("pattern~>data", "SimpleList");
						SimpleLists.put($Engine, SimpleListRef);
					}
					TypeRef DTRef   = ((TRParametered)TRef).getParameterTypeRef(0);
					TypeRef ListRef = new TLParametered.TRParametered(SimpleListRef, DTRef);
					
					int[] NewCR = $Result.locationCROf("$New");
					return $ME.newExpr(NewCR, Inst_NewInstance.Name, $ME.newType(NewCR, ListRef));
					
				} else if("pattern~>data=>SimpleSet".equals(TRefStr)) {
					
				} else if(
		         (
		                "pattern~>data=>SimpleMap".equals(TRefStr)
		             || "curry=>Map".equals(TRefStr)
		         )
		         &&
		         (((TRParametered)TRef).getParameterCount() == 2)
		        ) {
                    TypeRef SimpleMapRef = SimpleMaps.get($Engine);
                    if(SimpleMapRef == null) {
                        SimpleMapRef = new TLPackage.TRPackage("pattern~>data", "SimpleMap");
                        SimpleMaps.put($Engine, SimpleMapRef);
                    }
                    TypeRef KeyTRef   = ((TRParametered)TRef).getParameterTypeRef(0);
                    TypeRef ValueTRef = ((TRParametered)TRef).getParameterTypeRef(1);
                    TypeRef MapRef    = new TLParametered.TRParametered(SimpleMapRef, KeyTRef, ValueTRef);
                    
                    int[] NewCR = $Result.locationCROf("$New");
                    return $ME.newExpr(NewCR, Inst_NewInstance.Name, $ME.newType(NewCR, MapRef));
				}
			}
			
			// New object
			return Util_Atomic.CompileNew(TRef, Params, New, $TPackage, $CProduct);
			
		} else {

			
			Object Value = null;
			$CProduct.newScope(null, TKJava.TAny.getTypeRef());
			try     { Value = $Result.valueOf("#Value", $TPackage, $CProduct); }
			finally { $CProduct.exitScope(); }
			// Other value
			return Value;
		}
	}

	/** Compile a NewVar Statement */
	static public Expression ParseCompileNewPort(
	        final TypeRef        TRef,
	        final PortKind       PKind,
	        final boolean        IsNotDefault,
	        final boolean        IsDependent,
			final String         VarName,
			final int            VNamePos,
			final int[]          TypeCR,
			final int[]          ValueCR,
			final ParseResult    $Result,
			final PTypePackage   $TPackage,
			final CompileProduct $CProduct) {
	    
		// Get the engine
		final Engine       $Engine = $CProduct.getEngine();
		final MExecutable  $ME     = $Engine.getExecutableManager();
		final int          ZeroPos = $Result.posOf(0);
		final int[]        ZeroCR  = $Result.locationCROf(0);
		final TKDataHolder TKDH    = (TKDataHolder)($Engine.getTypeManager().getTypeKind(TKDataHolder.KindName));
		
		// Default PortKind
		final PortKind aPKind = (PKind == null) ? PKSingle.Instance : PKind;
		
		// Local variable
		if($CProduct.isLocalVariableExist(VarName)) {
			String Msg = String.format("The local variable `%s` is already exist.", VarName);
			if($CProduct.isCompileTimeCheckingFull()) { $CProduct.reportError(  Msg, null, VNamePos); return null; }
			else                                        $CProduct.reportWarning(Msg, null, VNamePos);
		}

		if($CProduct.isVariableExist(VarName) && $CProduct.isCompileTimeCheckingFull()) {
			$CProduct.reportWarning(
				String.format("The local variable is hiding another variable `%s`", VarName),
				null, VNamePos
			);
		}

		final boolean IsNew        = $Result.textOf("$New")       != null;
		final boolean IsNewOf      = $Result.textOf("$NewOfType") != null;
		final boolean aIsDependent = IsDependent && !(IsNew || IsNewOf);
		final int[]   aValueCR     = 
                        IsNewOf
                            ? $Result.locationCROf("$NewOfType")
                            : IsNew
                                ? $Result.locationCROf("$New")
                                : ValueCR;
		
		final Object     Value;
		final Expression ValueExpr;
		if(!IsNewOf) {
			Value = GetDefaultValue(TRef, $Result, $TPackage, $CProduct);
			
			Executable ValueExec  = Util_Compiler.GetPatternExecutableAssignment($CProduct, Value, aValueCR);
			Expression VExpr      = Util_Compiler.GetWrappedExecutableValue($ME, ValueExec, aValueCR);
			Expression ActionType = $ME.newType(ZeroCR, UPattern.TREF_PAAssignment_Simple);
			
			ValueExpr = $ME.newExpr(ZeroCR, Inst_NewInstance .Name, ActionType, null, VExpr, !IsNotDefault);
			
		} else {
			Value = null;
			
			Expression ActionType = $ME.newType(ZeroCR, UPattern.TREF_PAAssignment_New);
			TypeRef    NewTypeRef = (TypeRef)$Result.valueOf("#NewTypeRef", $TPackage, $CProduct);
			ValueExpr = $ME.newExpr(ZeroCR, Inst_NewInstance .Name, ActionType, null, NewTypeRef);
		}
		
		Expression PortInfoType = $ME.newType(TypeCR, UPattern.TREF_PortInfo);
		MoreData   MDVarName    = new MoreData("VarName", VarName);
		Expression NewPortInfo;
		
		final boolean aIsMap = (aPKind instanceof PKMap);
		if (aIsMap) {
		    final String aComparator = $Result.textOf("$Comparator");
		    if (aComparator != null)
		        MDVarName.setData(PKMap.MIName_Comparator, aComparator);
		    else if ($Result.textOf("#KeyComparator") != null){
		        final TypeRef    aKeyTRef = TRef.getParameters($Engine)[0];
		        final Executable aExec    = GetKeyComparatorExecutable(aKeyTRef, $Result, $TPackage, $CProduct);
		        MDVarName.setData(PKMap.MIName_Comparator, aExec);
		    }
		}
		
		if($Result.textOf("$IsSet") != null)
			 NewPortInfo = $ME.newExpr(ZeroCR, Inst_NewInstance.Name, PortInfoType, TRef, aPKind, true, null, true, null, MDVarName);
		else NewPortInfo = $ME.newExpr(ZeroCR, Inst_NewInstance.Name, PortInfoType, TRef, aPKind,             true, null, MDVarName);
		
		
		Expression NewPort = $ME.newExpr(ZeroCR, Inst_NewDH.Name, NewPortInfo, ValueExpr);

		String      InstName = Inst_NewConstant.Name;
		Instruction Inst     = $Engine.getInstruction(InstName);
		TypeRef     DHRef    = TKDH.getNoNameTypeRef(TRef, true, true, null);
		Object      DHType   = $ME.newType(TypeCR, DHRef);
		
		Inst.manipulateCompileContextStart    (                                           $CProduct, ZeroPos);
		Inst.manipulateCompileContextBeforeSub(new Object[] { VarName, DHType, NewPort }, $CProduct, ZeroPos);

		Expression Expr = $ME.newExpr(TypeCR, InstName, VarName, DHType, NewPort);

		// Simulate stack
		if(!Expr.manipulateCompileContextFinish($CProduct))
			return null;

		if(!Expr.ensureParamCorrect($CProduct))
			return null;
		
		// Make it a transparential dataholder
		MoreData MD = $CProduct.getVariableMoreData(VarName, true);
		MD.setData(CompileProduct.MDName_IsTransparentDH, true);
		
		// Force this new port render when all the dependent is rendered 
		if(aIsDependent) {
			String[] PNames = Util_Compiler.GetAllUsedLocalVariableNames($CProduct, true, Value);
			if((PNames != null) && (PNames.length != 0)) {
				Expression RenderExpr = $ME.newExpr(
					ZeroCR,
					Inst_ConfigDH.Name,
					$ME.newExpr(ZeroCR, Inst_GetVarValue.Name, VarName),
					UPattern.OPERNAME_RENDER
				);
				
				Executable RenderExec = Util_Compiler.GetPatternExecutable($CProduct, ExecSignature.newEmptySignature("render", null, null), RenderExpr);				
				Expression RExpr;
				if(RenderExec instanceof Expression)
					 RExpr = Expression.newExpr((Expression)RenderExec);
				else RExpr = $ME.newExpr(ZeroCR, Inst_ReCreate.Name, RenderExec);
				
				if(PNames.length == 1) {
					// Single
				    
					// Create PostRendering Simple
					RExpr = $ME.newExpr(ZeroCR, Inst_NewInstance .Name,
						$ME.newType(ZeroCR, UPattern.TREF_PAPostRendering_Simple),
						null, RExpr
					);
					
					// Create the expression
					RExpr = $ME.newExpr(ZeroCR, Inst_ConfigDH.Name,
						$ME.newExpr(ZeroCR,
							Inst_GetVarValue.Name,
							PNames[0]
						),
						UPattern.CONFIG_NAME_PATTERN_ACTION,
						RExpr
					);
					
					if(!RExpr.ensureParamCorrect($CProduct));
					
				} else {
					// Multiple
					Object[] Params = new Object[2 + PNames.length];
					Params[0] = null;	// Condition
					Params[1] = RExpr;	// Action
					for(int i = 0; i < PNames.length; i++)
						Params[i + 2] = $ME.newExpr(ZeroCR, Inst_GetVarValue.Name, PNames[i]);
					
					RExpr = $ME.newExpr(ZeroCR, Instructions_Pattern.Inst_AddPostRenderAction.Name, Params);
					if(!RExpr.ensureParamCorrect($CProduct));
					
				}
				
				Expr = $ME.newGroup(ZeroCR, Expr, RExpr);
			}
		}
		
		return Expr;
	}
	
	static Executable GetKeyComparatorExecutable(
	        final TypeRef        pKeyTRef,
	        final ParseResult    $Result,
	        final PTypePackage   $TPackage,
	        final CompileProduct $CProduct) {
	    
        Engine        $Engine = $CProduct.getEngine(); 
        MExecutable   $ME     = $Engine.getExecutableManager();
        int[]         ZeroCR  = $Result.locationCROf(0);
        int[]         BodyCR  = $Result.locationCROf("$BodyStart");
        
        String[]      PNames;
        TypeRef[]     PTRefs;
        Object[]      Bodies;
        Expression    ValueExpr;
        Executable    ValueExec;
        ExecSignature Signature;
	    
        try {
            // By comparator
            String Each1Name = $Result.textOf("$Each1");
            String Each2Name = $Result.textOf("$Each2");
            PNames = new String[]  { Each1Name, Each2Name };
            PTRefs = new TypeRef[] { pKeyTRef,  pKeyTRef  };
            
            // Prepare the signature
            Signature = ExecSignature.newSignature(
                            "sort",
                            PTRefs,
                            PNames,
                            false,
                            TKJava.TInteger.getTypeRef(),
                            new Location($CProduct.getCurrentCodeName(), BodyCR[0], BodyCR[1]),
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
	    
	    return ValueExec;
	}
}
