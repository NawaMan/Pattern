package net.nawaman.pattern;

import net.nawaman.curry.*;
import net.nawaman.curry.Instructions_Core.Inst_NewInstance;
import net.nawaman.curry.Instructions_Core.Inst_Type;
import net.nawaman.curry.Instructions_StackOwner.Inst_Invoke_ByParams;
import net.nawaman.util.CanBeImmutable;

/** Action to associate map port value */
abstract public class PAAssociate extends PAPostAssignment {

    private static final long serialVersionUID = -2800261778302063628L;
    
    /** Constructs a Port action */
    protected PAAssociate(Executable pCondition) {
        super(pCondition);
    }
    
    /**{@inheritDoc}*/ @Override
    protected void doAction(Context $Context, Port Port, Object PortValue) {
        
        if(!(Port.PData.getKind() instanceof PKMap)) {
            ActionRecord     AR = this.ARecord;
            LocationSnapshot LS = (AR == null) ? null : AR.getLocationSnapshot();
            throw new PatternActionError(
                String.format(
                    "\nIn compatible final value: only collection port can be append: %s <PAAssociate:25>\n    %s",
                    Port, LS
                )
            );
        }
        
        PFVMap FVMap = (PFVMap)Port.IPIFinal;
        Object FMap  = FVMap.getValue();
        
        if(FMap instanceof DObjectStandalone)
            FMap = ((DObjectStandalone)FMap).getAsDObject();
        
        Engine      $Engine = ExternalContext.getEngine($Context);
        MExecutable $ME     = $Engine.getExecutableManager();
        DObject     DMap    = (DObject)FMap;
        
        if(DMap == null) {
            if (FVMap.getActionRecord() == null) {
                // When no one has set anything, let create the default one
                Expression aPType = $ME.newExpr(Inst_Type.Name, Port.PData.getTypeRef());
                FMap              = ExternalContext.execute($Context, $ME.newExpr(Inst_NewInstance.Name, aPType));
                FVMap.Value       = FMap;
                
                if(FMap instanceof DObjectStandalone)
                    FMap = ((DObjectStandalone)FMap).getAsDObject();
                
                DMap = (DObject)FMap;
                
            } else {
                throw new PatternActionError(
                    String.format(
                        "Fail to append: The final value is null: %s <PAAssociate:56>:\n    %s\n" +
                        "The null value is given at:\n    %s",
                        Port, this.ARecord, Port.IPIFinal.FinalAction.ARecord
                    ),
                    $Context
                );
            }
        }
        
        
        Object DNative = DMap.getAsNative();
        if(((CanBeImmutable)DNative).isImmutable()) {
            throw new PatternActionError(
                String.format("The final value is immutable: %s <PAAssociate:69>:\n    %s", Port, this.ARecord),
                $Context
            );
        }
        
        Object Key   = this.getKey  ($Context, Port);
        Object Value = this.getValue($Context, Port);
        
        // Add the value
        ExternalContext.execute($Context, $ME.newExpr(Inst_Invoke_ByParams.Name, DMap, "put", Key, Value));
        
        // Add the action
        FVMap.FinalValues.put(Key, new FinalValue(Value, this));
    }
    
    /** Get the associated key */
    abstract protected Object getKey(Context $Context, Port Port);
    
    /** Get the associated value */
    abstract protected Object getValue(Context $Context, Port Port);
    
    // SubClasses ------------------------------------------------------------------------------------------------------
    
    /** Simple implementation of a Port Assignment Action */
    static public class Simple extends PAAssociate {
        
        static private final long serialVersionUID = 52157852685565556L;

        /** Constructs a Port action */
        public Simple(Executable pCondition, Executable pKey, Executable pValue) {
            super(pCondition);
            this.Key   = pKey;
            this.Value = pValue;
        }
        
        Executable Key;
        Executable Value;
        
        /**{@inheritDoc}*/ @Override
        protected Object getKey(Context $Context, Port Port) {
            // Get the key
            Object aKey = this.executeExecutable($Context, this.Key, Port);
            return aKey;
        }
        /**{@inheritDoc}*/ @Override
        protected Object getValue(Context $Context, Port Port) {
            // Get the value
            Object aValue = this.executeExecutable($Context, this.Value, Port);
            return aValue;
        }
    }
}
