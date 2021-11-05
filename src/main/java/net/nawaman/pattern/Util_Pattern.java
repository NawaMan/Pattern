 package net.nawaman.pattern;

import java.io.Serializable;
import java.util.Vector;

import net.nawaman.curry.Accessibility;
import net.nawaman.curry.CurryError;
import net.nawaman.curry.Documentation;
import net.nawaman.curry.Engine;
import net.nawaman.curry.ExecInterface;
import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.Executable;
import net.nawaman.curry.Location;
import net.nawaman.curry.MExecutable;
import net.nawaman.curry.StackOwnerBuilder;
import net.nawaman.curry.TLPackage;
import net.nawaman.curry.TLParametered;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.Executable.ExecKind;
import net.nawaman.curry.Instructions_Core.Inst_NewInstance;
import net.nawaman.curry.compiler.CompileProduct;
import net.nawaman.curry.compiler.ElementResolver;
import net.nawaman.curry.compiler.FileCompileResult;
import net.nawaman.curry.compiler.StackOwnerAppender;
import net.nawaman.curry.compiler.TypeSpecCreator;
import net.nawaman.curry.compiler.Util_Curry;
import net.nawaman.curry.compiler.Util_Element;
import net.nawaman.curry.compiler.Util_ElementResolver;
import net.nawaman.curry.compiler.Util_TypeDef;
import net.nawaman.curry.compiler.Util_TypeElement;
import net.nawaman.curry.compiler.FileCompileResult.PackageElement;
import net.nawaman.curry.compiler.FileCompileResult.TypeRegistration;
import net.nawaman.curry.compiler.FileCompileResult.StructuralRegistration;
import net.nawaman.curry.compiler.StackOwnerAppender.AttrAppender;
import net.nawaman.curry.compiler.StackOwnerAppender.OperAppender;
import net.nawaman.curry.compiler.Util_File;
import net.nawaman.curry.util.MoreData;
import net.nawaman.regparser.PTypeProvider;
import net.nawaman.regparser.result.ParseResult;
import net.nawaman.regparser.typepackage.PTypePackage;
import net.nawaman.util.UArray;
import net.nawaman.util.UObject;

public class Util_Pattern {

	static public final String enCONSTRUCTOR = "#Constructor";
	static public final String enOPERATION   = "#Operation";
	static public final String enATTRIBUTE   = "#Attribute";
	
	static public final String enCONSTANT = "$Constant";
	static public final String enINPORT   = "$InPort";
	static public final String enPORT     = "$Port";

	static public final String enDOCUMENT = "#Documentation";
	static public final String enACCESS   = "#Acc";
	
	static public final String enINHERITREF  = "#InheritTypeRef";
	static public final String enIMPLEMENTED = "#Implemented";
	
	static public final String enNAME = "$Name";
	static public final String enTYPE = "#Type";
	
	static public final String enIS_COLLECTION = "$IsCollection";
	static public final String enIS_MAP        = "$IsMap";
	
	static public final String enIS_NOT_DEFAULT = "$IsNotDefault";
	static public final String enIS_DEPENDENT   = "$IsDependent";
	static public final String enDEFAULT_VALUE  = "#DefaultValue";

	static public final String enPATTERN_TYPE = "#Pattern";
	
	static public final String enFlag = "#Flag";
	
	static public final String enKEYTYPE   = "#KeyTypeRef";
	static public final String enVALUETYPE = "#ValueTypeRef";
	
	static final java.util.Random Random = new java.util.Random();

