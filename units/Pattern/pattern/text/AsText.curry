// @Curry:
@@:Package(pattern~>text);

<?{ Object with a CharSequence }?>
@@:TypeDef public interface AsText extends net.nawaman.text.AsText {

	<?{ Returns a char sequence }?>
	@@:Method public asText():CharSequence;

};