package net.nawaman.pattern;

import java.util.*;

import net.nawaman.curry.*;

public class PIDMap extends PIDConfigurable {
    
    static class PIDMapElement {
        final Object Key;
        final Port   VPort;
        PIDMapElement(
                final Engine  $Engine,
                final Object  pKey,
                final TypeRef pTRef) {
            final PortInfo    aPI1  = new PortInfo(pTRef, PKSingle.Instance, true, null, null);
            final MExecutable aEM   = $Engine.getExecutableManager();
            final Type        aType = (Type)$Engine.execute(aEM.newType(pTRef));
            this.Key   = pKey;
            this.VPort = PortFactory.Factory.newDataHolder(null, $Engine, aType, true, true, null, aPI1);
        }
        @Override
        public String toString() {
            return String.format("[%s: %s]", this.Key, this.VPort);
        }
    }

    @SuppressWarnings("rawtypes")
    PIDMap(final Comparator pKeyComparator) {
        this.KeyComparator = pKeyComparator;
    }
    
    Vector<PIDMapElement> AssociationData = new Vector<PIDMapElement>();
    boolean               IsCollapsed     = false;
    
    @SuppressWarnings("rawtypes")
    private Comparator KeyComparator   = null;
    
    final boolean isCollased() {
        return this.IsCollapsed;
    }
    final boolean isEmpty() {
        return this.AssociationData.isEmpty();
    }
    final int size() {
        return this.AssociationData.size();
    }
    @SuppressWarnings("rawtypes")
    final Comparator getKeyComparator() {
        return this.KeyComparator;
    } 
    
    // Merging -------------------------------------------------------------------------------------
    
    @Override
    protected void mergeBy_More(final PortInternalData pPIData) {
        super.mergeBy_More(pPIData);
        final PIDMap aFrom = (PIDMap)pPIData;
        this.AssociationData = this.mergeVector(this.AssociationData, aFrom.AssociationData);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean compareKey(
            final Engine     $Engine,
            final Comparator pKeyComparator,
            final Object     pKey,
            final Object     pAlreadyAddedKey) {
        final Object aAKey = pAlreadyAddedKey;
        if (pKey == aAKey)
            return true;
        
        if (pKey == null)
            return false;
        
        // Execute the comparison
        if (pKeyComparator != null) {
            final int aCompareValue = pKeyComparator.compare(pKey, aAKey);
            return (aCompareValue == 0);
        }
        
        // Compare using equals
        final boolean aIsEquals = $Engine.equals(pKey, aAKey) && $Engine.equals(aAKey, pKey);
        return aIsEquals;
    }
    @SuppressWarnings("rawtypes")
    private boolean compareElementKey(
            final Engine        $Engine,
            final Comparator    pKeyComparator,
            final PIDMapElement pElement,
            final PIDMapElement pAlreadyAddedElement) {
        final Object  aKey      = pElement.Key;
        final Object  aAKey     = pAlreadyAddedElement.Key;
        final boolean aIsEquals = this.compareKey($Engine, pKeyComparator, aKey, aAKey);
        return aIsEquals;
    }
    private void mergeElement(
            final PIDMapElement pElement,
            final PIDMapElement pFrom) {
        final Port aPort = pElement.VPort;
        final Port aFrom = pFrom   .VPort;
        aPort.mergeBy(aFrom);
    }
    
    @SuppressWarnings("rawtypes")
    void collapse(
            final Engine     $Engine,
            final Comparator pKeyComparator) {
        if (this.IsCollapsed)
            return;
        
        final Vector<PIDMapElement> aCollapsedData = new Vector<PIDMapElement>();
        MainLoop: for(final PIDMapElement aElement : this.AssociationData) {
            for(final PIDMapElement aAlreadyAddedElement : aCollapsedData) {
                final boolean aIsSame = this.compareElementKey(
                        $Engine,
                        pKeyComparator,
                        aElement,
                        aAlreadyAddedElement);
                
                if (!aIsSame)
                    continue;
                
                // Combine
                this.mergeElement(aAlreadyAddedElement, aElement);
                continue MainLoop;
            }
            // Add
            aCollapsedData.add(aElement);
        }
        this.AssociationData.clear();
        this.AssociationData.addAll(aCollapsedData);
        this.IsCollapsed = true;
    }
    
    /*
    private TypeRef getKeyTRef(final Port pPort) {
        if (!(pPort.PData.getKind() instanceof PKMap))
            throw new IllegalArgumentException("Map port is required: " + pPort + " <PIDMap:115>");
        
        final Engine              $Engine  = pPort.PData.getEngine();
        final TypeRef             aMapTRef = pPort.PData.getTypeRef();
        final ParameteredTypeInfo aPTInfo  = aMapTRef.getParameteredTypeInfo($Engine);
        final TypeRef             aTRef    = aPTInfo.getParameterTypeRef(0); // Key
        return aTRef;
    }*/
    private TypeRef getValueTRef(final Port pPort) {
        if (!(pPort.PData.getKind() instanceof PKMap))
            throw new IllegalArgumentException("Map port is required: " + pPort + " <PIDMap:125>");
        
        final Engine              $Engine  = pPort.PData.getEngine();
        final TypeRef             aMapTRef = pPort.PData.getTypeRef();
        final ParameteredTypeInfo aPTInfo  = aMapTRef.getParameteredTypeInfo($Engine);
        final TypeRef             aTRef    = aPTInfo.getParameterTypeRef(1); // Value
        return aTRef;
    }
    
    Port newElement(
            final Port   pPort,
            final Object pKey) {
        final Engine        $Engine     = pPort.PData.getEngine();
        final TypeRef       aValueRef   = this.getValueTRef(pPort);
        final PIDMapElement aNewElement = new PIDMapElement($Engine, pKey, aValueRef);
        this.AssociationData.add(aNewElement);
        return aNewElement.VPort;
    }
    @SuppressWarnings("rawtypes")
    Port getElement(
            final Port   pPort,
            final Object pKey) {
        final Engine     $Engine   = pPort.PData.getEngine();
        final Comparator $Combiner = (this.IsCollapsed && (this.KeyComparator != null))
                            ? this.KeyComparator
                            : null;
        
        // Returns the last one if exist
        for (int i = this.AssociationData.size(); --i >= 0; ) {
            final PIDMapElement aEachElement = this.AssociationData.get(i);
            final Object        aKey         = aEachElement.Key;
            if (pKey == aKey)
                return aEachElement.VPort;
            
            final boolean aIsEquals = this.compareKey($Engine, $Combiner, pKey, aKey);
            if (aIsEquals)
                return aEachElement.VPort;
        }
        
        if (this.isCollased())
            return null;
        
        final Port aPort = this.newElement(pPort, pKey);
        return aPort;
    }
}
