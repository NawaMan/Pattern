// @Curry:
@@:Package(nawaman~>data);

@@:Import(java.util.*);

<?{ Checks if the element in the collection is repeated. }?>
@@:SubRoutine public CheckElementRepeat(Col :Collection):boolean {
	return CheckElementRepeat(Col, null);
}

<?{ Checks if the element in the collection is repeated. }?>
@@:SubRoutine public CheckElementRepeat(Col :Collection, Order :Executable:<(Object):int>):boolean {
	if(Col == null) return false;
	
	int     PreviousOrder = -1;
	boolean IsRepeat      = false;
	
	if((Col ==> List) || (Col ==> Set)) {
		fromto(int i = 0 : Col->size().hash()) {
			Object Data = Col->get(i);
			int O = Order(Data);
			if(PreviousOrder == -1) {
				PreviousOrder = O;
				continue;
			}
				
			if(PreviousOrder == O) {
				IsRepeat = true;
				stop;
			}
					
			PreviousOrder = O;
		}
		return IsRepeat;
	}
	
	foreach(Object Data : Col) {
		int O = Order(Data);
		if(PreviousOrder == -1) {
			PreviousOrder = O;
			continue;
		}
			
		if(PreviousOrder == O) {
			IsRepeat = true;
			stop;
		}
				
		PreviousOrder = O;
	}
	return IsRepeat;
};

<?{ Compares the specified object with this entry for equality. }?>
@@:SubRoutine public CheckEquals_Vector(V:Vector, O:Object):boolean {
	// Can be compared to any list
	
	int Length = V.size();
	
	// Actual array -----------------------------------------------------------------
	cast(any[] Vs = O) {
		// If the length is no equals, return false
		if(Length != Vs.length) return false;
		
		// If the length is zero, they are equals
		if(Length == 0) return true;
		
		// Compare each value
		fromto(int I = 0 : Length)
			if(V.get(I) != Vs[I])
				return false;
	
	// Vable --------------------------------------------------------------------
	} else cast(curry=>Arrayable A = O) {
		// If the length is no equals, return false
		if(Length != A.length()) return false;
		
		// If the length is zero, they are equals
		if(Length == 0) return true;
		
		// Compare each value
		fromto(int I = 0 : Length)
			if(V.get(I) != A.get(I))
				return false;
	} else {
	
		// Iterable
	    	 cast(curry =>  Iterable I = O) { O = I.iterator(); }
		else cast(java.lang.Iterable I = O) { O = I.iterator(); } 
	
		// Iterator<any> ----------------------------------------------------------------
		cast(curry=>Iterator I = O) {
			// Compare each value
			int Count = 0;
			foreach(any Value : I) {
				if((Count >= Length) || (Value != V.get(Count))) return false;
				Count++;
			}
			if(Count != Length)
				return false;
		
		// Iterator ---------------------------------------------------------------------
		} else cast(java.util.Iterator I = O) {
			// Compare each value
			int Count = 0;
			foreach(any Value : I) {
				if((Count >= Length) || (Value != V.get(Count))) return false;
				Count++;
			}
			if(Count != Length)
				return false;
			
		} else {
			return false;
		}
	}
		
	return true;
};