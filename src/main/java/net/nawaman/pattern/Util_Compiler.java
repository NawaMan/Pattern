package net.nawaman.pattern;

import java.util.HashSet;
import java.util.Vector;

import net.nawaman.curry.CurryExecutable.*;
import net.nawaman.curry.*;
import net.nawaman.curry.Instructions_Context.*;
import net.nawaman.curry.Instructions_ControlFlow.*;
import net.nawaman.curry.Instructions_ControlFlow.Inst_Switch.*;
import net.nawaman.curry.Instructions_Core.*;
import net.nawaman.curry.Instructions_DataHolder.*;
import net.nawaman.curry.Instructions_Executable.*;
import net.nawaman.curry.Instructions_ForSpeed.*;
import net.nawaman.curry.Instructions_StackOwner.*;
import net.nawaman.curry.compiler.*;
import net.nawaman.curry.extra.text.*;
import net.nawaman.curry.extra.text.EE_Text.*;
import net.nawaman.curry.util.*;
import net.nawaman.pattern.Instructions_Pattern.*;
import net.nawaman.pattern.Util_Action.RendererInfo;
import net.nawaman.regparser.*;
import net.nawaman.regparser.result.Coordinate;
import net.nawaman.regparser.result.ParseResult;
import net.nawaman.regparser.typepackage.*;
import net.nawaman.util.*;

public class Util_Compiler {

	/** RegPaser for parsing instruction */
	static RegParser InstPattern = RegParser.compile("($Pre:~(^get)*~)get($Mid:~(^Value)*~)Value($Post:~.*~)");

	
	static public Expression CompileValueExpr(String pName,
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		
		Expression ValueExpr;
		try {
			$CProduct.newScope(null, TKJava.TAny.getTypeRef());
			ValueExpr = Expression.toExpr($Result.valueOf(pName, $TPackage, $CProduct));
		} finally {
			$CProduct.exitScope();
		}
		return ValueExpr;
	}
	
	
	/** Checks if the variable of the given name is transparential data-holder */
	static boolean IsVarTransparentialDataHolder(String VName, CompileProduct $CProduct) {
		if(VName == null) return false;
		
		MoreData MD = $CProduct.getVariableMoreData(VName, false);
		return ((MD != null) && Boolean.TRUE.equals(MD.getData(CompileProduct.MDName_IsTransparentDH)));
	}

	/** Checks if the operand is a port */
	static Boolean IsOperandPort(Object Operand, CompileProduct $CProduct) {
		if(Operand == null)         return null;
		if(Operand instanceof Port) return true;
		
		Engine $Engine = $CProduct.getEngine();

		// It is literally a port
		TypeRef OprdTRef = $CProduct.getReturnTypeRefOf(Operand);
		if(MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Port, OprdTRef))
			return true;
		
		// Other literal that is not an expression
		if(!(Operand instanceof Expression))
			return false;
		
		Expression OprdExpr = (Expression)Operand;
		String     InstName = OprdExpr.getInstructionName($Engine);
		if(InstName == null)	// Data and Expr with Port as data will match TREF_PORT, so we can safely return false. 
			return false;
		
		if (Inst_GetMapPortElement.Name.equals(InstName))
		    return true;
        if (Inst_GetAttrMapPortElement.Name.equals(InstName))
            return true;
		
		// Get value of the Transparential DataHold
		if(Util_Action.CheckIfGetDHValue($Engine, OprdExpr))
			return IsOperandPort(OprdExpr.getParam(0), $CProduct);
		
		// Local variable - (+$)
		if(Inst_GetVarValue.Name.equals(InstName)) {
			Object VName = OprdExpr.getParam(0);
			if(!(VName instanceof String)) return null;
			
			if(IsVarTransparentialDataHolder((String)VName, $CProduct))
				return true;
			
			if("this".equals(VName) || Context.StackOwner_VarName.equals(VName)) {
				TypeRef OwnerTRef = $CProduct.getOwnerTypeRef();
				if(MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Pattern, OwnerTRef))
					return true;
			}
			
