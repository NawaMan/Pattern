// @Curry:
@@:Package(nawaman~>data);

@@:Import(net.nawaman.util.CanBeImmutable);
@@:Import(curry=>Arrayable);

<?{ Simple implementation of an array of data }?>
@@:TypeDef public class SimpleArray<ValueType:any> implements Arrayable<ValueType>, CanBeImmutable {

	// Fields ----------------------------------------------------------------------------------------------------------

	@@:Field private Data:       ValueType[] =  null;
	@@:Field private IsReadOnly: boolean     = false;
	
	// Constructors ----------------------------------------------------------------------------------------------------

	<?{ Constructs an SimpleArray. }?>
	@@:Constructor public (Size:int) {
		this.Data = new ValueType[Size];
	};

	<?{ Constructs an SimpleArray. }?>
	@@:Constructor public (pData:ValueType[]) {
		if(pData == null) this.Data = new ValueType[0];
		else              this.Data = pData.clone();
	};
	
	// Arrayable<ValueType> --------------------------------------------------------------------------------------------

	<?{ Changes the element value. }?>
	@@:Method public set(I:int, Value:ValueType):ValueType {
		// Array in the range and not immutable
		if((I >= 0) && (I < this.Data.length) && !this.isImmutable()) this.Data[I] = Value;
		return Value;
	};
	
	<?{ Returns the element value. }?>
	@@:Method public get(I:int):ValueType {
		return this.Data[I];
	};
	
	<?{ Returns the length of the array. }?>
	@@:Method public length():int {
		return this.Data.length;
	};
	
	// Arrayable -------------------------------------------------------------------------------------------------------

	<?{ Changes the element value. }?>
	@@:Method public set(I:int, Value:Object):Object {
		if((Value != null) && !(Value instanceof ValueType))
			throw new IllegalArgumentException("Wrong data kind: `"+Value+"` for '"+ValueType.type+"'.");
			
		return this.set(I, (ValueType)Value);
	};
	
	// Iterable<ValueType> ---------------------------------------------------------------------------------------------
	
	<?{ Returns an iterator over a set of elements. }?>
	@@:Method public iterator():Iterator<ValueType> {
		return new ArrayIterator<ValueType>(this.Data.clone());
	};
	
	// MightBeImmutable ------------------------------------------------------------------------------------------------
	
	<?{ Checks if this SimpleArray is immutable. }?>
	@@:Method public isImmutable():boolean {
		return this.IsReadOnly;
	};
	
	// CanBeImmutable --------------------------------------------------------------------------------------------------
	
	<?{ Make this SimpleArray a immutable one. }?>
	@@:Method public toImmutable():boolean {
		return this.IsReadOnly = true;
	};
	
	// General ---------------------------------------------------------------------------------------------------------
	
	<?{ Compares the specified object with this entry for equality. }?>
	@@:Method public equals(O:Object):boolean {
		return SimpleArray.CheckEquals(this, O);
	};
	
	<?{ Returns string representation of this SimpleArray. }?>
	@@:Method public toString():String {
		StringBuffer SB = new StringBuffer();
		ValueType[]  Vs = this.Data;
		
		SB.append("[");
		fromto(int i = 0 : Vs.length) {
			if(i != 0) SB.append(", ");
			Object V = Vs[i];
			if(V ==> String) SB.append("\"").append(V).append("\"");
			else             SB.append(@:toString(V));
		}
		SB.append("]");
		return SB.toString();
	};
	
	<?{ Returns hash value (not hashCode) of this SimpleArray. }?>
	@@:Method public hash():int {
		int         H  = 0;
		ValueType[] Vs = this.Data;
		fromto(int i = 0 : Vs.length)
			H += (i + 10)*@:hash(Vs[i]);
		return H;
	};
	
	// Utilities -------------------------------------------------------------------------------------------------------
	
	<?{ Compares the specified object with this entry for equality. }?>
	@@:Method static public CheckEquals(Array:Arrayable, O:Object):boolean {
		// Can be compared to any list
		
		int Length = Array.length();
		
		// Actual array -----------------------------------------------------------------
		cast(any[] Vs = O) {
			// If the length is no equals, return false
			if(Length != Vs.length) return false;
			
			// If the length is zero, they are equals
			if(Length == 0) return true;
			
			// Compare each value
			fromto(int I = 0 : Length)
				if(Array.get(I) != Vs[I])
					return false;
		
		// Arrayable --------------------------------------------------------------------
		} else cast(Arrayable A = O) {
			// If the length is no equals, return false
			if(Length != A.length()) return false;
			
			// If the length is zero, they are equals
			if(Length == 0) return true;
			
			// Compare each value
			fromto(int I = 0 : Length)
				if(Array.get(I) != A.get(I))
					return false;
		} else {
		
			// Iterable
		    	 cast(curry =>  Iterable I = O) { O = I.iterator(); }
			else cast(java.lang.Iterable I = O) { O = I.iterator(); } 
		
			// Iterator<any> ----------------------------------------------------------------
			cast(curry=>Iterator I = O) {
				// Compare each value
				int Count = 0;
				foreach(any V : I) {
					if((Count >= Length) || (V != Array.get(Count))) return false;
					Count++;
				}
				if(Count != Length) return false;
			
			// Iterator ---------------------------------------------------------------------
			} else cast(java.util.Iterator I = O) {
				// Compare each value
				int Count = 0;
				foreach(any V : I) {
					if((Count >= Length) || (V != Array.get(Count))) return false;
					Count++;
				}
				if(Count != Length) return false;
				
			} else {
				return false;
			}
		}
			
		return true;
	};
};