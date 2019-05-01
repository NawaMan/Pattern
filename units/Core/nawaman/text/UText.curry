// @Curry:
@@:Package(nawaman~>text);

@@:Import(java.io.*);

<?{ Utilities for Text }?>
@@:TypeDef public class UText {

	@@:Const static public ERRMSG_FILE_TO_CREATE_FILE: String = "Fail to create the folder for the file '%s'.";

	<?{ Save a text in to a file }?>
	@@:Method static public SaveTextToFile(aFileName: Text, aText: Text):void {
		SaveTextToFile(new java.io.File(aFileName?.toString()), aText);
	};
	
	<?{ Save a text in to a file }?>
	@@:Method static public SaveTextToFile(aFile:java.io.File, aText: Text):void {
		if(aFile == null)
			throw new NullPointerException("Null file object");
			
		try {
		
			// Write the content to the file
			aFile.getParentFile().mkdirs();
			BufferedWriter TextOut = new BufferedWriter(new FileWriter(aFile));
			TextOut.write(aText?.toString());
			TextOut.close();
			
		} catch(IOException IOE) {
			throw new IOException(\f=(ERRMSG_FILE_TO_CREATE_FILE)=(aFile)?.toString());
		}
	};
	
};