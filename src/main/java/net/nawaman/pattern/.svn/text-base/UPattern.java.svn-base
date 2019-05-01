package net.nawaman.pattern;

import net.nawaman.curry.TLPrimitive;
import net.nawaman.curry.TypeRef;
import net.nawaman.curry.util.MoreData;

public class UPattern {
	
	// ID and render ---------------------------------------------------------------------------------------------------
	
	/** The operation name for render */
	static public final String OPERNAME_RENDER = "render";
	
	// Port ------------------------------------------------------------------------------------------------------------
	
	/** Config name for pattern action */
	static public final String CONFIG_NAME_PATTERN_ACTION = "pattern_action";
	
	/** Config name for pattern action */
	static public final String CONFIG_NAME_IS_RENDERED = "isRendered";
	
	/** Config name for pattern action */
	static public final String CONFIG_NAME_GETDATA = "getData";
	
	/** Config name for pattern action */
	static public final String CONFIG_NAME_GETFINALVALUE = "getFinalValue";
	
	/** Config name for pattern action */
	static public final String CONFIG_NAME_HASNOVALUE = "hasNoValue";
	
	// Java TypeRefs ---------------------------------------------------------------------------------------------------

	static public final TypeRef TREF_Renderer = new TLPrimitive.TRPrimitive(Renderer.class.getCanonicalName());
	static public final TypeRef TREF_Pattern  = new TLPrimitive.TRPrimitive(Pattern .class.getCanonicalName());
	static public final TypeRef TREF_Port     = new TLPrimitive.TRPrimitive(Port    .class.getCanonicalName());
	static public final TypeRef TREF_PortInfo = new TLPrimitive.TRPrimitive(PortInfo.class.getCanonicalName());

	static public final TypeRef TREF_PortAction                      = new TLPrimitive.TRPrimitive(PortAction                     .class.getCanonicalName());
	static public final TypeRef TREF_PAAssignment_Simple             = new TLPrimitive.TRPrimitive(PAAssignment            .Simple.class.getCanonicalName());
	static public final TypeRef TREF_PAAssignment_New                = new TLPrimitive.TRPrimitive(PAAssignment_New               .class.getCanonicalName());
	static public final TypeRef TREF_PAAssertion_Simple              = new TLPrimitive.TRPrimitive(PAAssertion             .Simple.class.getCanonicalName());
	static public final TypeRef TREF_PAAppend_Simple                 = new TLPrimitive.TRPrimitive(PAAppend                .Simple.class.getCanonicalName());
	static public final TypeRef TREF_PASortAppendable_Hash_Simple    = new TLPrimitive.TRPrimitive(PASortAppendable_Hash   .Simple.class.getCanonicalName());
	static public final TypeRef TREF_PASortAppendable_Compare_Simple = new TLPrimitive.TRPrimitive(PASortAppendable_Compare.Simple.class.getCanonicalName());
	static public final TypeRef TREF_PAFilter_Simple                 = new TLPrimitive.TRPrimitive(PAAFilter               .Simple.class.getCanonicalName());
	
    static public final TypeRef TREF_PAAssociate_Simple = new TLPrimitive.TRPrimitive(PAAssociate.Simple.class.getCanonicalName());

	static public final TypeRef TREF_PAPostRendering_Simple   = new TLPrimitive.TRPrimitive(PAPostRendering  .Simple.class.getCanonicalName());
	static public final TypeRef TREF_Action_PostRender_Simple = new TLPrimitive.TRPrimitive(Action_PostRender.Simple.class.getCanonicalName());
	static public final TypeRef TREF_AAssertion_Simple        = new TLPrimitive.TRPrimitive(AAssertion       .Simple.class.getCanonicalName());
	
	static public final TypeRef TREF_PAPreRendering_Simple   = new TLPrimitive.TRPrimitive(PAPreRendering  .Simple.class.getCanonicalName());
	static public final TypeRef TREF_Action_PreRender_Simple = new TLPrimitive.TRPrimitive(Action_PreRender.Simple.class.getCanonicalName());

	// PortKind --------------------------------------------------------------------------------------------------------
	
	/** Name or MoreData entry that holds a type kind */
	static public final String MIName_PortKind = "PortKind";
	
	/** Creates a more data holding a type kind */
	static public MoreData NewMoreDataFromPortKind(PortKind pKind) {
		return new MoreData(MIName_PortKind, pKind);
	}
    /** Creates a more data holding a type kind */
    static public MoreData NewMoreDataFromPortKind(
            final PortKind                  pKind,
            final SerializableComparator<?> pComparator) {
        if (pComparator == null) {
            final MoreData aMD = NewMoreDataFromPortKind(pKind);
            return  aMD;
        }
        return new MoreData(
                new MoreData.Entry(UPattern.MIName_PortKind,   pKind),
                new MoreData.Entry(PKMap   .MIName_Comparator, pComparator)
            );
    }
	
	/** Returns the port-kind held by a MoreData */
	static public PortKind GetPortKindFromMoreData(MoreData MD) {
		if(MD == null) return PKSingle.Instance;
		
		Object Value = MD.getData(MIName_PortKind);
		if(!(Value instanceof PortKind))
			return PKSingle.Instance;
		
		return ((PortKind)Value);
	}
	
	// InPort & Method -------------------------------------------------------------------------------------------------
	
	/** Name or MoreData entry that holds the flag indicating if the Port is InPort */
	static public final String MIName_IsInPort = "InPort";
	
	/** Name or MoreData entry that holds the flag indicating if the Port default value is initializly assigned as default. */
	static public final String MIName_IsDefaultValue_DefaultAssigned = "IsDefaultAssigned";
	
	/** Name or MoreData entry that holds the flag indicating if the operation is Method */
	static public final String MIName_IsMethod = "Method";
		
	/** Checks if the MoreData of a port indicate that the port is an InPort */
	static public boolean CheckInPortFromMoreData(MoreData MD) {
		if(MD == null)
			return false;
		
		Object Value = MD.getData(MIName_IsInPort);
		if(!(Value instanceof Boolean))
			return false;
		
		return ((Boolean)Value);
	}
		
	/** Checks if the MoreData of a operation indicate that the operation is a Method */
	static public boolean CheckMethodFromMoreData(MoreData MD) {
		if(MD == null)
			return false;
		
		Object Value = MD.getData(MIName_IsMethod);
		if(!(Value instanceof Boolean))
			return false;
		
		return ((Boolean)Value);
	}
}
