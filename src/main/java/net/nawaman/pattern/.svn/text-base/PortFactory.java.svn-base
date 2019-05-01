package net.nawaman.pattern;

import net.nawaman.curry.Context;
import net.nawaman.curry.Engine;
import net.nawaman.curry.ExternalContext;
import net.nawaman.curry.MType;
import net.nawaman.curry.Type;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.util.DataHolder;
import net.nawaman.curry.util.DataHolderFactory;
import net.nawaman.curry.util.DataHolderInfo;
import net.nawaman.curry.util.MoreData;

/** PortFactory */
public class PortFactory implements DataHolderFactory {
	
	static final public String FactoryName = "Port";

	static public final PortFactory Factory = new PortFactory();
	
	/**{@inheritDoc}*/ @Override
	public String getName() {
		return FactoryName;
	}
	
	/**{@inheritDoc}*/ @Override
	public Port newDataHolder(Context pContext, Engine pEngine, Type pType, Object pData, boolean pIsReadable,
			boolean pIsWritable, MoreData pMoreInfo, DataHolderInfo pDHInfo) {
		return this.newDataHolder(pContext, pEngine, pType, pData, true, pIsReadable, pIsWritable, pMoreInfo, pDHInfo);
	}
	
	/**{@inheritDoc}*/ @Override
	public Port newDataHolder(Context pContext, Engine pEngine, Type pType, boolean pIsReadable, boolean pIsWritable,
			MoreData pMoreInfo, DataHolderInfo pDHInfo) {
		return this.newDataHolder(pContext, pEngine, pType, null, false, pIsReadable, pIsWritable, pMoreInfo, pDHInfo);
	}
	
	private Port newDataHolder(Context pContext, Engine pEngine, Type pType, Object pData, boolean IsSet,
			boolean pIsReadable, boolean pIsWritable, MoreData pMoreInfo, DataHolderInfo pDHInfo) {
		
		if(!(pDHInfo instanceof PortInfo))
			throw new IllegalArgumentException("PortInfo is needed for creating a Port.");
		
		if(pDHInfo.isWritable() != pIsWritable)
			throw new IllegalArgumentException("PortInfo conflict: The given PortInfo is for a Port.");
		
		if(!pDHInfo.getTypeRef().equals(pType.getTypeRef()))
			throw new IllegalArgumentException(
					String.format(
						"PortInfo conflict: The given PortInfo is for '%s' but the given type is '%s'.",
						pDHInfo.getTypeRef(), pType.getTypeRef() 
					)
				);
		
		PortInfo PInfo = (PortInfo)pDHInfo;
		
		// Port must always be readable
		if(!pIsReadable)
			throw new IllegalArgumentException("All ports must be readable.");
		
		// The type must match the port kind
		MoreData MD = PInfo.getMoreInfo();
		PortKind PK = null;
		if((MD != null) && ((PK = UPattern.GetPortKindFromMoreData(MD)) != null) && !(PK instanceof PKSingle)) {
			TypeRef BaseRef = PK.getBaseTypeRef(pEngine);
			
			// The default data must be assignable to the type
			if(!MType.CanTypeRefByAssignableByInstanceOf(null, pEngine, BaseRef, pType.getTypeRef()))
				throw new IllegalArgumentException(
						String.format(
							"Uncompatible port type '%s' to the port kind %s.",
							pType.getTypeRef(), PK
						)
					);
		}
		
		// Record the action
		if(pData instanceof PAAssignment)
			((PAAssignment)pData).ARecord = ExternalContext.newActionRecord(pContext);
		
		// Creates appropriate port as specified
		Port P = new Port(pEngine, (PortInfo)pDHInfo, PK);
		if(IsSet) {
			// Set the initial value
			P.config(
				pContext,
				UPattern.CONFIG_NAME_PATTERN_ACTION,
				new Object[] {
					// Wrap the object so that it can be assigned even if the port is not writable 
					pData
				}
			);
		}
		
		return P;
	}
	
	/**{@inheritDoc}*/ @Override
	public boolean isInstance(DataHolder DH) {
		return DH instanceof Port;
	}
}