	/** Registers the types */
	static public FileCompileResult.TypeRegistration RegisterTypes(String $PackageName,
			final ParseResult $Result, final PTypePackage $TPackage, final CompileProduct $CProduct) {

		final String   ID = "PatternID"+Random.nextInt();
		final String[] $Imports = Util_File.ExtractImports($PackageName, $Result.subsOf("#Import"), $Result, $TPackage, $CProduct);
		final FileCompileResult.TypeRegistration TReg = new FileCompileResult.TypeRegistration($PackageName, $Imports, ID);
		
		// Add the one that us compiled by Curry
		TypeRegistration OldTReg = Util_File.RegisterTypes($PackageName, $Result, $TPackage, $CProduct);
		if(OldTReg != null) {
			for(int i = 0; i < OldTReg.getTypeDataCount(); i++)
				TReg.addTypeData(ID, OldTReg.getTypeData(i));
		}
		
		// Parse Pattern types --------------------------------------------------------------------

		// Add the imports
		if($Imports != null) $CProduct.addImport($Imports);

		Documentation Doc = null;
		for(int i = 0; i < $Result.entryCount(); i++) {
			String EntryName = $Result.nameOf(i);

			if(enPATTERN_TYPE.equals(EntryName)) {
				TypeRef OldOwnerTypeRef = $CProduct.getOwnerTypeRef();
				
				try {
					ParseResult Sub = $Result.subOf(i);
					
					// Get the type name
					String  TName = Sub.textOf(Util_TypeDef.enTYPE_NAME);
					String  PName = $PackageName;
					TypeRef NewOwnerTypeRef = OldOwnerTypeRef;
					
					if((PName != null) && (TName != null))
						NewOwnerTypeRef = new TLPackage.TRPackage(PName, TName);
					
					// Pass on the Document
					$CProduct.setCurrentCodeData(Util_Element.dnDOCUMENT_FOR_TYPE, Doc);
					
					final int      Index      = i;
					final Object[] TypeHolder = new Object[] { null };
					
					$CProduct.doWithAnotherOwnerTypeRef(NewOwnerTypeRef, new Runnable() {
						public void run() { TypeHolder[0] = $Result.valueOf(Index, $TPackage, $CProduct); }
					});
					
					Object TSC = TypeHolder[0];
					if(!(TSC instanceof TypeSpecCreator)) {
						$CProduct.reportError(
							String.format(
								"Invalid TypeDef result from `%s` (%s) <Util_TypeDef:30>",
								$Result.nameOf(i), TSC
							), null,
							$Result.posOf(i));
						return null;
					}
					
					// Get the type accessibility
					Accessibility      Access = (Accessibility)Sub.valueOf(enACCESS, $TPackage, $CProduct);
					if(Access == null) Access = net.nawaman.curry.Package.Public;
					
					TypeSpecCreator TSCreator = (TypeSpecCreator)TSC;
					Documentation   Document  = Doc;
					Location        Location  = $CProduct.getCurrentLocation($Result.locationCROf(0));
					
					// Create the type specification object
					Object Type = new FileCompileResult.TypeSpecification(TName, Document, Access, false, Location, TSCreator);
					// TODO - Ensure that this is really unused
					/*
					if(!(Type instanceof FileCompileResult.TypeSpecification)) {
						if(Type == null) continue;
						Util_File.ReportResultProblem(
							$CProduct, "Pattern Type", "TypeSpecification", "registering/refining type",
							$Result.posOf(Index)
						);
						return null;
					}*/
					
					// Add the Type Registration record
					TReg.addTypeData(ID, (FileCompileResult.TypeSpecification)Type);
					
				} finally {
					// Release the document
					Doc = null;
					$CProduct.setCurrentCodeData(Util_Element.dnDOCUMENT_FOR_TYPE, Doc);
				}
				
			} else if("#Import".equals(EntryName)) {
				// This will add the import into the import list 
				$Result.valueOf(i, $TPackage, $CProduct);
				
			} else if(Util_Element.enDOCUMENT.equals(EntryName)) {
				Doc = (Documentation)$Result.valueOf(i, $TPackage, $CProduct);
				
			} else if(EntryName  != null)
				Doc = null;
		}
		
		
		return TReg;
	}
	
