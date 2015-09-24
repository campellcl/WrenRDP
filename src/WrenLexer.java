public class WrenLexer implements Lexer,WrenTokens {
    char[] inStr;
    String str;
    int chIndex;
    int eofIndex;
    int lines;

    public WrenLexer(String input) {
	str = input + " ";
	inStr = str.toCharArray();
	chIndex = 0;
	eofIndex = input.length();
	lines = 1;
    }

    public int getLineNum() { return lines; }
    private void error(String msg) {
	System.out.println("LexError: "+msg);
	System.exit(1);
    }

    public int getToken() {
	skip_whitespace();
	while (skip_comment()) ;
	if (chIndex >= eofIndex) return EOP_TOK;

	char c = inStr[chIndex];
	if ('a' <= c && c <= 'z') return kv_token();
	else if ('0' <= c && c <= '9') return n_token();
	else return sym_token();
    }

    private boolean skip_comment() {
	if (inStr[chIndex] == '/' && inStr[chIndex+1] == '/') {
	    while (chIndex < eofIndex && inStr[chIndex]!='\n')
		chIndex++;
	    skip_whitespace();
	    return true;
	}
	return false;
    }
    private void skip_whitespace() {
	while (chIndex < eofIndex && isWS(inStr[chIndex]))
	    chIndex++;
    }
    private int kv_token() {
	int tokStart = chIndex;
	while (('a' <= inStr[chIndex] && inStr[chIndex] <= 'z')
	       ||
	       ('0' <= inStr[chIndex] && inStr[chIndex] <= '9')
	       ||
	       (inStr[chIndex] == '_'))
	    chIndex++;
	int tokEnd = chIndex;
	String kv = str.substring(tokStart, tokEnd);
	if ("program".equals(kv)) return PROG_TOK;
	else if ("is".equals(kv)) return IS_TOK;
	else if ("begin".equals(kv)) return BEGIN_TOK;
	else if ("end".equals(kv)) return END_TOK;
	else if ("var".equals(kv)) return VAR_TOK;
	else if ("integer".equals(kv)) return INT_TOK;
	else if ("boolean".equals(kv)) return BOOL_TOK;
	else if ("skip".equals(kv)) return SKIP_TOK;
	else if ("read".equals(kv)) return READ_TOK;
	else if ("write".equals(kv)) return WRITE_TOK;
	else if ("while".equals(kv)) return WHILE_TOK;
	else if ("do".equals(kv)) return DO_TOK;
	else if ("if".equals(kv)) return IF_TOK;
	else if ("then".equals(kv)) return THEN_TOK;
	else if ("else".equals(kv)) return ELSE_TOK;
	else if ("or".equals(kv)) return OR_TOK;
	else if ("and".equals(kv)) return AND_TOK;
	else if ("true".equals(kv)) return TRUE_TOK;
	else if ("false".equals(kv)) return FALSE_TOK;
	else if ("not".equals(kv)) return NOT_TOK;
	else return VARIABLE_TOK;
    }
    private int n_token() {
	int tokStart = chIndex;
	while ('0' <= inStr[chIndex] && inStr[chIndex] <= '9')
	    chIndex++;
	int tokEnd = chIndex;
	return INTCONST_TOK;
    }
    private boolean isWS(char c) {
	if (c == '\n') lines++;
	return c == ' ' || c == '\t' || c == '\n';
    }
    private int sym_token() {
	char c = inStr[chIndex++];
	switch (c)  {
	case ':' : 
	    if (inStr[chIndex] == '=') {
		chIndex++;
		if (inStr[chIndex]==':') {
		    chIndex++;
		    return BOOLASSIGN_TOK;
		}
		return INTASSIGN_TOK;
	    }
	    else return COLON_TOK;
	case ';' : return SEMICOLON_TOK;
	case ',' : return COMMA_TOK;
	case '(' : return LPAR_TOK;
	case ')' : return RPAR_TOK;
	case '[' : return LBRACK_TOK;
	case ']' : return RBRACK_TOK;
	case '-' : return MINUS_TOK;
	case '+' : return PLUS_TOK;
	case '*' : return MUL_TOK;
	case '/' : return DIV_TOK;
	case '=' : return EQ_TOK;
	case '>' :
	    if (inStr[chIndex] == '=') {
		chIndex++;
		return GE_TOK;
	    }
	    else return GT_TOK;
	case '<' :
	    if (inStr[chIndex] == '=') {
		chIndex++;
		return LE_TOK;
	    }
	    else if (inStr[chIndex] == '>') {
		chIndex++;
		return NE_TOK;
	    }
	    else return LT_TOK;
	default: error("lexing symbol lexeme, unknown char "+c);
	}
	return -1;
    }
}