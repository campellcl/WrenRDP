public class WrenRDP extends RDP implements WrenTokens {
    public static void main(String args[]) {
	String input = "";
	if (args.length == 0) {
	    System.out.println("Need program-string arg.");
	    System.out.println("Example use: java WrenRDP \"`cat test.w`\"");
	    System.exit(1);
	}
	for (String s : args)
	    input = input + s + "\n";

	WrenRDP p = new WrenRDP(input);
	p.parse();
    }
    public WrenRDP(String input) {
	lexer = new WrenLexer(input);
    }
    public void StartSymbol() {
	program();
    }
    private void program() {
    }
    private void block() {
    }
    private void decseq() {
    }
    private void dec() {
    }
    private void type() {
    }
    private void varlist() {
    }
    private void varlist2() {
    }
    private void commandseq() {
    }
    private void commandseq2() {
    }
    private void command() {
    }
    private void assign2() {
    }
    private void if2() {
    }
    private void intexpr() {
    }
    private void intexpr2() {
    }
    private void intterm() {
    }
    private void intterm2() {
    }
    private void strong_op() {
    }
    private void weak_op() {
    }
    private void intelement() {
    }
    private void boolexpr() {
    }
    private void boolexpr2() {
    }
    private void boolterm() {
    }
    private void boolterm2() {
    }
    private void relation() {
    }
    private void boolelement() {
	if (currTok == TRUE_TOK) match(TRUE_TOK);
	else if (currTok == FALSE_TOK) match(FALSE_TOK);
	else if (currTok == NOT_TOK) {
	    match(NOT_TOK);
	    match(LBRACK_TOK);
	    boolexpr();
	    match(RBRACK_TOK);
	}
	else if (currTok == LBRACK_TOK) {
	    match(LBRACK_TOK);
	    boolexpr();
	    match(RBRACK_TOK);
	}
	else if (currTok == VARIABLE_TOK) {
	    // jbf : VARIABLE ALONE OR COMPARISON, PREDICTION PROBLEM
	    currTok = lexer.getToken();
	    switch (currTok) {
	    case LT_TOK : case LE_TOK: case GT_TOK: 
	    case GE_TOK: case EQ_TOK: case NE_TOK:
		relation(); intexpr();
		break;
	    case MUL_TOK : case DIV_TOK: 
		intterm2(); relation(); intexpr();
		break;
	    case PLUS_TOK : case MINUS_TOK: 
	        intexpr2(); relation(); intexpr();
		break;
	    }
	}
	else if (currTok == INTCONST_TOK || currTok == LPAR_TOK || currTok == MINUS_TOK) {
	    intexpr();
	    relation();
	    intexpr();
	}
	else error("boolelement");
    }
}