	/** Registers the structure */
	static public FileCompileResult.StructuralRegistration RegisterFileStructure(final String $PackageName,
			final ParseResult $Result, final PTypePackage $TPackage, final CompileProduct $CProduct) {

		String ID = "PatternID"+Random.nextInt();
		FileCompileResult.StructuralRegistration SReg = new FileCompileResult.StructuralRegistration($PackageName, null, ID);
		
		StructuralRegistration OldSReg = Util_File.RegisterFileStructure($PackageName, $Result, $TPackage, $CProduct);
		for(int i = 0; i < OldSReg.getPackageElementCount(); i++) {
			PackageElement<?> PE = OldSReg.getPackageElement(i);
			if(PE instanceof FileCompileResult.PackageFunction) SReg.addFunction(ID, (FileCompileResult.PackageFunction)PE);
			if(PE instanceof FileCompileResult.PackageVariable) SReg.addVariable(ID, (FileCompileResult.PackageVariable)PE);
		}
		for(int i = 0; i < OldSReg.getTypeWithElementsCount(); i++)
			SReg.addTypeWithElements(ID, OldSReg.getTypeWithElements(i));
		
		// Parse Pattern Types --------------------------------------------------------------------

		Documentation Doc = null;
		for(int i = 0; i < $Result.entryCount(); i++) {
			String EntryName = $Result.nameOf(i);

			if(enPATTERN_TYPE.equals(EntryName)) {
				TypeRef OldOwnerTypeRef = $CProduct.getOwnerTypeRef();
				
				try {
					ParseResult Sub = $Result.subOf(i);
					
					// Get the type name
					String  TName = Sub.textOf(Util_TypeDef.enTYPE_NAME);
					String  PName = $PackageName;
					TypeRef NewOwnerTypeRef = OldOwnerTypeRef;
					
					if((PName != null) && (TName != null))
						NewOwnerTypeRef = new TLPackage.TRPackage(PName, TName);
					
					// Pass on the Document
					$CProduct.setCurrentCodeData(Util_Element.dnDOCUMENT_FOR_TYPE, Doc);
					
					final int      Index      = i;
					final Object[] TypeHolder = new Object[] { null };
					
					$CProduct.doWithAnotherOwnerTypeRef(NewOwnerTypeRef, new Runnable() {
						public void run() { TypeHolder[0] = $Result.valueOf(Index, $TPackage, $CProduct); }
					});
					
					Object PElements = TypeHolder[0];
					if(!(PElements instanceof FileCompileResult.TypeElement<?>[])) {
						$CProduct.reportError(
							String.format(
								"Invalid TypeDef result from `%s` (%s) <Util_Pattern:216>",
								$Result.nameOf(i), PElements
							), null,
							$Result.posOf(i));
						return null;
					}
					
					FileCompileResult.TypeWithElements TWE = new FileCompileResult.TypeWithElements(TName, ID);
					
					boolean                            HasElement = false;
					FileCompileResult.TypeElement<?>[] Elements   = (FileCompileResult.TypeElement[])PElements;
					if(Elements.length != 0) {
						for(int e = 0; e < Elements.length; e++) {
							FileCompileResult.TypeElement<?> Element = Elements[e];
							if(Element == null) continue;
								
							if(     Element instanceof FileCompileResult.TypeMethod)
								TWE.addMethod(ID, (FileCompileResult.TypeMethod)Element);
							
							else if(Element instanceof FileCompileResult.TypeField)
								TWE.addField(ID, (FileCompileResult.TypeField)Element);
							
							else if(Element instanceof FileCompileResult.TypeConstructor)
								TWE.addConstructor(ID, (FileCompileResult.TypeConstructor)Element);
							
							else continue;
							
							HasElement = true;
						}
					}
					
					if(!HasElement) continue;
					
					// Register the element
					SReg.addTypeWithElements(ID, TWE);
				} finally {
					// Release the document
					Doc = null;
					$CProduct.setCurrentCodeData(Util_Element.dnDOCUMENT_FOR_TYPE, Doc);
				}
				
			} else if("#Import".equals(EntryName)) {
				// This will add the import into the import list 
				$Result.valueOf(i, $TPackage, $CProduct);
				
			} else if(Util_Element.enDOCUMENT.equals(EntryName)) {
				Doc = (Documentation)$Result.valueOf(i, $TPackage, $CProduct);
				
			} else if(EntryName  != null)
				Doc = null;
		}
		
		return SReg;
	}

