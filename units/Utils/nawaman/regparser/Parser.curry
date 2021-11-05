// @Curry:
@@:Package(nawaman~>regparser);

@@:Import(net.nawaman.regparser.RegParser);
@@:Import(net.nawaman.regparser.result.ParseResult);

<?{ A type of a Parser }?>
@@:TypeDef Variant Parser as <CharSequence|RegParser> {

	@@:Method getRegParser():RegParser {
		return (this ==> RegParser) ? (RegParser)this : RegParser.newRegParser((String)this);
	};
	
	@@:Method Parse(aText:String):ParseResult {
		return Parse(this, aText);
	};

	@@:Method Compile(aText:String, aCompiler:Executable:<(ParseResult):Object>):Object {
		return Compile(this, aText, aCompiler);
	};
	
};