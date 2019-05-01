// @Curry:
@@:Package(pattern~>data);

@@:Import(net.nawaman.util.CanBeImmutable);

<?{ Simple Implementation of MapEntry. }?>
@@:TypeDef public class SimpleMapEntry<KeyType:any, ValueType:any> implements MapEntry<KeyType,ValueType>, CanBeImmutable {

	// Fields ----------------------------------------------------------------------------------------------------------

	@@:Field private Key:        KeyType   =  null;
	@@:Field private Value:      ValueType =  null;
	@@:Field private IsReadOnly: boolean   = false;
	
	// Constructors ----------------------------------------------------------------------------------------------------

	<?{ Constructs an MapEntry. }?>
	@@:Constructor public (pKey:KeyType, pValue:ValueType) {
		this.Key   = pKey;
		this.Value = pValue;
	};

	// MapEntry<KeyType:any, ValueType:any> ----------------------------------------------------------------------------
          
	<?{ Returns the key corresponding to this entry. }?>
	@@:Method public getKey():KeyType {
		return this.Key;
	};
          
	<?{ Replaces the value corresponding to this entry with the specified value (optional operation). }?>
	@@:Method public setValue(pValue:ValueType):ValueType {
		if(this.IsReadOnly) return null;
		return this.Value = pValue;
	};
          
	<?{ Returns the value corresponding to this entry. }?>
	@@:Method public getValue():ValueType {
		return this.Value;
	};

	// MapEntry --------------------------------------------------------------------------------------------------------
          
	<?{ Replaces the value corresponding to this entry with the specified value (optional operation). }?>
	@@:Method public setValue(pValue:any):any {
		if(pValue instanceof ValueType) return null;
		return this.setValue((ValueType)pValue);
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
		if(!(O instanceof java.util.Map.Entry)) return false;
		java.util.Map.Entry ME = (java.util.Map.Entry)O;
		return (this.Key == ME.getKey()) && (this.Value == ME.getValue());
	};
	
	<?{ Returns string representation of this SimpleArray. }?>
	@@:Method public toString():String {
		return "MayEntry {"+this.Key+":"+this.Value+"}";
	};
	
	<?{ Returns hash value (not hashCode) of this SimpleArray. }?>
	@@:Method public hash():int {
		return ("MayEntry {"+this.Key+":"+this.Value+"}").hash();
	};
	
};