	/**
	 * Compile a set of PatternElements.
	 * 
	 * The elements' names must be "Constructor", "Operation" and "Attribute".
	 * The rest will be ignored.
	 **/
	static public FileCompileResult.TypeElement<?>[] ParseCompilePatternElements(
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		
		// Structure Registration
		if(!$CProduct.getCompilationState().isStructuralRegistration()) return null;
		
		// Prepare the element list
		Vector<FileCompileResult.TypeElement<?>> Elements = new Vector<FileCompileResult.TypeElement<?>>();
		
		// Collect all the methods -------------------------------------------------------------------------------------
		ParseResult[] CPRs = $Result.subsOf(  enCONSTRUCTOR);
		Object[]      Cs   = $Result.valuesOf(enCONSTRUCTOR, $TPackage, $CProduct);
		
		for(int i = 0; i < ((Cs == null) ? 0 : Cs.length); i++) {
			Object C = Cs[i];
			if(C == null) continue;
			if(!(C instanceof FileCompileResult.TypeConstructor)) {
				$CProduct.reportError(
					"Invalid return result for constructor. <PatternElements:297>",
					null, CPRs[i].startPosition()
				);
				return null;
			}
			Elements.add((FileCompileResult.TypeElement<?>)C);
		}

		// Collect all the methods -------------------------------------------------------------------------------------
		ParseResult[] MPRs = $Result.subsOf(  enOPERATION);
		Object[]      Ms   = $Result.valuesOf(enOPERATION, $TPackage, $CProduct);
		
		for(int i = 0; i < ((Ms == null) ? 0 : Ms.length); i++) {
			Object M = Ms[i];
			if(M == null) continue;
			if(!(M instanceof FileCompileResult.TypeMethod)) {
				$CProduct.reportError(
					"Invalid return result for method. <PatternElements:314>",
					null, MPRs[i].startPosition()
				);
				return null;
			}
			Elements.add((FileCompileResult.TypeElement<?>)M);
		}
		
		// Collect all the attribute -----------------------------------------------------------------------------------
		ParseResult[] FPRs = $Result.subsOf(  enATTRIBUTE);
		Object[]      Fs   = $Result.valuesOf(enATTRIBUTE, $TPackage, $CProduct);
		
		for(int i = 0; i < ((Fs == null) ? 0 : Fs.length); i++) {
			Object F = Fs[i];
			if(F == null) continue;
			
			if(!(F instanceof FileCompileResult.TypeField)) {
				$CProduct.reportError(
					"Invalid return result for pattern attribute. <Util_Pattern:110>",
					null, FPRs[i].startPosition()
				);
				return null;
			}
			
			// Add the element
			Elements.add((FileCompileResult.TypeElement<?>)F);
		}
		
		// Returns the elements
		return (Elements.size() == 0) ? null : Elements.toArray(new FileCompileResult.TypeElement[Elements.size()]);
	}

	static MoreData merge(MoreData pMoreData, Documentation pDoc) {
		MoreData MD = pMoreData;
		if(pDoc != null) {
			MoreData NewMD = new MoreData(Documentation.MIName_Documentation, pDoc);
			MD = (MD == null)?NewMD:MoreData.combineMoreData(MD, NewMD);
		}
		return MD;
	}

