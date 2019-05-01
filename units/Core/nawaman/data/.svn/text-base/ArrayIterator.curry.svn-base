// @Curry:
@@:Package(nawaman~>data);

@@:Import(curry=>Iterator);

<?{ Iterator for an array }?>
@@:TypeDef public class ArrayIterator<ValueType:any> implements Iterator<ValueType>, java.util.Iterator {

	@@:Field private Data: ValueType[] = null;
	@@:Field private Index:int         =    0;

	<?{ Constructs an ArrayInterator. }?>
	@@:Constructor public (pData:ValueType[]) {
		if(pData == null) this.Data = new ValueType[0];
		else              this.Data = pData.clone();
	};
	
	// Iterator --------------------------------------------------------------------------------------------------------

	<?{ Returns true if the iteration has more elements. }?>
	@@:Method public hasNext():boolean {
		return (this.Index < this.Data.length);
	};

	<?{ Returns the next element in the iteration. }?>
	@@:Method public next():ValueType {
		if(!this.hasNext()) return null;
		return this.Data[this.Index++];
	};

	<?{ Removes from the underlying collection the last element returned by the iterator (optional operation) }?>
	@@:Method public remove():void {
		throw new UnsupportedOperationException("ArrayIterator does not support remove operation.");
	};
	
	// General ---------------------------------------------------------------------------------------------------------
	
	<?{ Returns hash value (not hashCode) of this SimpleArray. }?>
	@@:Method public hash():int {
		return this.Data.hash();
	};
	
};