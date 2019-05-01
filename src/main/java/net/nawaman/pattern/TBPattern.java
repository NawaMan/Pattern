package net.nawaman.pattern;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.nawaman.curry.Accessibility;
import net.nawaman.curry.ExecSignature;
import net.nawaman.curry.Executable;
import net.nawaman.curry.Expression;
import net.nawaman.curry.Documentation;
import net.nawaman.curry.ExternalExecutor;
import net.nawaman.curry.Location;
import net.nawaman.curry.OperationInfo;
import net.nawaman.curry.StackOwnerBuilderEncloseObject;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.Executable.Macro;
import net.nawaman.curry.OperationInfo.OIDirect;
import net.nawaman.curry.extra.type_object.TBClass;
import net.nawaman.curry.util.MoreData;

/** TypeBuilder of Patterns */
public class TBPattern extends TBClass {

	/** Constructs a pattern-type builder */
	protected TBPattern(TSPattern pTSpec, Accessibility pAccess, Location pLocation,
			StackOwnerBuilderEncloseObject pEncloseObject) {
		super(pTSpec, pAccess, pLocation, pEncloseObject);
	}
	
	/**{@inheritDoc}*/ @Override
	protected void doJustBeforeToInactive() {		
		// Ensure that render() can only be a function -----------------------------------------------------------------
		if(!this.validateRender())
			throw new IllegalArgumentException(
				String.format(
					"'%s()' of a pattern can only be a public Function: '%s' <TBPattern:58>.",
					UPattern.OPERNAME_RENDER, this.getTypeRef()
				)
			);
		
		if(!this.reorganizeRender())
			throw new IllegalArgumentException(
				String.format(
					"'%s()' of a pattern must be a macro: '%s' <TBPattern:66>.",
					UPattern.OPERNAME_RENDER, this.getTypeRef()
				)
			);
	}
	
	/** Validate the operation 'Render' */
	private boolean validateRender() {
		HashSet<OperationInfo> TOIs = this.getStaticOperInfos();
		if((TOIs != null) && (TOIs.size() != 0)) {
			for(OperationInfo OI : TOIs) {
				if(OI == null) continue;
				ExecSignature ES = OI.getDeclaredSignature();
				if(UPattern.OPERNAME_RENDER.equals(ES.getName()) && (ES.getParamCount() == 0))
					return false;
			}
		} 
		HashSet<OperationInfo> DOIs = this.getOperInfos();
		if((DOIs != null) && (DOIs.size() != 0)) {
			for(OperationInfo OI : DOIs) {
				if(OI == null) continue;
				ExecSignature ES = OI.getDeclaredSignature();
				if(!UPattern.OPERNAME_RENDER.equals(ES.getName()))
					continue;
				
				if((ES.getParamCount() == 0) && OI.getAccessibility().isPublic())
					continue;
					
				MoreData MD = OI.getMoreData();
				if(!UPattern.CheckInPortFromMoreData(MD))
					return false;
			}
		}
		
		return true;
	}
	
	/** Reorganize render function or add one if it does not exist */
	private boolean reorganizeRender() {
		// Find the render function first
		OperationInfo RenderOI = null;
		HashSet<OperationInfo> DOIs = this.getOperInfos();
		if((DOIs != null) && (DOIs.size() != 0)) {
			for(OperationInfo OI : DOIs) {
				if(OI == null) continue;
				ExecSignature ES = OI.getDeclaredSignature();
				if(!UPattern.OPERNAME_RENDER.equals(ES.getName()))
					continue;
				
				if((ES.getParamCount() != 0) || !OI.getAccessibility().isPublic())
					continue;
				
				// Save for later used
				RenderOI = OI;
				break;
			}
		}

		// Re-organize Render function - Move it to a data slot and replace it with the default one 
		if(RenderOI != null) {
			if(RenderOI instanceof OIDirect) {
				OIDirect RenderDOI = (OIDirect)RenderOI;
				Executable Exec = RenderDOI.getDeclaredExecutable();
				if(!(Exec instanceof Macro))
					return false;
				
				// Move the executable of the render function to the data array
				this.setTSpecDataAt(TSPattern.Index_RenderFunction, Exec);
				// Replace the one we want back in
				DOIs.remove(RenderDOI);
				this.addFunction(Accessibility.Public, Pattern.GetRenderFunction(this.getEngine()), null);
			}
		} else {
			// Add the default render function
			this.addFunction(Accessibility.Public, Pattern.GetRenderFunction(this.getEngine()), null);
		}
		
		return true;
	}
	
	// Constants -------------------------------------------------------------------------------------------------------
	