	static public StackOwnerAppender newTempConstant(
			final CompileProduct pCProduct, final PTypeProvider $TPackage, final ParseResult $Result, 
			final Accessibility Access, final String pVName, final Location Loc, final Documentation pDoc,
			final MoreData pMoreInfo, final MoreData pMoreData, final Object TempData) {
		
		return new AttrAppender() {
			public void append(CompileProduct $CProduct, StackOwnerBuilder SOB) {
				if(this.isRepeat($CProduct, SOB, pVName, $Result)) return;
				((TBPattern)SOB).addTempConstant(Access, pVName, pMoreInfo, Loc, merge(pMoreData, pDoc), TempData);
			}
		};
	}
	static public StackOwnerAppender newTempOutPort(
			final CompileProduct pCProduct,  final PTypeProvider $TPackage,  final ParseResult $Result,
			final Accessibility  Access,     final String        AName,      final TypeRef     TRef,
			final PortKind       PKind,      final boolean       IsWritable, final boolean     IsIValueDefault,
			final Location       Loc,        final Documentation Doc,        final MoreData    MoreInfo,
			final MoreData       MoreData,   final Object        TempData) {
		
		return new AttrAppender() {
			public void append(CompileProduct $CProduct, StackOwnerBuilder SOB) {
				if(this.isRepeat($CProduct, SOB, AName, $Result)) return;
				((TBPattern)SOB).addTempOutPort(Access, Access, AName, TRef, PKind, IsWritable, IsIValueDefault,
						Loc, Doc, MoreInfo, MoreData, TempData);
			}
		};
	}
	static public StackOwnerAppender newTempInPort(
			final CompileProduct pCProduct, final PTypeProvider $TPackage,  final ParseResult $Result,
			final Accessibility  Access,    final String        AName,      final TypeRef     TRef,
			final PortKind       PKind,     final boolean       IsWritable, final boolean     IsIValueDefault,
			final Location       Loc,       final Documentation Doc,        final MoreData    MoreInfo,
			final MoreData       MoreData,  final Object        pTempData) {
		
		return new AttrAppender() {
			public void append(CompileProduct $CProduct, StackOwnerBuilder SOB) {
				if(this.isRepeat($CProduct, SOB, AName, $Result)) return;
				((TBPattern)SOB).addTempInPort(Access, Access, AName, TRef, PKind, IsWritable, IsIValueDefault,
						Loc, Doc, MoreInfo, MoreData, pTempData);
			}
		};
	}
	