			return false;
		}
		
		// StackOwner - (~,+$)                     || StackOwner(AsType) - (~,!,+$)
		if(Inst_GetAttrValue.Name.equals(InstName) || Inst_GetAttrValueAsType.Name.equals(InstName)) {
			Object  Obj     = OprdExpr.getParam(0);
			TypeRef ObjTRef = $CProduct.getReturnTypeRefOf(Obj);
			if(MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Pattern, ObjTRef))
				return true;
			
			return IsOperandPort(Obj, $CProduct);
		}
		
		// this.<...> - (+$)
		if(Inst_thisGetAttrValue.Name.equals(InstName)) {
			TypeRef OwnerTRef = $CProduct.getOwnerTypeRef();
			if(MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Pattern, OwnerTRef))
				return true;
		}
		
		return false;
	}

	/** Checks if the operand is a port */
	static boolean EnsureOperandPort(Object Operand, int OperandPos, CompileProduct $CProduct) {
		Boolean IsOperandPort = IsOperandPort(Operand, $CProduct);

		// If not even a port
		if(Boolean.TRUE.equals(IsOperandPort))
			return true;
		
		// Report the error
		$CProduct.reportError(
			String.format(
				"The compiler is unable to handle the operand '%s' (it does not seemd to be a port) <Util_Compiler:135>.",
				((Operand instanceof Expression) ? ((Expression)Operand).toDetail($CProduct.getEngine()) : Operand)
			),
			null,
			OperandPos
		);
		return false;
	}
	
	/** Returns the Instruction name for config from the get data instruction name */
	static String GetConfigExprName(CompileProduct $CProduct, Engine $Engine, Expression Expr, boolean $IsNoError) {
		
		String InstConfigName = TryConfigExprName($CProduct, $Engine, Expr);
		if(InstConfigName != null)
			return InstConfigName;
		
		boolean aIsGetMapPortElement     = Expr.isInstruction($Engine, Inst_GetMapPortElement    .Name);
        boolean aIsGetAttrMapPortElement = Expr.isInstruction($Engine, Inst_GetAttrMapPortElement.Name);
		if (aIsGetMapPortElement || aIsGetAttrMapPortElement)
		    return null;
		
		if ($IsNoError)
		    return null;
		
		// Report the error
		$CProduct.reportError(
			String.format(
				"The compiler is unable to handle the operand '%s' (it does not seemd to be a port) <Util_Compiler:154>.",
				Expr.toDetail($Engine)
			),
			null,
			$CProduct.getPosition(Expr)
		);
		return null;
	}
	/** Returns the Instruction name for config from the get data instruction name */
	static String TryConfigExprName(CompileProduct $CProduct, Engine $Engine, Expression Expr) {
		String InstName = Expr.getInstructionName($Engine);
		if(Inst_GetVarValue.Name.equals(InstName))
			return null;
		
		if (Inst_GetDHValueOrNull.Name.equals(InstName))
		    InstName = Inst_GetDHValue.Name;
		
		ParseResult PResult = Util_Compiler.InstPattern.parse(InstName);
		if(PResult == null) return null;
		
		// Use index because it faster - just have update it accordingly if the RegParse changed
		String Pre  = PResult.textOf(0);
		String Mid  = PResult.textOf(2);
		String Post = PResult.textOf(4);
		
		// Construct and ensure it exist
		String InstConfigName = Pre + "config" + Mid + Post;
		if($Engine.getInstruction(InstConfigName) == null)
			return null;
			
		return InstConfigName;
	}

	/** Creates and return a new config expression for the operand */
	static public Expression NewConfigPortExpr(
	        final CompileProduct $CProduct,
	              Object         Operand,
	        final Coordinate     CR,
	        final String         ConfigName,
	        Object[] Params) {
		if(Operand == null) return null;
		
		// Default value
		if(Params == null) Params = UObject.EmptyObjectArray;

		Engine      $Engine = $CProduct.getEngine();
		MExecutable $ME     = $Engine.getExecutableManager();
		
		if(!(Operand instanceof Expression)) {
            if (Operand instanceof RendererInfo) {
                if (((RendererInfo)Operand).IsPattern)
                    return null;
                Operand = ((RendererInfo)Operand).Renderer;
            }
            
            // It is literally a port
            TypeRef OprdTRef = $CProduct.getReturnTypeRefOf(Operand);
            if(MType.CanTypeRefByAssignableByInstanceOf(null, $Engine, UPattern.TREF_Port, OprdTRef)) {
                Expression Expr = $ME.newExpr(CR, Inst_ConfigDH.Name, Operand, Params);
                return Expr.ensureParamCorrect($CProduct) ? Expr : null;
            }
            
            if(!(Operand instanceof Expression))
                return null;
		}
		
		if(!Boolean.TRUE.equals(IsOperandPort(Operand, $CProduct)))
		    return null;
		
		String InstName = GetConfigExprName($CProduct, $Engine, (Expression)Operand, true);
		if(InstName == null) {
		    Expression aOperExpr = (Expression)Operand;
		    /*
	        boolean aIsExpr_GetMapPortElement     = aOperExpr.isInstruction($Engine, Inst_GetMapPortElement    .Name);
	        boolean aIsExpr_GetAttrMapPortElement = aOperExpr.isInstruction($Engine, Inst_GetAttrMapPortElement.Name);
	        if (aIsExpr_GetMapPortElement || aIsExpr_GetAttrMapPortElement) {*/
    	        // +1 for Config name, +1 for DH
    	        Object[] aConfigParams = new Object[Params.length + 1 + 1];
    	        aConfigParams[0] = aOperExpr;
    	        aConfigParams[1] = ConfigName;
    	        System.arraycopy(Params, 0, aConfigParams, 2, Params.length);
    	        
    	        Expression aExpr = $ME.newExpr(CR, Inst_ConfigDH.Name, aConfigParams);
    		    return aExpr;
	        /*}
	        
	        return null;*/
		}

		int      PCount       = ((Expression)Operand).getParamCount();
		Object[] ConfigParams = new Object[PCount + Params.length + 1];
		
		// Parameter of the Operand
		for(int i = PCount; --i >= 0; ) ConfigParams[i] = ((Expression)Operand).getParam(i);
		
		// The Config name
		ConfigParams[PCount] = ConfigName;
		
		// The Config parameter
		System.arraycopy(Params, 0, ConfigParams, PCount + 1, Params.length);

		// Create the expression
		Expression Expr = $ME.newExpr(CR, InstName, ConfigParams);
		return Expr.ensureParamCorrect($CProduct) ? Expr : null;
	}
	
	
	/** Creates and return a new config expression for the operand */
	static Expression NewAddActionToPortExpr(CompileProduct $CProduct, Object Operand, int[] ZeroCR, TypeRef ActionTRef,
			int[] ValueCR, Expression Condition, Expression Value, Object ... ExtraParams) {
		return NewAddActionToPortExpr($CProduct, Operand, Coordinate.of(ZeroCR), ActionTRef, ValueCR, Condition, Value, ExtraParams);
	}
	/** Creates and return a new config expression for the operand */
	static Expression NewAddActionToPortExpr(CompileProduct $CProduct, Object Operand, Coordinate ZeroCR, TypeRef ActionTRef,
			Coordinate ValueCR, Expression Condition, Expression Value, Object ... ExtraParams) {
		Engine      $Engine = $CProduct.getEngine();
		MExecutable $ME     = $Engine.getExecutableManager();

		// Prepare parameter
		int EPCount = (ExtraParams == null) ? 0 : ExtraParams.length;
		Object[] Params = new Object[EPCount + 3];
		Params[0] = $ME.newType(ValueCR, ActionTRef);	// Type
		Params[1] = Condition;	                        // Condition
		Params[2] = Value;		                        // Action
		if(ExtraParams != null) System.arraycopy(ExtraParams, 0, Params, 3, EPCount);
		
		// Create the newInstance of the action type
		Expression VExpr = $ME.newExpr(ValueCR, Inst_NewInstance.Name, (Object[])Params);
		if(!VExpr.ensureParamCorrect($CProduct)) return null;

		// Create the expression
		return Util_Compiler.NewConfigPortExpr($CProduct, Operand, ZeroCR, UPattern.CONFIG_NAME_PATTERN_ACTION, new Object[] { VExpr });
	}
	/** Creates and return a new config expression for the operand */
	static Expression NewAddActionToPortExpr(CompileProduct $CProduct, Object Operand, Coordinate ZeroCR, TypeRef ActionTRef,
			int[] ValueCR, Expression Condition, Expression Value, Object ... ExtraParams) {
		Engine      $Engine = $CProduct.getEngine();
		MExecutable $ME     = $Engine.getExecutableManager();

		// Prepare parameter
		int EPCount = (ExtraParams == null) ? 0 : ExtraParams.length;
		Object[] Params = new Object[EPCount + 3];
		Params[0] = $ME.newType(ValueCR, ActionTRef);	// Type
		Params[1] = Condition;	                        // Condition
		Params[2] = Value;		                        // Action
		if(ExtraParams != null) System.arraycopy(ExtraParams, 0, Params, 3, EPCount);
		
		// Create the newInstance of the action type
		Expression VExpr = $ME.newExpr(ValueCR, Inst_NewInstance.Name, (Object[])Params);
		if(!VExpr.ensureParamCorrect($CProduct)) return null;

		// Create the expression
		return Util_Compiler.NewConfigPortExpr($CProduct, Operand, ZeroCR, UPattern.CONFIG_NAME_PATTERN_ACTION, new Object[] { VExpr });
	}

	/** Extracts all local variable names used within the Expr */
	static public String[] GetAllUsedLocalVariableNames(CompileProduct $CProduct, boolean IsOnlyPort, Object Obj) {
		return GetAllUsedLocalVariableNames($CProduct, false, IsOnlyPort, Obj);
	}

	/** Extracts all local variable names used within the Expr */
	static public String[] GetAllUsedLocalVariableNames(CompileProduct $CProduct, boolean IsOnlyImmediate,
			boolean IsOnlyPort, Object Obj) {
		HashSet<String> Names = new HashSet<String>();
		ExtractAllUsedLocalVariableNames($CProduct, IsOnlyImmediate, IsOnlyPort, Obj, Names);
		
		if($CProduct.getOwnerTypeRef() != null)
			Names.add("this");
		
		return Names.toArray(new String[Names.size()]);
	}
	
	static final int GVVHash = -(UObject.hash(Inst_GetVarValue.Name) + 1);
	static final int SVVHash = -(UObject.hash(Inst_SetVarValue.Name) + 1);
	
	/** Returns an array containing names of local variable that are port */
	static public String[] GetLocalPatternNames(CompileProduct $CProduct, String[] VNames) {
		if((VNames == null) || (VNames.length == 0)) return VNames;
		Vector<String> VNs = new Vector<String>();
		
		// Checks each var if it is a transparentDH
		for(String VName : VNames) {
			if(!IsVarTransparentialDataHolder(VName, $CProduct))
				continue;
			
			VNs.add(VName);
		}
		
		return VNs.toArray(new String[VNs.size()]);
	}
	
	/** Extracts all local variable names used within the Exec */
	static void ExtractAllUsedLocalVariableNames(CompileProduct $CProduct, Executable Exec, HashSet<String> Names) {
		if(Exec == null) return;
			
		int FCount = Exec.getFrozenVariableCount();
		for(int i = FCount; --i >= 0; )
			Names.add(Exec.getFrozenVariableName(i));
	}

	/** Returns the executable of the closest the O */
	static Executable GetExecutableFromObject(Object O) {
		Expression Expr = null;
		// Screen for Expression that is not Data or Expr
		while(true) {
			if(Expr == null) {
				if(!(O instanceof Expression)) {
					if(O == null)
						return null;
					
					if(!(O instanceof Executable))
						return null;
					
					return (Executable)O;
				}
				Expr = (Expression)O;
				O    = null;
			}
			if(Expr.isExpr()) {
				Expr = Expr.getExpr();
				continue;
			}
			if(Expr.isData()) {
				O    = Expr.getData();
				Expr = null;
				continue;
			} 
			break;
		}
		return Expr;
	}
	
	/** Returns the local immediete ports' name of the given object */
	static void ExtractAllUsedLocalVariableNames(CompileProduct $CProduct, boolean IsOnlyImmediate, boolean IsOnlyPort,
			Object Obj, HashSet<String> Names) {
		
		Executable Exec = GetExecutableFromObject(Obj);
		if(!(Exec instanceof Expression)) {
			ExtractAllUsedLocalVariableNames($CProduct, Exec, Names);
			return;
		}
			
		Expression Expr = (Expression)Exec;
		
		Engine      $Engine = $CProduct.getEngine();
		Instruction Inst    = Expr.getInstruction($Engine);
		
		// Detect local variable access -------------------------------------------------
		
		int hInst = Inst.getNameHash();
		if((GVVHash == hInst) || (SVVHash == hInst)) {
			Object OName = Expr.getParam(0);
			if(OName instanceof String)
				Names.add((String)OName);
		}

		// Two special cases ------------------------------------------------------------
		
		//  Do the choose/switch
		if(Inst instanceof Inst_Switch) {
			Object ObjCEs = Expr.getParam(2);
			if(ObjCEs instanceof CaseEntry[]) {
				CaseEntry[] CEs = (CaseEntry[])ObjCEs;
				for(CaseEntry CE : CEs) {
					ExtractAllUsedLocalVariableNames($CProduct, false, IsOnlyPort, CE.getCaseValue(), Names);
					
					if(!IsOnlyImmediate)
						ExtractAllUsedLocalVariableNames($CProduct, false, IsOnlyPort, CE.getCaseBody(), Names);
				}
				
			}
		}
		
		// Normal -----------------------------------------------------------------------
		
		// Look throug the paramters
		int PCount = Expr.getParamCount();
		for(int i = PCount; --i >= 0; ) {
			String[] VNames = GetAllUsedLocalVariableNames($CProduct, false, IsOnlyPort, Expr.getParam(i));
			String[] PNames = IsOnlyPort ? GetLocalPatternNames($CProduct, VNames) : VNames;
			if((PNames == null) || (PNames.length == 0)) continue;
			
			for(String Name : PNames) {
				if(Name == null)
					continue;
				Names.add(Name);
			}
		}
		
		// See if we should check the sub-expression
		if(IsOnlyImmediate &&
		   !(Inst instanceof Inst_AbstractCutShort) &&
		   !(Inst instanceof Inst_Group) &&
		   !(Inst instanceof Inst_RunOnce)
		) return;
		
		int SCount = Expr.getSubExprCount();
		for(int i = SCount; --i >= 0; ) {
			String[] VNames = GetAllUsedLocalVariableNames($CProduct, false, IsOnlyPort, Expr.getSubExpr(i));
			String[] PNames = IsOnlyPort ? GetLocalPatternNames($CProduct, VNames) : VNames;
			if((PNames == null) || (PNames.length == 0)) continue;
			
			for(String Name : PNames) {
				if(Name == null)
					continue;
				Names.add(Name);
			}
		}
	}
	
	/** Create an assignment executable for Pattern assignment */
	static public Executable GetPatternExecutable(CompileProduct $CProduct, ExecSignature Signature, Object Value) {
		// It was not an executable (therefore not an expression), make it one and return
		if(!(Value instanceof Executable))
			Value = Expression.toExpr(Value);
		
		Expression ValueExpr = (Expression)Value;
		String[]   VarNames  = GetAllUsedLocalVariableNames($CProduct, false, ValueExpr);
		
		return GetPatternExecutable($CProduct, VarNames, Signature, Value);
	}

	/** Create an assignment executable for Pattern assignment */
	static public Executable GetPatternExecutable(CompileProduct $CProduct, String[] VarNames, ExecSignature Signature,
			Object Value) {
		
		// It was not an executable (therefore not an expression), make it one and return
		if(!(Value instanceof Executable))
			Value = Expression.toExpr(Value);

		Expression ValueExpr = (Expression)Value;
		if(VarNames == null) VarNames = UString.EmptyStringArray;

		// Prepare the frozen scope
		Scope FrozenScope = new Scope();
		FrozenLoop: for(int i = 0; i < VarNames.length; i++) {
			String VName = VarNames[i];
			if(VName == null)
				continue;
			
			// If the name is the same with the signature parameters then skip
			for(int p = 0; p < Signature.getParamCount(); p++) {
				if(VName.equals(Signature.getParamName(p))) {
					VarNames[i] = null;
					continue FrozenLoop;
				}
			}
			
			// Prepare the frozen scope
			TypeRef VTRef = $CProduct.getVariableTypeRef(VName);
			if(VTRef == null) {
				VarNames[i] = null;
				continue;
			}
			
			Type VType = $CProduct.getTypeAtCompileTime(VTRef);
			FrozenScope.newConstant(VName, VType, null);
		}
		
		return new CurrySubRoutine($CProduct.getEngine(), Signature, ValueExpr, VarNames, FrozenScope);
	}
	
	/** Create an assignment executable for Pattern assignment */
	static public Executable GetPatternExecutableAssignment(CompileProduct $CProduct, Object Value, Coordinate CR) {
		// It was not an executable (therefore not an expression), make it one and return
		if(!(Value instanceof Executable))
			Value = Expression.toExpr(Value);
		
		Expression ValueExpr = (Expression)Value;
		
		// Make it a SubRoutine to freeze up variables if it needed
		int R = -1; int C = -1;
		if(CR != null) { R = Coordinate.rowOf(CR); C = Coordinate.colOf(CR); }
		// Prepare the signature
		ExecSignature Signature = ExecSignature.newProcedureSignature(
				"set",
				$CProduct.getReturnTypeRefOf(ValueExpr),
				new Location($CProduct.getCurrentCodeName(), C, R),
				null
				);
		
		return GetPatternExecutable($CProduct, Signature, ValueExpr);
	}
	
	/** Create an assignment executable for Pattern assignment */
	static public Executable GetPatternExecutableAssignment(CompileProduct $CProduct, Object Value, int[] CR) {
		// It was not an executable (therefore not an expression), make it one and return
		if(!(Value instanceof Executable))
			Value = Expression.toExpr(Value);
		
		Expression ValueExpr = (Expression)Value;

		// Make it a SubRoutine to freeze up variables if it needed
		int R = -1; int C = -1;
		if(CR != null) { R = CR[1]; C = CR[0]; }
		// Prepare the signature
		ExecSignature Signature = ExecSignature.newProcedureSignature(
		                              "set",
		                              $CProduct.getReturnTypeRefOf(ValueExpr),
		                              new Location($CProduct.getCurrentCodeName(), C, R),
		                              null
		                          );
		
		return GetPatternExecutable($CProduct, Signature, ValueExpr);
	}
	
	/** Returns a wrapped expression of the given executable */
	static Expression GetWrappedExecutableValue(MExecutable $ME, Executable ValueExec, Coordinate CR) {
		if(ValueExec instanceof Expression)
			 return Expression.newExpr((Expression)ValueExec);
		else return $ME.newExpr(CR, Inst_ReCreate.Name, ValueExec);
	}
	
	/** Returns a wrapped expression of the given executable */
	static Expression GetWrappedExecutableValue(MExecutable $ME, Executable ValueExec, int[] CR) {
		if(ValueExec instanceof Expression)
			 return Expression.newExpr((Expression)ValueExec);
		else return $ME.newExpr(CR, Inst_ReCreate.Name, ValueExec);
	}

	/** Checks if the port is appendable */
	static boolean EnsureOperandAppendablePort(Object Operand, int OperandPos, CompileProduct $CProduct) {
		// Ensure that it is a port
		if(!Util_Compiler.EnsureOperandPort(Operand, OperandPos, $CProduct))
			return false;
		
		if(IsOperandAppendablePort(Operand, $CProduct))
			return true;
		
		IsOperandAppendablePort(Operand, $CProduct);
		
		// Report Error
		$CProduct.reportError(
			String.format(
				"Only appendable port can be appended: '%s' <Util_Action:426>",
				$CProduct.getReturnTypeRefOf(Operand)
			),
			null,
			OperandPos
		);
		return false;
	}
	
	/** Checks if the port is appendable */
	static boolean IsOperandAppendablePort(Object Operand, CompileProduct $CProduct) {
		// TODO
		// NOTE: At the moment we check its type but this can be inaccurate
		// The best way is to try to get PortInfo and see its PortKind but that is quite a job so for now we assume it
		// is enough
		
		TypeRef OperTRef = $CProduct.getReturnTypeRefOf(Operand);
		
		if (!(OperTRef instanceof TLParametered.TRParametered))
		    return false;
		
		String aToString = OperTRef.toString();
		return aToString.startsWith("curry=>List<")
		    || aToString.startsWith("pattern~>data=>SimpleList<");
	}

    /** Checks if the port is map */
    static boolean EnsureOperandMapPort(Object Operand, int OperandPos, CompileProduct $CProduct) {
        // Ensure that it is a port
        if(!Util_Compiler.EnsureOperandPort(Operand, OperandPos, $CProduct))
            return false;
        
        if(IsOperandMapPort(Operand, $CProduct))
            return true;
        
        // Report Error
        $CProduct.reportError(
            String.format(
                "Only map port can be associated: '%s' <Util_Compiler:548>",
                $CProduct.getReturnTypeRefOf(Operand)
            ),
            null,
            OperandPos
        );
        return false;
    }
    
    /** Checks if the port is map */
    static boolean IsOperandMapPort(Object Operand, CompileProduct $CProduct) {
        // TODO
        // NOTE: At the moment we check its type but this can be inaccurate
        // The best way is to try to get PortInfo and see its PortKind but that is quite a job
        //   so for now we assume it is enough
        
        TypeRef OperTRef = $CProduct.getReturnTypeRefOf(Operand);
        
        if (!(OperTRef instanceof TLParametered.TRParametered))
            return false;
        
        String aToString = OperTRef.toString();
        return aToString.startsWith("curry=>Map<")
            || aToString.startsWith("pattern~>data=>SimpleMap<");
    }
	
	static public Object ParseCompileLoopText(ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		
		// Get the engine
		Engine      $Engine  = $CProduct.getEngine();
		MExecutable $ME      = $Engine.getExecutableManager();
		int         Position = $Result.startPositionOf(0);

		// Before ------------------------------------------------------------------------------------------------------
		Instruction Inst = $Engine.getInstruction("forEach");
		// Manipulate the context - Before
		Inst.manipulateCompileContextStart($CProduct, Position);

		// Parameters (check ourself for better error report) ----------------------------------------------------------
		String  Name       = "$$";
		Object  Collection = $Result.valueOf("#Collection", $TPackage, $CProduct);
		
		Inst_ForEach FEInst = (Inst_ForEach)$Engine.getInstruction(Inst_ForEach.Name);
		TypeRef      TRef   = FEInst.getContainTypeRef($CProduct, $CProduct.getReturnTypeRefOf(Collection), Position);

		Object[] Params = new Object[] { null, Name, $ME.newType($Result.coordinateOf("#Collection"), TRef), Collection };
		// Manipulate the context before sub
		Inst.manipulateCompileContextBeforeSub(Params, $CProduct, Position);

		Expression Body = null;
		Coordinate CR   = $Result.coordinateOf("#Each");
		try {
			$CProduct.newScope(null, EE_Text.TREF_Text);
			$CProduct.newVariable(Name, TRef);

			// Echo
			Body = $ME.newExpr(
				CR,
				Inst_EchoText.Name,
				$Result.valueOf("#Each", $TPackage, $CProduct)
			);
			
			Expression[] Subs = null;
			if($Result.textOf("#Separator") == null) Subs = new Expression[] { Body };
			else {
				// Unless zero, echo Separator
				Subs = new Expression[] { 
					$ME.newExprSub(
						Inst_If.Names[1],
						new Object[] {
							$ME.newExpr(
								Inst_IsZero.Name,
								$ME.newExpr(
									Inst_GetVarValue.Name,
									Inst_AbstractLoop.LoopCountName
								)
							)
						},
						new Expression[] {
							$ME.newExpr(
								$Result.coordinateOf("#Separator"),
								Inst_EchoText.Name,
								$Result.valueOf("#Separator", $TPackage, $CProduct)
							)
						}
					),
					Body
				};
			}
			
			Body = $ME.newGroup(CR, Subs);
			
		} finally {
			$CProduct.exitScope();
		}

		// Body ------------------------------------------------------------------------------------------------------------
		Expression Expr = $ME.newExprSub($Result.coordinateOf(0), "forEach", Params, Body);
		if(!Expr.ensureParamCorrect($CProduct) || !Expr.manipulateCompileContextFinish($CProduct)) return null;
		
		Expr = $ME.newExpr(CR, Inst_CreateText.Name, Expression.newExpr($ME.newGroup(CR,  Expr, null)));
		if(!Expr.ensureParamCorrect($CProduct) || !Expr.manipulateCompileContextFinish($CProduct)) return null;
		
		return Expr;
	}
}