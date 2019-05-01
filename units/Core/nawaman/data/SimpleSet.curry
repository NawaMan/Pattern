// @Curry:
@@:Package(nawaman~>data);

@@:Import(java.util.Vector);
@@:Import(net.nawaman.util.CanBeImmutable);
@@:Import(curry=>Iterable);
@@:Import(curry=>Set);

<?{ Simple implementation of a Set }?>
@@:TypeDef public class SimpleSet <ValueType:any> implements Set<ValueType>, CanBeImmutable {

	// Fields ----------------------------------------------------------------------------------------------------------

	@@:Field private Data:       Vector  = new Vector();
	@@:Field private IsReadOnly: boolean = false;
	
	// Constructors ----------------------------------------------------------------------------------------------------

	<?{ Constructs an SimpleSet. }?>
	@@:Constructor public () {};

	<?{ Constructs an SimpleSet. }?>
	@@:Constructor public (pData:ValueType[]) {
		if(pData == null) return;
		this.Data.addAll(java.util.Arrays.asList(pData));
	};

	<?{ Constructs an SimpleSet. }?>
	@@:Constructor public (pData:Collection<ValueType>) {
		if(pData == null) return;
		this.Data.addAll(pData);
	};

	<?{ Constructs an SimpleSet. }?>
	@@:Constructor public (pData:java.util.Collection) {
		if(pData == null) return;
		this.Data.addAll(pData);
	};

	// Set<ValueType> --------------------------------------------------------------------------------------------------

	<?{ Adds element into the set }?>
	@@:Method public add(Value:ValueType):boolean {
		if(this.IsReadOnly)           return false;
		if(this.Data.contains(Value)) return false;
		return this.Data.add(Value);
	};

	<?{ Adds all elements of the given list into the list }?>
	@@:Method public addAll(Col:Collection<ValueType>):boolean {
		if(this.IsReadOnly) return false;
		// Loop to add all
		foreach(ValueType Value : Col) {
			if(this.Data.contains(Value)) continue;
			return this.Data.add(Value);
		}
		return true;
	};
	
	<?{ Empty the list }?>
	@@:Method public clear():void {
		if(this.IsReadOnly) return;
		this.Data.clear();
	};
	
	<?{ Checks if the list contains the element }?>
	@@:Method public contains(Value:ValueType):boolean {
		return this.Data.contains(Value);
	};
	
	<?{ Checks if the list contains all elements of the given collection }?>
	@@:Method public containsAll(Col:Collection<ValueType>):boolean {
		return this.Data.containsAll(Col);
	};
	
	<?{ Checks if the set is empty }?>
	@@:Method public isEmpty():boolean {
		return this.Data.isEmpty();
	};
	
	<?{ Returns the size of the list }?>
	@@:Method public size():int {
		return this.Data.size();
	};
	
	<?{ Removes a single instance of the specified element from this list, if it is present. }?>
	@@:Method public remove(Value:ValueType):boolean {
		if(this.IsReadOnly) return false;
		return this.Data.remove(Value);
	};

	<?{ Removes all this list's elements that are also contained in the specified list. }?>
	@@:Method public removeAll(Col:Collection<ValueType>):boolean {
		if(this.IsReadOnly) return false;
		return this.Data.removeAll(Col);
	};
	
	<?{ Removes all this list's elements that are also contained in the specified list. }?>
	@@:Method public retainAll(Col:Collection<ValueType>):boolean {
		if(this.IsReadOnly) return false;
		return this.Data.retainAll(Col);
	};
	
	<?{ Returns the number of elements in this list. }?>
	@@:Method public toArray():ValueType[] {
		ValueType[] A = new ValueType[this.Data.size()];
		
		for(int i = 0; i < this.Data.size(); i++)
			A[i] = (ValueType)(this.Data.get(i));
		return A;
	};

	<?{ Returns an array containing all of the elements in this list. }?>
	@@:Method public toArray(A:ValueType[]):ValueType[] {
		if((A == null) || (A.length != this.Data.size()))
			 return this.toArray();
		else {
			for(int i = 0; i < this.Data.size(); i++)
				A[i] = (ValueType)(this.Data.get(i));
			return A;
		}
	};

	// Set -------------------------------------------------------------------------------------------------------------

	<?{ Adds element into the set }?>
	@@:Method public add(Value:any):boolean {
		cast(ValueType V = Value) return this.add(V);
		throw new IllegalArgumentException("Wrong data kind: `"+Value+"` for '"+ValueType.type+"'.");
	};

	<?{ Adds all elements of the given list into the list }?>
	@@:Method public addAll(Col:java.util.Collection):boolean {
		if(Col == null) return true;
		foreach(any V : Col) if(!this.add(V)) return false;
		return true;
	};
	
	<?{ Checks if the list contains the element }?>
	@@:Method public contains(Value:any):boolean {
		cast(ValueType V = Value) return this.contains(V);
		throw new IllegalArgumentException("Wrong data kind: `"+Value+"` for '"+ValueType.type+"'.");
	};
	
	<?{ Checks if the list contains all elements of the given collection }?>
	@@:Method public containsAll(Col:java.util.Collection):boolean {
		if(Col == null) return true;
		foreach(any V : Col) if(!this.contains(V)) return false;
		return true;
	};
	
	<?{ Removes a single instance of the specified element from this list, if it is present. }?>
	@@:Method public remove(Value:any):boolean {
		cast(ValueType V = Value) return this.remove(V);
		return false;
	};

	<?{ Removes all this list's elements that are also contained in the specified list. }?>
	@@:Method public removeAll(Col:java.util.Collection):boolean {
		if(Col == null) return true;
		foreach(any V : Col) {
			cast(ValueType D = V) {
				if(!this.remove(D))
					return false;
			}
		}
		return true;
	};
	
	<?{ Removes all this list's elements that are also contained in the specified list. }?>
	@@:Method public retainAll(Col:java.util.Collection):boolean {
		if(this.IsReadOnly) return false;
		return this.Data.retainAll(Col);
	};

	<?{ Returns an array containing all of the elements in this list. }?>
	@@:Method public toArray(A:any[]):any[] {
		if(A == null) return this.toArray();
	
		net.nawaman.curry.Type AT = @:getComponentTypeArrayObject(A);
		if(!@:isKindOf(ValueType.type, AT))
			throw new IllegalArgumentException("Wrong data kind: `"+AT+"` for '"+ValueType.type+"'.");
		
		int Length = this.Data.size();
		if(A.length != Length) A = @:newArray(AT, Length);
		
		// Copy each value
		fromto(int i = 0 : Length)
			A[i] = this.Data.get(i);
			
		return A;
	};
	
	// Iterable<ValueType> ---------------------------------------------------------------------------------------------

	<?{ Returns an iterator over a set of elements of type T. }?>
	@@:Method public iterator():Iterator<ValueType> {
		// TODO - Improve here for speed
		return new ArrayIterator<ValueType>(this.toArray());
	};
	
	// MightBeImmutable ------------------------------------------------------------------------------------------------
	
	<?{ Checks if this SimpleSet is immutable. }?>
	@@:Method public isImmutable():boolean {
		return this.IsReadOnly;
	};
	
	// CanBeImmutable --------------------------------------------------------------------------------------------------
	
	<?{ Make this SimpleSet a immutable one. }?>
	@@:Method public toImmutable():boolean {
		return this.IsReadOnly = true;
	};
	
	// General ---------------------------------------------------------------------------------------------------------
	
	<?{ Compares the specified object with this entry for equality. }?>
	@@:Method public equals(O:Object):boolean {
	
		// If O is an array, change it to a set.
		cast(ValueType[] A = O)
			O = new SimpleSet<ValueType>(A);
		
		// If O is an iterable, change it to an iterator
		else cast(Iterable<ValueType> I = O)
			O = I.iterator();
		
		// If O is a iterator, make sure all of its value exist in this Set and no elements of this set that is not
		//    in the iterator.  
		cast(Iterator<ValueType> I = O) {
			int Count = this.size();
			foreach(ValueType V : I) {
				if(!this.contains(V)) return false;
				Count--;
			}
			return (Count == 0);
		}
	
		// If O is a collection, ensure exclusive containning 
		cast(Collection<ValueType> C = O)
			return (C.size() == this.size()) && C.containsAll(this) && this.containsAll(C);
		
		// Other kind of collection
		return nawaman~>data:>CheckEquals_Vector(this.Data, O);
	};
	
	<?{ Returns string representation of this SimpleSet. }?>
	@@:Method public toString():String {
		StringBuffer SB = new StringBuffer();
		Vector       Vs = this.Data;
		
		SB.append("{");
		fromto(int i = 0 : Vs.size()) {
			if(i != 0) SB.append(", ");
			Object V = Vs.get(i);
			if(V ==> String) {
			 SB.append("\"");
			 SB.append(V);
			 SB.append("\"");
			} else             SB.append(@:toString(V));
		}
		SB.append("}");
		return SB.toString();
	};
	
	<?{ Returns hash value (not hashCode) of this SimpleSet. }?>
	@@:Method public hash():int {
		int    H  = 0;
		Vector Vs = this.Data;
		fromto(int i = 0 : Vs.size())
			H += @:hash(Vs.get(i));
		return H;
	};
};