	/** Registers the structure */
	static public FileCompileResult.TypeField ParseCompilePatternAttribute(
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		
		// Structure Registration
		if(!$CProduct.getCompilationState().isStructuralRegistration()) return null;

		String Name = $Result.textOf(Util_Element.enNAME);
		
		// Quick validate ----------------------------------------------------------------------------------------------
		
		Documentation Document = (Documentation)$Result.valueOf(enDOCUMENT,     $TPackage, $CProduct);
		Accessibility Access   = (Accessibility)$Result.valueOf(enACCESS,       $TPackage, $CProduct);
		
		boolean IsConstant = ($Result.textOf(enCONSTANT) != null);
		boolean IsInPort   = ($Result.textOf(enINPORT)   != null);
		boolean IsPort     = ($Result.textOf(enPORT)     != null);
		
		boolean IsStatic   =  IsConstant;
		boolean IsWritable = !IsConstant;
		
		TypeRef     TRef   = (TypeRef)$Result.valueOf(Util_Element.enTYPE, $TPackage, $CProduct);
		ParseResult DValue =          $Result.subOf(  Util_Element.enDEFAULTVALUE);
		Location    Loc    = Util_Curry.GetLocationOf($Result, $CProduct, "$Start");
		
		// Default accessibility is public
		if(Access == null) Access = net.nawaman.curry.Package.Public;	
		
		PortKind PKind = PKSingle.Instance;
		if($Result.textOf(enIS_COLLECTION) != null) {
			PKind = PKCollection.Instance;
			TRef  = PKind.getTypeRefFor($CProduct.getEngine(), TRef);
		}
		if($Result.textOf(enIS_MAP) != null) {
		    TypeRef aKeyTRef   = (TypeRef)$Result.valueOf(enKEYTYPE,   $TPackage, $CProduct);
		    TypeRef aValueTRef = (TypeRef)$Result.valueOf(enVALUETYPE, $TPackage, $CProduct);
		    
            PKind = PKMap.Instance;
            TRef  = PKind.getTypeRefFor($CProduct.getEngine(), aKeyTRef, aValueTRef);
        }
		
		boolean IsDefault = $Result.textOf(enIS_NOT_DEFAULT) == null;

		MoreData MInfo = new MoreData(
			PortInfo.DName_Location,
			new Location(
				$CProduct.getCurrentCodeName(),
				$Result.locationCROf(Util_Element.enDEFAULTVALUE)
			)
		);
		
        final boolean aIsMap = (PKind instanceof PKMap);
        if (aIsMap) {
            final String aComparator = $Result.textOf("$Comparator");
            if (aComparator != null)
                MInfo.setData(PKMap.MIName_Comparator, aComparator);
            else if ($Result.textOf("#KeyComparator") != null){
                final TypeRef    aKeyTRef = TRef.getParameters($CProduct.getEngine())[0];
                final Executable aExec    = Util_NewPort.GetKeyComparatorExecutable(aKeyTRef, $Result, $TPackage, $CProduct);
                MInfo.setData(PKMap.MIName_Comparator, aExec);
            }
        }

		ElementResolver Resolver = null;
		if($Result.textOf("$New") != null) {
			MExecutable $ME = $CProduct.getEngine().getExecutableManager(); 
			Serializable DV = $ME.newExpr(
				$Result.locationCROf("$New"),
				Inst_NewInstance.Name,
				$ME.newType(TRef),
				UObject.EmptyObjectArray
			);
			Resolver = Util_ElementResolver.newAttrResolver_Value(IsStatic, Name, $Result.posOf("$New"), DV);
			
		} else if(DValue != null) {
			int EIndex = -1;
			for(int i = $Result.entryCount(); --i >= 0; ) {
				if(DValue != $Result.subResultAt(i)) continue;
				EIndex = i;
				break;
			}
			Resolver = Util_ElementResolver.newAttrResolver(IsStatic, Name, $Result, EIndex, $TPackage, $CProduct);			
		}
		
		StackOwnerAppender  SOA = null;
		if     (IsConstant) SOA = newTempConstant($CProduct, $TPackage, $Result, Access, Name,                                     Loc, Document,  null, null, Resolver);
		else if(IsPort)     SOA = newTempOutPort( $CProduct, $TPackage, $Result, Access, Name, TRef, PKind, IsWritable, IsDefault, Loc, Document, MInfo, null, Resolver);
		else if(IsInPort)   SOA = newTempInPort(  $CProduct, $TPackage, $Result, Access, Name, TRef, PKind, IsWritable, IsDefault, Loc, Document, MInfo, null, Resolver);
	
		if(SOA == null) return null;
		return new FileCompileResult.TypeField(Name, IsStatic, false, SOA);
	}

	static public StackOwnerAppender newTempPatternOperation(final char PEKind,
			final CompileProduct pCProduct, final PTypeProvider $TPackage, final ParseResult $Result, 
			final Accessibility Access, final Executable pExec, final MoreData pMoreData, final Object TempData) {
		
		return new OperAppender() {
			public void append(CompileProduct $CProduct, StackOwnerBuilder SOB) {
				if(this.isRepeat($CProduct, SOB, pExec.getSignature(), $Result)) return;
				TBPattern TBP = (TBPattern)SOB;
				switch(PEKind) {
					case 'p': TBP.addTempProcedure(Access, pExec, pMoreData, TempData); break;
					case 'f': TBP.addTempFunction( Access, pExec, pMoreData, TempData); break;
					case 'm': TBP.addTempMethod(   Access, pExec, pMoreData, TempData); break;
					default: throw new CurryError(String.format("Unknown pattern executable kind: '%s'", PEKind));
				}
			}
		};
	}
	
