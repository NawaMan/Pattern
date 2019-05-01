// @Curry:
@@:Package(pattern~>text);

@@:Import(net.nawaman.curry.CurryError);
@@:Import(net.nawaman.text.AsText);

<?{ A type }?>
@@:TypeDef public variant Text as <any||:char:|char[]|CharSequence|HasCharSequence|AsText> implements CharSequence {
	
	<?{ Returns the char value at the specified index. }?>
	@@:Method public charAt(index:int):char {
		if(index >= 0) {
			Object O = this;
			
			     cast(HasCharSequence HCS  = O) O = HCS .getCharSequence();
			else cast(AsText          ATxt = O) O = ATxt.asText();
			
			cast(char         C  = O) { if(index == 0)          return C;                } else
			cast(char[]       Cs = O) { if(index < Cs.length  ) return Cs[index];        } else
			cast(CharSequence CS = O) { if(index < CS.length()) return CS.charAt(index); } else
			this.throwNonMemberError();
		}
		throw new IndexOutOfBoundsException(\f"Char at (%d)"(index));
	};
	
	<?{ Returns the length of this character sequence. }?>
	@@:Method public length():int {
		Object O = this;
			
		     cast(HasCharSequence HCS  = O) O = HCS .getCharSequence();
		else cast(AsText          ATxt = O) O = ATxt.asText();
			
		cast(char         C  = O) return 1;           else
		cast(char[]       Cs = O) return Cs.length;   else
		cast(CharSequence CS = O) return CS.length(); else
		this.throwNonMemberError(); 
	};
	
	<?{ Returns a new CharSequence that is a subsequence of this sequence. }?>
	@@:Method public subSequence(start:int, end:int):CharSequence {
		if((start < 0) || (end < 0) || (start > end) || (end <= this.length()))
			throw new IndexOutOfBoundsException(\f"Sub Sequence (%d-%d)"(start, end));
	
		Object O = this;	
		     cast(HasCharSequence HCS  = O) O = HCS .getCharSequence();
		else cast(AsText          ATxt = O) O = ATxt.asText();
			
		cast(char         C  = O) { return "" + C;                                   } else
		cast(char[]       Cs = O) { return (new String(Cs)).subSequence(start, end); } else
		cast(CharSequence CS = O) { return CS              .subSequence(start, end); } else
		this.throwNonMemberError();
	};
	
	<?{ Returns a string containing the characters in this sequence in the same order as this sequence. }?>
	@@:Method public toString():String {
		Object O = this;	
		     cast(HasCharSequence HCS  = O) O = HCS .getCharSequence();
		else cast(AsText          ATxt = O) O = ATxt.asText();
			
		cast(char         C  = O) return "" + C;         else
		cast(char[]       Cs = O) return new String(Cs); else
		cast(CharSequence CS = O) return CS.toString();  else
		this.throwNonMemberError(); 
	};
	
	<?{ Returns a string containing the characters in this sequence in the same order as this sequence. }?>
	@@:Method public getCharSequence():CharSequence {
		return this.toCharSequence();
	};
	
	<?{ Returns a string containing the characters in this sequence in the same order as this sequence. }?>
	@@:Method public toCharSequence():CharSequence {
		Object O = this;
		cast(HasCharSequence HCS  = O) return HCS.getCharSequence();	
		cast(AsText          ATxt = O) return ATxt.asText();
		cast(CharSequence    CS   = O) return CS;
			
		cast(char   C  = O) return "" + C;         else
		cast(char[] Cs = O) return new String(Cs); else
		this.throwNonMemberError(); 
	};
	
	<?{---
	Throw an error in case that this is not one of the member.
	NOTE: This only occurs when a new member is added but the method is forgoted to updated.
	---}?>
	@@:Method private throwNonMemberError():void {
		throw new CurryError(\f"Invalid object in Variant type: `%s` for '%s' <Text:64>."(this, $Type$)); 
	};
};