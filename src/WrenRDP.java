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
    	if (currTok == PROG_TOK) {
    		match(PROG_TOK);
    		match(VARIABLE_TOK);
    		match(IS_TOK);
    		block();
    	} else {
    		error("in program()");
    	}
    }
    private void block() {
    	if (currTok == VARIABLE_TOK || currTok == BEGIN_TOK) {		
    		decseq();
    		match(BEGIN_TOK);
    		commandseq();
    		match(END_TOK);
    	} else {
    		error("in block()");
    	}
    }
    private void decseq() {
    	//TODO: How to do lambda sequence & recursion?
    	/*
    	 * if (currTok == LAMBDA_TOK) {
    	 * 		//do nothing
    	 * } else {
    	 * 		error("in decseq()");
    	 * }
    	dec();
    	decseq();
    	*/
    }
    private void dec() {
    	if (currTok == VAR_TOK) {
    		match(VAR_TOK);
    		varlist();
    		type();
    	} else {
    		error("in dec()");
    	}
    }
    private void type() {
    	if (currTok == INT_TOK) {
    		match(INT_TOK);
    	} else if (currTok == BOOL_TOK) {
    		match(BOOL_TOK);
    	} else {
    		error("in type()");
    	}
    }
    private void varlist() {
    	//Resolved left common prefix.
    	if (currTok == VARIABLE_TOK) {
    		match(VARIABLE_TOK);
    		varlist2();
    	} else {
    		error("in varlist()");
    	}
    }
    private void varlist2() {
    	//There might be an error below...
    	if (currTok == VARIABLE_TOK) {
    		match(VARIABLE_TOK);
    		match(COMMA_TOK);
    		varlist();
    	} else {
    		error("in varlist2()");
    	}
    }
    /**
     * Need help with this method.
     * how to deal with recursion here?
     */
    private void commandseq() {
    	//TODO: Resolve common left prefix
    	//This does not seem right...
    	command();
    }
    private void commandseq2() {
    }
    private void command() {
    	switch(currTok) {
    		case SKIP_TOK:
    			match(SKIP_TOK);
    			break;
    		case READ_TOK:
    			match(READ_TOK);
    			match(VARIABLE_TOK);
    			break;
    		case WRITE_TOK:
    			match(WRITE_TOK);
    			intexpr();
    			break;
    		case WHILE_TOK:
    			match(WHILE_TOK);
    			boolexpr();
    			match(DO_TOK);
    			commandseq();
    			match(END_TOK);
    			match(WHILE_TOK);
    			break;
    		case IF_TOK:
    			//Not sure if this is done according to correct methodology. 
    			match(IF_TOK);
    			boolexpr();
    			match(THEN_TOK);
    			commandseq();
    			if (currTok == END_TOK) {
    				match(END_TOK);
    				match(IF_TOK);
    				break;
    			} else if (currTok == ELSE_TOK) {
    				match(ELSE_TOK);
    				break;
    			} else {
    				error("in command()");
    				break;
    			}
    		//TODO: Fix below code.
    		/*
    		case assign():
    			//do something?
    		case commandseq():
    			//do something?
    		*/
    		default:
    			error("in command()");
    			break;
    	}
    }
    private void assign2() {
    	//TODO: I was very tired... make sure this is right.
    	match(VARIABLE_TOK);
    	if (currTok == EQ_TOK) {
    		match(EQ_TOK);
    		intexpr();
    	} else if (currTok == COLON_TOK) {
    		match(COLON_TOK);
    		match(EQ_TOK);
    		match(COLON_TOK);
    		boolexpr();
    	} else {
    		error("in assign2()");
    	}
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
    	if (currTok == MUL_TOK) {
    		match(MUL_TOK);
    	} else if (currTok == DIV_TOK) {
    		match(DIV_TOK);
    	} else {
    		error("in strong_op()");
    	}
    }
    private void weak_op() {
    	if (currTok == PLUS_TOK) {
    		match(PLUS_TOK);
    	} else if (currTok == MINUS_TOK) {
    		match(MINUS_TOK);
    	} else {
    		error("in weak_op()");
    	}
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
    	/* Figure out how to do this one...
    	switch (currTok) {
    		case LE_TOK:
    			match(LE_TOK);
    			break;
    		case LT_TOK:
    			match(LT_TOK);
    			break;
    		case EQ_TOK:
    			match(EQ_TOK);
    			break;
    		case <>:
    			match(?)
    			break;
    		default:
    			error("in relation()");
    			break;
    	}
    	*/
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