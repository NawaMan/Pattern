// @Curry:
@@:Package(nawaman~>data);

@@:Import(java.util.Vector);

@@:Import(net.nawaman.util.CanBeImmutable);

<?{ Simple Implementation of Map. }?>
@@:TypeDef public class SimpleMap <KeyType:any, ValueType:any> implements Map<KeyType,ValueType>, CanBeImmutable {

	// Fields ----------------------------------------------------------------------------------------------------------

	@@:Field private Keys:       Vector  = new Vector();
	@@:Field private Data:       Vector  = new Vector();
	@@:Field private IsReadOnly: boolean = false;
	
	// Constructors ----------------------------------------------------------------------------------------------------

	<?{ Constructs an SimpleMap. }?>
	@@:Constructor public () {};

	<?{ Constructs an SimpleMap. }?>
	@@:Constructor public (pMap:Map<KeyType, ValueType>) {
		if(pMap == null) return;
		this.putAll(pMap);
	};

	<?{ Constructs an SimpleMap. }?>
	@@:Constructor public (pMap:java.util.Map) {
		if(pMap == null) return;
		this.putAll(pMap);
	};

	// Map<KeyType,ValueType> ------------------------------------------------------------------------------------------

	<?{ Removes all mappings from this map (optional operation). }?>
	@@:Method public clear():void {
		if(this.IsReadOnly) return;
		this.Keys.clear();
		this.Data.clear();
		return;
	};

	<?{ Returns true if this map contains a mapping for the specified key. }?>
	@@:Method public containsKey(Key:KeyType):boolean {
		return this.Keys.contains(Key);
	};

	<?{ Returns true if this map maps one or more keys to the specified value. }?>
	@@:Method public containsValue(Value:ValueType):boolean {
		return this.Data.contains(Value);
	};

	<?{ Returns a set view of the mappings contained in this map. }?>
	@@:Method public entrySet():Set<MapEntry<KeyType, ValueType>> {
		throw new UnsupportedOperationException("SimpleMap does not support entrySet().");
	};

	<?{ Returns the value to which this map maps the specified key. }?>
	@@:Method public get(Key:KeyType):ValueType {
		int Index = this.Keys.indexOf(Key);
		if(Index < 0)
		    return ((ValueType)null)??;	// Default value of KeyType
		return this.Data.get(Index);
	};

	<?{ Returns true if this map contains no key-value mappings. }?>
	@@:Method public isEmpty():boolean {
		return this.Keys.isEmpty();
	};

	<?{ Returns a set view of the keys contained in this map. }?>
	@@:Method public keySet():Set<KeyType> {
		return new SimpleSet<KeyType>(this.Keys);
	};

	<?{ Associates the specified value with the specified key in this map (optional operation). }?>
	@@:Method public put(Key:KeyType, Value:ValueType):ValueType {
		if(this.IsReadOnly) return null;
		int Index = this.Keys.indexOf(Key);
		if(Index == -1) {
			this.Keys.add(Key);
			this.Data.add(Value);
		} else {
			this.Keys.set(Index, Key);
			this.Data.set(Index, Value);
		}
		return Value;
	};

	<?{ Copies all of the mappings from the specified map to this map (optional operation). }?>
	@@:Method public putAll(M:Map<KeyType, ValueType>):void {
		if(this.IsReadOnly) return null;
		if(M == null) return;
		
		Set<KeyType> Keys = M.keySet();
		foreach(KeyType Key : Keys)
			this.put(Key, M.get(Key));
			
		return null;
	};

	<?{ Removes the mapping for this key from this map if it is present (optional operation). }?>
	@@:Method public remove(Key:KeyType):ValueType {
		if(this.IsReadOnly) return null;
		int Index = this.Keys.indexOf(Key);
		if(Index == -1) return null;
		Object Return = this.Data.get(Index); 
		this.Keys.remove(Index);
		this.Data.remove(Index);
		return null;
	};

	<?{ Returns the number of key-value mappings in this map. }?>
	@@:Method public size():int {
		return this.Keys.size();
	};

	<?{ Returns a collection view of the values contained in this map. }?>
	@@:Method public values():Collection<ValueType> {
		return new SimpleList<ValueType>(this.Data);
	};

	// Map -------------------------------------------------------------------------------------------------------------

	<?{ Returns true if this map contains a mapping for the specified key. }?>
	@@:Method public containsKey(Key:any):boolean {
		return this.Keys.contains(Key);
	};

	<?{ Returns true if this map maps one or more keys to the specified value. }?>
	@@:Method public containsValue(Value:any):boolean {
		return this.Data.contains(Value);
	};

	<?{ Returns the value to which this map maps the specified key. }?>
	@@:Method public get(Key:any):ValueType {
		cast(KeyType K = Key) return this.get(K);
		return null;
	};

	<?{ Associates the specified value with the specified key in this map (optional operation). }?>
	@@:Method public put(Key:any, Value:any):ValueType {
		if(this.IsReadOnly) return null;
		
		cast(KeyType   K = Key)
		cast(ValueType V = Value)
			return this.put(K, V);
			
		return null;
	};

	<?{ Copies all of the mappings from the specified map to this map (optional operation). }?>
	@@:Method public putAll(M:java.util.Map):void {
		if((this.IsReadOnly) || (M == null)) return;
		java.util.Set Ks = M.keySet();
		foreach(Any Key : Ks) {
			cast(KeyType   K = Key)
			cast(ValueType V = M.get(Key))
				this.put(K, V);
		}
		return null;
	};

	<?{ Removes the mapping for this key from this map if it is present (optional operation). }?>
	@@:Method public remove(Key:any):ValueType {
		cast(KeyType K = Key) return this.remove(K);
		return null;
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
		cast(java.util.Map M = O) {
			if(this.size() != M.size()) return false;
			
			java.lang.Iterable Ks = null;
			cast(SimpleMap SM = O) Ks = SM.Keys;
			else                   Ks = M .keySet();

			foreach(Any K : Ks) {
				unless(this.containsKey(K))     return false;
				unless(this.get(K) == M.get(K)) return false;
			}
			return true;
		}
		return false;
	};
	
	<?{ Returns string representation of this SimpleArray. }?>
	@@:Method public toString():String {
		StringBuffer SB = new StringBuffer();
		SB.append("{");
		Vector Ks = this.Keys;
		Vector Vs = this.Data;
		fromto(int i = 0 : Ks.size()) {
			if(i != 0) SB.append(", ");
			SB.append(@:toString(Ks.get(i)));
			SB.append("->");
			SB.append(@:toString(Vs.get(i)));
		}
		SB.append("}");
		return SB.toString();
	};
	
	<?{ Returns hash value (not hashCode) of this SimpleArray. }?>
	@@:Method public hash():int {
		int    H  = 0;
		Vector Ks = this.Keys;
		Vector Vs = this.Data;
		fromto(int i = 0 : Ks.size())
			H += ((@:hash(Ks.get(i)) << 3) + @:hash(Vs.get(i)));
		return H;
	};

};