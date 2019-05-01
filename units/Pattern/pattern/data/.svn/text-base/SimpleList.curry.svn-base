// @Curry:
@@:Package(pattern~>data);

@@:Import(java.util.Vector);
@@:Import(net.nawaman.util.CanBeImmutable);

<?{ Simple Implementation of List }?>
@@:TypeDef public class SimpleList <ValueType:any> implements List<ValueType>, CanBeImmutable {

	// Fields ----------------------------------------------------------------------------------------------------------

	@@:Field private Data:       Vector  = new Vector();
	@@:Field private IsReadOnly: boolean = false;
	
	// Constructors ----------------------------------------------------------------------------------------------------

	<?{ Constructs an SimpleList. }?>
	@@:Constructor public () {};

	<?{ Constructs an SimpleList. }?>
	@@:Constructor public (pData:ValueType[]) {
		if(pData == null) return;
		this.Data.addAll(java.util.Arrays.asList(pData));
	};

	<?{ Constructs an SimpleList. }?>
	@@:Constructor public (pData:Collection<ValueType>) {
		if(pData == null) return;
		this.Data.addAll(pData);
	};

	<?{ Constructs an SimpleList. }?>
	@@:Constructor public (pData:java.util.Collection) {
		if(pData == null) return;
		this.Data.addAll(pData);
	};
	
	// List<ValueType> -------------------------------------------------------------------------------------------------

	<?{ Adds element into the list }?>
	@@:Method public add(Value:ValueType):boolean {
		if(this.IsReadOnly) return false;
		return this.Data.add(Value);
	};

	<?{ Adds element into the list at the index }?>
	@@:Method public add(Index:int, Value:ValueType):boolean {
		if(this.IsReadOnly) return false;
		this.Data.add(Index,Value);
		return true;
	};

	<?{ Adds all elements of the given list into the list }?>
	@@:Method public addAll(Col:Collection<ValueType>):boolean {
		if(this.IsReadOnly) return false;
		return this.Data.add(Col);
	};

	<?{ Adds all elements of the given list into the list at the index }?>
	@@:Method public addAll(Index:int, Col:Collection<ValueType>):boolean {
		if(this.IsReadOnly) return false;
		return this.Data.addAll(Index,Col);
	};

	<?{ Changes the element value. }?>
	@@:Method public set(Index:int, Value:ValueType):ValueType {
		if(this.IsReadOnly) return false;
		 this.Data.set(Index, Value);
		 return Value;
	};
	
	<?{ Returns the element value. }?>
	@@:Method public get(Index:int):ValueType {
		return (ValueType)this.Data.get(Index);
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
	
	<?{ Checks if the list is empty }?>
	@@:Method public isEmpty():boolean {
		return this.Data.isEmpty();
	};
	
	<?{ Returns the size of the list }?>
	@@:Method public size():int {
		return this.Data.size();
	};
	
	<?{
		Returns the index in this list of the first occurrence of the specified element, or -1 if this list does not
		contain this element.
	}?>
	@@:Method public indexOf(Value:ValueType):int {
		return this.Data.indexOf(Value);
	};
	
	<?{
		Returns the index in this list of the last occurrence of the specified element, or -1 if this list does not
		contain this element.
	}?>
	@@:Method public lastIndexOf(Value:ValueType):int {
		return this.Data.lastIndexOf(Value);
	};
	
	<?{ Returns a list iterator of the elements in this list (in proper sequence). }?>
	@@:Method public listIterator():ListIterator<ValueType> {
		throw new UnsupportedOperationException("SimpleList does not support listIterator().");
	};
	
	<?{
		Returns a list iterator of the elements in this list (in proper sequence), starting at the specified position in
		this list.
	}?>
	@@:Method public listIterator(Index:int):ListIterator<ValueType> {
		throw new UnsupportedOperationException("SimpleList does not support listIterator(int).");
	};
	
	<?{ Removes a single instance of the specified element from this list, if it is present. }?>
	@@:Method public remove(Value:ValueType):boolean {
		if(this.IsReadOnly) return false;
		return this.Data.remove(Value);
	};
	
	<?{ Removes the element at the specified position in this list (optional operation). }?>
	@@:Method @#:IgnoreWhenRepeat: public remove(Index:int):ValueType {
		if(this.IsReadOnly) return false;
		return this.Data.remove(Index);
	};
	
	<?{ Removes the element at the specified position in this list (optional operation). }?>
	@@:Method public removeAt(Index:int):ValueType {
		if(this.IsReadOnly) return false;
		return this.Data.remove(Index);
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

	<?{ Returns a view of the portion of this list between the specified FromIndex, inclusive, and ToIndex, exclusive. }?>
	@@:Method public subList(FromIndex:int, ToIndex:int):List<ValueType> {
		throw new UnsupportedOperationException("SimpleList does not support subList(int,int).");
	};
	
	<?{ Returns the number of elements in this list. }?>
	@@:Method public toArray():ValueType[] {
		ValueType[] A = new ValueType[this.Data.size()];
		
		for(int i = 0; i < this.Data.size(); i++)
			A[i] = (ValueType)(this.Data.get(i));
		
		return ((ValueType[])A);
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
		
	// List ------------------------------------------------------------------------------------------------------------

	<?{ Adds element into the list }?>
	@@:Method public add(Value:any):boolean {
		if(this.IsReadOnly) return false;
		cast(ValueType V = Value) return this.Data.add(V);
		throw new IllegalArgumentException("Wrong data kind: `"+Value+"` for '"+ValueType.type+"'.");
	};

	<?{ Adds element into the list at the index }?>
	@@:Method public add(Index:int, Value:any):boolean {
		if(this.IsReadOnly) return false;
		cast(ValueType V = Value) {
			this.Data.add(Index,V);
			return true;
		}
		throw new IllegalArgumentException("Wrong data kind: `"+Value+"` for '"+ValueType.type+"'.");
	};

	<?{ Adds all elements of the given list into the list }?>
	@@:Method public addAll(Col:java.util.Collection):boolean {
		if(Col == null) return true;
		foreach(any V : Col) if(!this.add(V)) return false;
		return true;
	};

	<?{ Adds all elements of the given list into the list at the index }?>
	@@:Method public addAll(Index:int, Col:java.util.Collection):boolean {
		if(Col == null) return true;
		foreach(any V : Col) if(!this.add(Index + $Count$, V)) return false;
		return true;
	};

	<?{ Changes the element value. }?>
	@@:Method public set(Index:int, Value:any):any {
		cast(ValueType V = Value) return this.set(Index, V);
		throw new IllegalArgumentException("Wrong data kind: `"+Value+"` for '"+ValueType.type+"'.");
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
	
	<?{
		Returns the index in this list of the first occurrence of the specified element, or -1 if this list does not
		contain this element.
	}?>
	@@:Method public indexOf(Value:any):int {
		cast(ValueType V = Value) return this.indexOf(V);
		return -1;
	};
	
	<?{
		Returns the index in this list of the last occurrence of the specified element, or -1 if this list does not
		contain this element.
	}?>
	@@:Method public lastIndexOf(Value:any):int {
		cast(ValueType V = Value) return this.lastIndexOf(V);
		return -1;
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
	
	<?{ Checks if this SimpleList is immutable. }?>
	@@:Method public isImmutable():boolean {
		return this.IsReadOnly;
	};
	
	// CanBeImmutable --------------------------------------------------------------------------------------------------
	
	<?{ Make this SimpleList a immutable one. }?>
	@@:Method public toImmutable():boolean {
		return this.IsReadOnly = true;
	};
	
	// General ---------------------------------------------------------------------------------------------------------
	
	<?{ Compares the specified object with this entry for equality. }?>
	@@:Method public equals(O:Object):boolean {
		return pattern~>data:>CheckEquals_Vector(this.Data, O);
	};
	
	<?{ Returns string representation of this SimpleList. }?>
	@@:Method public toString():String {
		StringBuffer SB = new StringBuffer();
		Vector       Vs = this.Data;
		
		SB.append("[");
		fromto(int i = 0 : Vs.size()) {
			if(i != 0) SB.append(", ");
			Object V = Vs.get(i);
			if(V ==> String) SB.append("\"").append(V).append("\"");
			else             SB.append(@:toString(V));
		}
		SB.append("]");
		return SB.toString();
	};
	
	<?{ Returns hash value (not hashCode) of this SimpleList. }?>
	@@:Method public hash():int {
		int    H  = 0;
		Vector Vs = this.Data;
		fromto(int i = 0 : Vs.size())
			H += (i + 10)*@:hash(Vs.get(i));
		return H;
	};
};