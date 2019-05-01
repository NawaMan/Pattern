package net.nawaman.pattern;

import java.io.Serializable;

import net.nawaman.curry.Documentation;
import net.nawaman.curry.Engine;
import net.nawaman.curry.Executable;
import net.nawaman.curry.Expression;
import net.nawaman.curry.Location;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.CurryExecutable.CurryFragment;
import net.nawaman.curry.util.DataHolderInfo;
import net.nawaman.curry.util.MoreData;

/** DataHolderInfo of PortInfo */
public class PortInfo extends DataHolderInfo {
	
	static private final long serialVersionUID = -1651651654546516586L;
	
	static public final String DName_Location = "Location";
	
	boolean IsIValue_Default;
	
	/** DataHolderInfo of PortInfo */
	public PortInfo(
	        final TypeRef       pTRef,
	        final PortKind      PKind,
	        final boolean       IsWritable,
	        final Documentation Doc,
	        final MoreData      pMoreInfo) {
		this(
			true,
			null,
			IsWritable,
			false,
			NewMoreData(PKind, Doc, pMoreInfo),
			pTRef
		);
	}
	/** DataHolderInfo of PortInfo */
	public PortInfo(
	        final TypeRef       pTRef,
	        final PortKind      PKind,
	        final boolean       pIsIValue_Default,
	        final Expression    pIValue,
	        final boolean       IsWritable,
	        final Documentation Doc,
	        final MoreData      pMoreInfo) {
		this(
			pIsIValue_Default,
			new PAAssignment.Simple(null, pIValue),
			IsWritable,
			true,
			NewMoreData(PKind, Doc, pMoreInfo),
			pTRef
		);
	}

	/** DataHolderInfo of PortInfo */
	private PortInfo(
	        final boolean             pIsIValue_Default,
	        final PAAssignment.Simple pIValue,
	        final boolean             pIsWritable,
	        final boolean             pIsSet,
	        final MoreData            pMoreInfo,
	        final TypeRef             pTRef) {
		super(
			pTRef,                    // The TypeRef
			pIValue,                  // Default value
			PortFactory.FactoryName,  // Factory name
			true,                     // Is readable
			pIsWritable,              // Is writable
			pIsSet,                   // Is set
			pIsSet,	                  // Is expression
			pMoreInfo                 // MoreInfo
		);
		this.IsIValue_Default = pIsIValue_Default;
	}
	
	/** Checks if the default value of this port will be assigned as "Default" */
	public boolean isIValue_Default() {
		return this.IsIValue_Default;
	}
	
	// Cloneable --------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	public PortInfo clone() {
		return new PortInfo(
				                 this.IsIValue_Default,
			(PAAssignment.Simple)this.getIValue(),
			                     this.isWritable(),
			                     this.isSet(),
			                     (this.getMoreInfo() == MoreData.Empty)
			                         ? MoreData.Empty
			                         : this.getMoreInfo().clone(),
			                     this.getTypeRef().clone()
		);
	}
	
	// Resolve ----------------------------------------------------------------
	
	/**{@inheritDoc}*/ @Override
	public PortInfo resolve(Engine $Engine, TypeRef DValueTypeRef, Serializable DValue) {
		TypeRef TRef       = this.getTypeRefRAW();
		boolean IsWritable = this.isWritable();
		if((TRef == null) && !IsWritable) TRef = DValueTypeRef;
		
		if(!(DValue instanceof PAAssignment.Simple)) {
			if(!(DValue instanceof Expression)) DValue = Expression.toExpr(DValue);
				
			Location Location = (Location)this.getMoreInfo().getData(DName_Location);
			
			if(!(DValue instanceof CurryFragment))
				DValue = new CurryFragment(null, "new_default", DValueTypeRef, Location, null, DValue, null, null);
				
			DValue = new PAAssignment.Simple(null, (Executable)DValue, this.IsIValue_Default);
		}
		
		return new PortInfo(
			                      this.IsIValue_Default,
			(PAAssignment.Simple) DValue,
			                      IsWritable,
			                      this.isSet(),
			                      (this.getMoreInfo() == MoreData.Empty)
			                           ? MoreData.Empty
			                           : this.getMoreInfo().clone(),
			                      this.getTypeRef().clone()
		);
	}
	
	// Utilities -------------------------------------------------------------------------------------------------------
	
	/** Creates a new MoreDAta to be used with Port */
	static MoreData NewMoreData(PortKind PKind, Documentation Doc, MoreData pMoreInfo) {
		// All null - so return null;
		if((PKind == null) && (Doc == null) && (pMoreInfo == null)) return null;
		
		// MoreInfo is null, so create a MoreData from PKind and Doc
		MoreData MD = null;
		if     ((PKind == null) && (Doc != null)) MD = Documentation.Util.NewMoreData(Doc);
		else if((PKind != null) && (Doc == null)) MD = UPattern.NewMoreDataFromPortKind(PKind);
		else if((PKind != null) && (Doc != null)) {
			MD = new MoreData();
			MD.setData(Documentation.MIName_Documentation, Doc);
			MD.setData(UPattern.     MIName_PortKind,      PKind);
			
		} else return pMoreInfo;
		
		if(pMoreInfo != null)
			MD = MoreData.combineMoreData(MD, pMoreInfo);
		
		return MD;
	}/* */
}