	/** Add a constant - Static attributes */
	final public Object addConstant(Accessibility PARead, String pVName, Serializable pValue, MoreData pMoreInfo,
			Location pLocation, MoreData pMoreData) {
		// Make it all expression
		if(!(pValue instanceof Expression))
			pValue = Expression.toExpr(pValue);
		// Add as a static attribute constant
		return this.addStaticAttrConst(PARead, pVName, false, pValue, true, pMoreInfo, pLocation, pMoreData);
	}
	/** Add a constant - Static attributes */
	final public Object addTempConstant(Accessibility PARead, String pVName, MoreData pMoreInfo, Location pLocation,
			MoreData pMoreData, Object TempData) {
		// Add as a static attribute constant
		return this.addTempStaticAttrConst(PARead, pVName, false, true, pMoreInfo, pLocation, pMoreData, TempData);
	}
	/** Resolve the temporary constant */
	final public boolean resolveTempConstant(String pName, Object pID, TypeRef pDValueTypeRef, Serializable pDValue) {
		return this.resolveStaticTempAttr(pName, pID, pDValueTypeRef, pDValue);
	}
	/** Returns the names of the temp attribute */
	final public Set<String> getTempConstantNames() {
		return this.getStaticTempAttrNames();
	}
	/** Returns the temp-data of the temporary attribute */
	final public Object getStaticTempConstantTempData(String pName) {
		return this.getStaticTempAttrTempData(pName);
	}
	
	// Procedure -------------------------------------------------------------------------------------------------------
	
	/** Add a procedure - Static operations */
	final public Object addProcedure(Accessibility Access, Executable pExec, MoreData pMoreData) {
		// Add as a static attribute constant
		return this.addStaticOperDirect(Access, pExec, pMoreData);
	}
	/** Add a procedure - Static operations */
	final public Object addTempProcedure(Accessibility Access, Executable pExec, MoreData pMoreData,
			Object TempData) {
		// Add as a static attribute constant
		return this.addTempStaticOperDirect(Access, pExec, pMoreData, TempData);
	}
	/** Returns the signatures of the temp attribute */
	final public Set<ExecSignature> getTempProcedureSignatures() {
		return this.getStaticTempOperSignatures();
	}
	/** Returns the temp-data of the temporary procedure */
	final public Object getTempProcedureTempData(ExecSignature pES) {
		return this.getStaticTempOperTempData(pES);
	}
	/** Resolve the temporary procedure */
	final public boolean resolveTempProcedure(ExecSignature pES, Object pID, Expression pBody) {
		return this.resolveStaticTempOper(pES, pID, pBody);
	}
	/** Resolve the temporary procedure */
	final public boolean resolveTempProcedure(ExecSignature pES, Object pID, ExternalExecutor pEE, Object pEEID, Object pEESC) {
		return this.resolveStaticTempOper(pES, pID, pEE, pEEID, pEESC);
	}
	/** Resolve the temporary procedure */
	final public boolean resolveTempProcedure(ExecSignature pES, Object pID, Executable pExec) {
		return this.resolveStaticTempOper(pES, pID, pExec);
	}
	
	// InPort/OutPort --------------------------------------------------------------------------------------------------
	
	/** Ensure that the MoreData contains appropriate data to indicate that this port is an InPort */
	final MoreData ensureMoreDataForInPort(MoreData MoreData) {
		MoreData MData = MoreData;
		if(MoreData == null) MData = new MoreData(UPattern.MIName_IsInPort, true);
		else {
			Object Flag = MData.getData(UPattern.MIName_IsInPort);
			if(!Boolean.TRUE.equals(Flag)) {
				if(Flag == null) {
					if(!MData.isFrozen()) MData.setData(UPattern.MIName_IsInPort, true);
					else {
						MData = net.nawaman.curry.util.MoreData.combineMoreData(
							new MoreData(UPattern.MIName_IsInPort, true),
							MData
						);
					}
				} else {
					throw new IllegalArgumentException(
						String.format(
							"The more-data entry '%s' of an InPort must be `true`:boolean: '%s' <TBPattern:272>",
							UPattern.MIName_IsInPort, this.getTypeRef()
						)
					);
				}
			}
		}
		return MData;
	}
	/** Ensure that the MoreData contains appropriate data to indicate that this port is an OutPort */
	final MoreData ensureMoreDataForOutPort(MoreData MoreData) {
		MoreData MData = MoreData;
		if(MoreData != null) {
			Object Flag = MData.getData(UPattern.MIName_IsInPort);
			if(!Boolean.FALSE.equals(Flag)) {
				if(Flag != null) {
					throw new IllegalArgumentException(
						String.format(
							"The more-data entry '%s' of an OutPort must be null or `false`:boolean: '%s' <TBPattern:291>",
							UPattern.MIName_IsInPort, this.getTypeRef()
						)
					);
				}
			}
		}
		return MData;
	}
	
