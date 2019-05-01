// @Curry:
@@:Package(nawaman~>regparser);

@@:Import(net.nawaman.regparser.RegParser);
@@:Import(net.nawaman.regparser.ParseResult);

@@:Sub Parse(aParser: Parser, aText:String):ParseResult {
	if(aParser == null) return null;

	RegParser aRParser = aParser.getRegParser();
	if(aRParser == null) return null;
	
	return aRParser.parse(aText);
};

@@:Sub Compile(aParser: Parser, aText:String, aCompiler:Executable:<(ParseResult):Object>):Object {
	if(aParser == null) return null;

	RegParser aRParser = aParser.getRegParser();
	if(aRParser == null) return null;
	
	ParseResult aPResult = aRParser.parse(aText);
	return aCompiler(aPResult);
};