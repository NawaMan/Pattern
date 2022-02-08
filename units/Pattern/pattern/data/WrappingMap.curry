// @Curry:
@@:Package(pattern~>data);

@@:Import(java.util.Vector);

@@:Import(net.nawaman.util.CanBeImmutable);

<?{ Wrapping Map is a curry map-wrapper }?>
@@:TypeDef public class WrappingMap <KeyType:any, ValueType:any>
            implements
                Map<KeyType,ValueType>,
                CanBeImmutable {

    // Fields --------------------------------------------------------------------------------------

    @@:Field private Data      : java.util.Map = null;
    @@:Field private IsReadOnly: boolean       = false;
    
    // Constructors --------------------------------------------------------------------------------
    
    <?{ Constructs an WrappingMap. }?>
    @@:Constructor public () {
        this(new java.util.HashMap());
    };
    
    <?{ Constructs an WrappingMap. }?>
    @@:Constructor public (pKeyComparator :java.util.Comparator) {
        this(new net.nawaman.util.data.ComparedMap(pKeyComparator));
    };
    
    <?{ Constructs an WrappingMap. }?>
    @@:Constructor public (pMap:java.util.Map) {
        this.Data = pMap;
    };
    
    // Map<KeyType,ValueType> ----------------------------------------------------------------------
    
    <?{ Removes all mappings from this map (optional operation). }?>
    @@:Method public clear():void {
        if(this.IsReadOnly) return;
        this.Data.clear();
        return;
    };

    <?{ Returns true if this map contains a mapping for the specified key. }?>
    @@:Method public containsKey(Key:KeyType):boolean {
        return this.Data.containsKey(Key);
    };

    <?{ Returns true if this map maps one or more keys to the specified value. }?>
    @@:Method public containsValue(Value:ValueType):boolean {
        return this.Data.containsValue(Value);
    };

    <?{ Returns a set view of the mappings contained in this map. }?>
    @@:Method public entrySet():Set<MapEntry<KeyType, ValueType>> {
        throw new UnsupportedOperationException("SimpleMap does not support entrySet().");
    };

    <?{ Returns the value to which this map maps the specified key. }?>
    @@:Method public get(Key:KeyType):ValueType {
        return this.Data.get(Key);
    };

    <?{ Returns true if this map contains no key-value mappings. }?>
    @@:Method public isEmpty():boolean {
        return this.Data.isEmpty();
    };

    <?{ Returns a set view of the keys contained in this map. }?>
    @@:Method public keySet():Set<KeyType> {
        return new SimpleSet<KeyType>(this.Data.keySet());
    };

    <?{ Associates the specified value with the specified key in this map (optional operation). }?>
    @@:Method public put(Key:KeyType, Value:ValueType):ValueType {
        if(this.IsReadOnly) return null;
        return this.Data.put(Key, Value);
    };

    <?{ Copies all of the mappings from the specified map to this map (optional operation). }?>
    @@:Method public putAll(M:Map<KeyType, ValueType>):void {
        if(this.IsReadOnly) return null;
        this.Data.putAll(M);
    };

    <?{ Removes the mapping for this key from this map if it is present (optional operation). }?>
    @@:Method public remove(Key:KeyType):ValueType {
        if(this.IsReadOnly) return null;
        return this.Data.remove(Key);
    };

    <?{ Returns the number of key-value mappings in this map. }?>
    @@:Method public size():int {
        return this.Data.size();
    };

    <?{ Returns a collection view of the values contained in this map. }?>
    @@:Method public values():Collection<ValueType> {
        return new SimpleList<ValueType>(this.Data.values());
    };

    // Map -------------------------------------------------------------------------------------------------------------

    <?{ Returns true if this map contains a mapping for the specified key. }?>
    @@:Method public containsKey(Key:any):boolean {
        return this.Data.containsKey(Key);
    };

    <?{ Returns true if this map maps one or more keys to the specified value. }?>
    @@:Method public containsValue(Value:any):boolean {
        return this.Data.containsValue(Value);
    };

    <?{ Returns the value to which this map maps the specified key. }?>
    @@:Method public get(Key:any):ValueType {
        return this.Data.get(Key);
    };

    <?{ Associates the specified value with the specified key in this map (optional operation). }?>
    @@:Method public put(Key:any, Value:any):ValueType {
        return this.Data.put(Key, Value);
    };

    <?{ Copies all of the mappings from the specified map to this map (optional operation). }?>
    @@:Method public putAll(M:java.util.Map):void {
        this.Data.putAll(M);
    };

    <?{ Removes the mapping for this key from this map if it is present (optional operation). }?>
    @@:Method public remove(Key:any):ValueType {
        return this.Data.remove(Key);
    };
    
    // MightBeImmutable ------------------------------------------------------------------------------------------------
    
    <?{ Checks if this SimpleMap is immutable. }?>
    @@:Method public isImmutable():boolean {
        return this.IsReadOnly;
    };
    
    // CanBeImmutable --------------------------------------------------------------------------------------------------
    
    <?{ Make this SimpleMap a immutable one. }?>
    @@:Method public toImmutable():boolean {
        return this.IsReadOnly = true;
    };
    
    // General ---------------------------------------------------------------------------------------------------------
    
    <?{ Compares the specified object with this entry for equality. }?>
    @@:Method public equals(O:Object):boolean {
        return this.Data.equals(O);
    };
    
    <?{ Returns string representation of this SimpleArray. }?>
    @@:Method public toString():String {
        return this.Data.toString();
    };
    
    <?{ Returns hash value (not hashCode) of this SimpleArray. }?>
    @@:Method public hash():int {
        return this.Data.hash();
    };
    
    @@:Method public computeIfPresent(key: any, func: java.util.function.BiFunction):any {
        return this.Data.computeIfPresent(key, func);
    };
    
    @@:Method public computeIfAbsent(key: any, func: java.util.function.BiFunction):any {
        throw new UnsupportedOperationException("SimpleMap does not support sort operation.");
    };
    
    @@:Method public computeIfAbsent(key: any, func: java.util.function.Function):any {
        throw new UnsupportedOperationException("SimpleMap does not support sort operation.");
    };
    
    @@:Method public getOrDefault(key: any, defaultValue: any):any {
        throw new UnsupportedOperationException("SimpleMap does not support sort operation.");
    };
    
    @@:Method public forEach(func: java.util.function.BiConsumer):void {
        throw new UnsupportedOperationException("SimpleMap does not support forEach operation.");
    };
    
    @@:Method public compute(key: any, action: java.util.function.BiFunction):any {
        throw new UnsupportedOperationException("SimpleMap does not support compute operation.");
    };
    
    @@:Method public putIfAbsent(a: any, b: any):any  {
        throw new UnsupportedOperationException("WrappingMap does not support putIfAbsent operation.");
    };
    
    @@:Method public merge(a: any, b: any, c: java.util.function.BiFunction):any  {
        throw new UnsupportedOperationException("SimpleMap does not support merge operation.");
    };
    
    @@:Method public replaceAll(a: java.util.function.BiFunction):void {
        throw new UnsupportedOperationException("SimpleMap does not support replaceAll operation.");
    };
    @@:Method public replace(a: any, b: any, c: any):boolean {
        throw new UnsupportedOperationException("SimpleMap does not support replace operation.");
    };
    @@:Method public replace(a: any, b: any):any {
        throw new UnsupportedOperationException("SimpleMap does not support replace operation.");
    };
    @@:Method public remove(a: any, b: any):boolean {
        throw new UnsupportedOperationException("SimpleMap does not support remove operation.");
    };
};