	/** Add an in-port - ports that are automatically rendered when the pattern render function is entered */
	final public Object addInPort(Accessibility PARead, Accessibility PAWrite, String AName, TypeRef TRef, PortKind PKind,
			boolean IsWritable, Location Location, Documentation Document, MoreData MoreInfo, MoreData MoreData) {		
		// Ensure the 'IsInPort' flag is properly set
		MoreData MData = this.ensureMoreDataForInPort(MoreData);
		// Constructor PortInfo
		PortInfo PInfo = new PortInfo(TRef, PKind, IsWritable, Document, MoreInfo);
		// Add as a direct attribute
		return this.addAttrDirect(PARead, PAWrite, PAWrite, AName, false, PInfo, Location, MData);
	}
	/** Add an in-port - ports that are automatically rendered when the pattern render function is entered */
	final public Object addInPort(Accessibility PARead, Accessibility PAWrite, String AName, TypeRef TRef, PortKind PKind,
			boolean IsWritable, boolean IsIValue_Default, Expression DefaultValue, Location Location, Documentation Document,
			MoreData MoreInfo, MoreData MoreData) {		
		// Ensure the 'IsInPort' flag is properly set
		MoreData MData = this.ensureMoreDataForInPort(MoreData);
		// Constructor PortInfo
		PortInfo PInfo = new PortInfo(TRef, PKind, IsIValue_Default, DefaultValue, IsWritable, Document, MoreInfo);
		// Add as a direct attribute
		return this.addAttrDirect(PARead, PAWrite, PAWrite, AName, false, PInfo, Location, MData);
	}
	/** Add an out-port - ports that are automatically rendered when the pattern render function is exited */
	final public Object addOutPort(Accessibility PARead, Accessibility PAWrite, String AName, TypeRef TRef, PortKind PKind,
			boolean IsWritable, boolean IsIValue_Default, Expression DefaultValue, Location Location, Documentation Document,
			MoreData MoreInfo, MoreData MoreData) {
		// Ensure the 'IsInPort' flag is properly set
		MoreData MData = this.ensureMoreDataForOutPort(MoreData);
		// Constructor PortInfo
		PortInfo PInfo = new PortInfo(TRef, PKind, IsIValue_Default, DefaultValue, IsWritable, Document, MoreInfo);
		// Add as a direct attribute
		return this.addAttrDirect(PARead, PAWrite, PAWrite, AName, false, PInfo, Location, MData);
	}

	/** Add an in-port - ports that are automatically rendered when the pattern render function is entered */
	final public Object addTempInPort(Accessibility PARead, Accessibility PAWrite, String AName, TypeRef TRef, PortKind PKind,
			boolean IsWritable, boolean IsIValue_Default, Location Location, Documentation Document, MoreData MoreInfo,
			MoreData MoreData, Object pTempData) {
		// Ensure the 'IsInPort' flag is properly set
		MoreData MData = this.ensureMoreDataForInPort(MoreData);
		
		// Constructor PortInfo
		PortInfo PInfo;
		if(pTempData == null)
			 PInfo = new PortInfo(TRef, PKind,                         IsWritable, Document, MoreInfo);
		else PInfo = new PortInfo(TRef, PKind, IsIValue_Default, null, IsWritable, Document, MoreInfo);
		
		// Add as a direct attribute
		return this.addTempAttrDirect(PARead, PAWrite, PAWrite, AName, false, PInfo, Location, MData, pTempData);
	}
	/** Add an out-port - ports that are automatically rendered when the pattern render function is exited */
	final public Object addTempOutPort(Accessibility PARead, Accessibility PAWrite, String AName, TypeRef TRef,
			PortKind PKind, boolean IsWritable, boolean IsIValue_Default, Location Location, Documentation Document,
			MoreData MoreInfo, MoreData MoreData, Object pTempData) {
		
		// Ensure the 'IsInPort' flag is properly set
		MoreData MData = this.ensureMoreDataForOutPort(MoreData);
		
		// Constructor PortInfo
		PortInfo PInfo;
		if(pTempData == null)
			 PInfo = new PortInfo(TRef, PKind,                         IsWritable, Document, MoreInfo);
		else PInfo = new PortInfo(TRef, PKind, IsIValue_Default, null, IsWritable, Document, MoreInfo);
		
		// Add as a direct attribute
		return this.addTempAttrDirect(PARead, PAWrite, PAWrite, AName, false, PInfo, Location, MData, pTempData);
	}
	
	// Port resolving -----------------------------------------------------------------------------
	
	/** Resolve the temporary port */
	final public boolean resolveTempPort(String pName, Object pID, TypeRef pDValueTypeRef, Serializable pDValue) {
		return this.resolveTempAttr(pName, pID, pDValueTypeRef, pDValue);
	}
	/** Returns the names of the temp attribute */
	final public Set<String> getTempPortNames() {
		return this.getTempAttrNames();
	}
	/** Returns the temp-data of the temporary attribute */
	final public Object getTempPortTempData(String pName) {
		return this.getTempAttrTempData(pName);
	}
	