	/** Registers the structure */
	static public FileCompileResult.TypeMethod ParseCompilePatternMethod(String $Param,
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {

		// Ensure the right state of the compilation
		if(!$CProduct.getCompilationState().isStructuralRegistration()) return null;

		// StructuralRegistration
		
		String Name      = $Result.textOf(enNAME);
		String PEKindStr = $Result.textOf("$PatternExecKind");
		char   PEKind    = (PEKindStr == null) ? 'f' : PEKindStr.toLowerCase().charAt(0);

		// Quick validate ----------------------------------------------------------------------------------------------

		// Prepare some parameters -----------------------------------------------------------------------------------------
		boolean IsInterface  = Util_Element.prForInterface.equals($Param);
		boolean IsAbstract   = ($Result.textOf(Util_Element.enABSTRACT)  != null);
		boolean IsStatic     = PEKind == 'p';
		boolean HasCurryBody = $Result.textOf("$StartBody") != null;
		boolean HasBody      = HasCurryBody;
		
		if(IsAbstract && IsStatic) {
			$CProduct.reportError(
				"Static method cannot be abstrct <Util_Pattern:475>.",
				null,
				$Result.posOf(Util_Element.enSTATIC)
			);
			return null;
		}
		
		// Abstract method cannot be dynamic, delegate or have a body
		if(IsAbstract && HasBody) {
			$CProduct.reportError(
				"Abstract method cannot have the body "+"<Util_Pattern:480>.",
				null,
				$Result.posOf(Util_Element.enSTARTBODY)
			);
			return null;
		}

		// Unless it is for interface, the method needs to be one of these
		if(!IsAbstract &&  !HasBody && !IsInterface) {
			$CProduct.reportError(
				"Missing method body <Util_Pattern:494>.",
				null,
				$Result.posOf(0)
			);
			return null;
		}

		// The method is abstract, if it is for Interface
		if(IsInterface) IsAbstract = true;

		// Prepare the signature ---------------------------------------------------------------------------------------
		Accessibility Access   = (Accessibility)$Result.valueOf(Util_Element.enACCESS,   $TPackage, $CProduct);
		Documentation Document = (Documentation)$Result.valueOf(Util_Element.enDOCUMENT, $TPackage, $CProduct);
		
		// Default Access
		if(Access == null) Access = Util_Element.DEFAULT_TYPE_ELEMENT_ACCESSIBILITY;
		
		Location      Location  = Util_Curry.GetLocationOf($Result, $CProduct, "$Start");
		ExecInterface Interface = (ExecInterface)$Result.valueOf(Util_Element.enINTERFACE, $TPackage, $CProduct);
		ExecSignature Signature = ExecSignature.newSignature(Name, Interface, Location, Documentation.Util.NewMoreData(Document));
		MoreData      MData     = MoreData.newMoreDataFromArray($Result.valuesOf(Util_Element.enMOREDATA, $TPackage, $CProduct), true);
		
		MExecutable MExec    = $CProduct.getEngine().getExecutableManager();
		Executable  Exec;
		ExecKind    EKind;
		
		
		// Render function is a macro
		if((PEKind == 'f') && Name.equals(UPattern.OPERNAME_RENDER) && (Signature.getParamCount() == 0)) {
			EKind = ExecKind.Macro;
			Exec  = MExec.newMacro(Signature, null, null, null);
		} else {
			EKind = ExecKind.SubRoutine;
			Exec  = MExec.newSubRoutine(Signature, null, null, null);
		}
		
		
		// Prepare the appender
		StackOwnerAppender SOA = null;
		if(IsAbstract) {
			SOA = StackOwnerAppender.Util.newAbstractOperDirect($CProduct, $TPackage, $Result,
					Access, Signature, EKind, MData, Document);
			
		} else if(HasBody) {
			int EIndex = $Result.lastIndexFor(Util_Element.enCURRYBODY);
			// Create the resolver
			ElementResolver Resolver = Util_ElementResolver.newOperResolver(
						IsStatic, Signature, EKind, $Result, EIndex, null, $TPackage, $CProduct);

			SOA = newTempPatternOperation(PEKind, $CProduct, $TPackage, $Result,
					Access, Exec, MData, Resolver);
			
		} else {
			$CProduct.reportError(
				"Internal Error: An impossible statuc for TypeMethod compilation is found. Please report this to " +
				"the developer of Curry. <Util_Pattern:191>",
				null,
				$Result.posOf(0)
			);
			return null;
		}
        
		return new FileCompileResult.TypeMethod(Signature, IsStatic, IsAbstract, SOA);
	}
	
	/** Compile a Class TypeSpec. */
	static public TypeSpecCreator ParseCompilePatternTypeSpecCreator(
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		
		// Type Registration and Type Refinition
		boolean isTRegistration = $CProduct.getCompilationState().isTypeRegistration();
		boolean isTRefinition   = $CProduct.getCompilationState().isTypeRefinition();
		
		if(!isTRegistration && !isTRefinition) return null;

		// Get the engine
		Engine $Engine = $CProduct.getEngine();
		
		TKPattern TKP = (TKPattern)$Engine.getTypeManager().getTypeKind(TKPattern.KindName);
		if(TKP == null) {
			$CProduct.reportError(
				"Class type is not support <Util_Pattern:278>.",
				null, $Result.posOf(0)
			);
			return null;
		}
		
		boolean IsFinal    = false; //$Result.textOf(enFINAL)    != null;
		boolean IsAbstract = false; //$Result.textOf(enABSTRACT) != null;
		
		try {
			int Count = $CProduct.getErrorMessageCount();
		
			// Prepare data
			TypeRef   SuperTypeRef   = (TypeRef)                      $Result.valueOf( enINHERITREF,  $TPackage, $CProduct);
			TypeRef[] InterfaceTRefs = (TypeRef[])UArray.convertArray($Result.valuesOf(enIMPLEMENTED, $TPackage, $CProduct), TypeRef[].class);
			
			// There is some error
			if(Count != $CProduct.getErrorMessageCount()) return null;
		
			return TKP.getTypeSpecCreator(IsAbstract, IsFinal, SuperTypeRef, InterfaceTRefs, null, null);
					
		} finally {
			$CProduct.clearParameterizedTypeInfos();
		}
	}
	
	/** Parse and compile Interface TypeDef */
	static public TypeSpecCreator ParseCompileInterfaceTypeSpecCreator(boolean IsDuck,
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		
		// Type Registration and Type Refinition
		boolean isTRegistration = $CProduct.getCompilationState().isTypeRegistration();
		boolean isTRefinition   = $CProduct.getCompilationState().isTypeRefinition();
		
		if(!isTRegistration && !isTRefinition) return null;
		
		TypeRef TargetTypeRef = (TypeRef)$Result.valueOf( enINHERITREF,  $TPackage, $CProduct);
		
		if(TargetTypeRef == null) TargetTypeRef = UPattern.TREF_Pattern;
		/*
		// TODO - Add this in later - This is commented out as a way to avoid some problem (Target Type is not yet refined)
		else if(isTRefinition) {
			if(!MType.CanTypeRefByAssignableByInstanceOf(null, $CProduct.getEngine(), UPattern.TREF_Pattern, TargetTypeRef)) {
				$CProduct.reportError(
					"Pattern interface must be targeting a Pattern: <Util_Pattern:649>",
					null,
					$Result.posOf(enINHERITREF)
				);
				return null;
			}
		}*/
		
		return Util_TypeDef.ParseCompileInterfaceTypeSpecCreator(IsDuck, TargetTypeRef, $Result, $TPackage, $CProduct);
	}
	
	/** Registers the structure */
	static public FileCompileResult.TypeField ParseCompilePatternInterfaceTypeAttribute(
			ParseResult $Result, PTypePackage $TPackage, CompileProduct $CProduct) {
		TypeRef TRef         = (TypeRef)$Result.valueOf(Util_Element.enTYPE, $TPackage, $CProduct);
		boolean IsCollection = ($Result.textOf("$IsCollection") != null);
		
		if(IsCollection)
			TRef = new TLParametered.TRParametered(PKCollection.getSimpleListTypeRef($CProduct.getEngine()), TRef);
		
		return Util_TypeElement.ParseCompileTypeAttribute(TRef, $Result, $TPackage, $CProduct);
	}
}
