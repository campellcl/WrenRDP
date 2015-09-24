public abstract class RDP {
    protected Lexer lexer;
    protected int currTok;

    abstract void StartSymbol();
    
    protected void error(String msg) {
	System.out.println("PARSE ERROR("+lexer.getLineNum()+"): " + msg);
	System.exit(1);
    }
    protected void error() {
	error("");
    }
    protected void match(int expTok) {
	if (expTok == currTok) currTok = lexer.getToken();
	else error("Expected tok#"+expTok+", but read tok#"+currTok);
    }
    protected void parse() {
	currTok = lexer.getToken();
	StartSymbol();
	if (currTok != Tokens.EOP_TOK)
	    error("Extraneous input after program??");
	System.out.println("Parse successful.");
    }
}