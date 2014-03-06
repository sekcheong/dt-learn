package ml.io.reader;



public class Token {
	
	public TokenType Type;
	public Object Value;
	
	Token(TokenType type, Object value) {
		this.Type=type;
		this.Value = value;
	}
	
}