	// Function/Methods ------------------------------------------------------------------------------------------------
	
	/** Ensure that the MoreData contains appropriate data to indicate that this operation is a method */
	final MoreData ensureMoreDataForMethod(MoreData MoreData) {
		MoreData MData = MoreData;
		if(MoreData == null) MData = new MoreData(UPattern.MIName_IsMethod, true);
		else {
			Object Flag = MData.getData(UPattern.MIName_IsMethod);
			if(!Boolean.TRUE.equals(Flag)) {
				if(Flag == null) {
					if(!MData.isFrozen()) MData.setData(UPattern.MIName_IsMethod, true);
					else {
						MData = net.nawaman.curry.util.MoreData.combineMoreData(
							new MoreData(UPattern.MIName_IsMethod, true),
							MData
						);
					}
				} else {
					throw new IllegalArgumentException(
						String.format(
							"The more-data entry '%s' of a Method must be `true`:boolean.: '%s' <TBPattern:436>",
							UPattern.MIName_IsMethod, this.getTypeRef()
						)
					);
				}
			}
		}
		return MData;
	}
	/** Ensure that the MoreData contains appropriate data to indicate that this operation is a function */
	final public MoreData ensureMoreDataForFunction(MoreData MoreData) {
		MoreData MData = MoreData;
		if(MoreData != null) {
			Object Flag = MData.getData(UPattern.MIName_IsMethod);
			if(!Boolean.FALSE.equals(Flag)) {
				if(Flag != null) {
					throw new IllegalArgumentException(
						String.format(
							"The more-data entry '%s' of a Function must be `true`:boolean.: '%s' <TBPattern:454>",
							UPattern.MIName_IsMethod, this.getTypeRef()
						)
					);
				}
			}
		}
		return MData;
	}

	/** Check the function Render when a Function is added */
	private void ensureRender_WhenFunctionAdded(Accessibility Access, Executable pExec) {
		ExecSignature ES = pExec.getSignature();
		if(!UPattern.OPERNAME_RENDER.equals(ES.getName()) || (ES.getParamCount() != 0) || (Access == null) || Access.isPublic())
			return;
		
		throw new IllegalArgumentException(
			String.format(
				"'%s()' of a pattern can only be a public Function: '%s' <TBPattern:472>.",
				UPattern.OPERNAME_RENDER, this.getTypeRef()
			)
		);
	}
	/** Check the function Render when a Method is added */
	private void ensureRender_WhenMethodAdded(Executable pExec) {
		ExecSignature ES = pExec.getSignature();
		if(!UPattern.OPERNAME_RENDER.equals(ES.getName()) || (ES.getParamCount() != 0))
			return;
		
		throw new IllegalArgumentException(
			String.format(
				"'%s()' of a pattern can only be a Function: '%s' <TBPattern:485>.",
				UPattern.OPERNAME_RENDER, this.getTypeRef()
			)
		);
	}
	
	/** Add a function - operations */
	final public Object addFunction(Accessibility Access, Executable pExec, MoreData MoreData) {
		// Check the Render function
		this.ensureRender_WhenFunctionAdded(Access, pExec);
		
		// Ensure the 'IsMethod' flag is properly set
		MoreData MData = this.ensureMoreDataForFunction(MoreData);
		// Add as a static attribute constant
		return this.addOperDirect(Access, pExec, MData);
	}
	/** Add a method - operations */
	final public Object addMethod(Accessibility Access, Executable pExec, MoreData MoreData) {
		// Check the Render function
		this.ensureRender_WhenMethodAdded(pExec);
		
		// Ensure the 'IsMethod' flag is properly set
		MoreData MData = this.ensureMoreDataForMethod(MoreData);
		// Add as a static attribute constant
		return this.addOperDirect(Access, pExec, MData);
	}
	/** Add a temp function - operations */
	final public Object addTempFunction(Accessibility Access, Executable pExec, MoreData pMoreData, Object TempData) {
		// Check the Render function
		this.ensureRender_WhenFunctionAdded(Access, pExec);
		
		// Add as a static attribute constant
		return this.addTempOperDirect(Access, pExec, pMoreData, TempData);
	}
	/** Add a temp function - operations */
	final public Object addTempMethod(Accessibility Access, Executable pExec, MoreData pMoreData, Object TempData) {
		// Check the Render function
		this.ensureRender_WhenMethodAdded(pExec);
		
		// Ensure the 'IsMethod' flag is properly set
		MoreData MData = this.ensureMoreDataForMethod(pMoreData);
		
		// Add as a static attribute constant
		return this.addTempOperDirect(Access, pExec, MData, TempData);
	}
	
}
