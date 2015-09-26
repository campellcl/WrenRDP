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
    /**
     *  <block> ::= <decseq> begin <commandfqseq> end
     *  The issue here is that:
     * 	first(<block>) = first(<decseq>); Whereas,
     *  <decseq> ::= <dec> <decseq> | lambda
     *  Since there is a lambda production in <decseq>,
     *  we must union first(<decseq>) with first(begin). 
     *  Hence, first(<block>) = first(<decseq>) U begin;
     *  	where first(<decseq>) = <dec>
     *  		and first(<dec>) = var.
     *  So that: first(<block>) = VAR_TOK Union BEGIN_TOK.
     */
    private void block() {
    	if (currTok == VAR_TOK || currTok == BEGIN_TOK) {		
    		decseq();
    		match(BEGIN_TOK);
    		commandseq();
    		match(END_TOK);
    	} else {
    		error("in block()");
    	}
    }
    /**
     * Here we have <decseq> ::= <dec> <decseq> | lambda
     * So that first(<decseq>) = first(<dec>)
     * 	where first(<dec>) = var
     *  	so that first(<dec>) = VAR_TOK. 
     */
    private void decseq() {
    	//if currTok = first(<dec>).
    	if (currTok == VAR_TOK)
    	{
    		dec();
    		decseq();
    	} else {
    		//Lambda, do nothing. 
    	}
    }
    /**
     * Nice and easy, since:
     * 	first(<dec>) = VAR_TOK
     */
    private void dec() {
    	if (currTok == VAR_TOK) {
    		match(VAR_TOK);
    		varlist();
    		match(COLON_TOK);
    		type();
    		match(SEMICOLON_TOK);
    	} else {
    		error("in dec()");
    	}
    }
    /**
     * first(<type>) = INT_TOK | BOOL_TOK
     */
    private void type() {
    	if (currTok == INT_TOK) {
    		match(INT_TOK);
    	} else if (currTok == BOOL_TOK) {
    		match(BOOL_TOK);
    	} else {
    		error("in type()");
    	}
    }
    /**
     * Left common prefix since:
     * <varlist> = IDENTIFER | IDENTIFER, <varlist>
     * 	the first(<varlist>) = IDENTIFER | IDENTIFIER
     * We must modify the grammar so that:
     * first(<varlist>) = <varlist2>
     * 	where first(<varlist2>) = COMMA_TOK | lambda
     */
    private void varlist() {
    	//Resolved left common prefix.
    	if (currTok == VARIABLE_TOK) {
    		match(VARIABLE_TOK);
    		varlist2();
    	} else {
    		error("in varlist()");
    	}
    }
    /**
     * Resolved left common prefix in <varlist>
     * <varlist2> = , <varlist> | lambda
     */
    private void varlist2() {
    	if (currTok == COMMA_TOK) {
    		match(COMMA_TOK);
    		varlist();
    	} else {
    		//Lambda. Do nothing.
    	}
    }
    /**
     * Resolve left common prefix in production:
     * 	<commandseq> ::= <command> | <command> ; <commandseq>
     * Create a new non-terminal:
     * 	<commandseq> ::= <commandseq2> ; <commandseq> | <commandseq2>
     * So that first(<commandseq>) = first(<commandseq2>)
     * 	
     */
    private void commandseq() {
    	//Resolved left common prefix. 
    	//TODO: Help filling out if statement?
    	//	He said the if-check was still required.
    	//if (currTok == first(<command>)
    	switch(currTok) {
    		case SKIP_TOK:
    		case READ_TOK:
    		case WRITE_TOK:
    		case WHILE_TOK:
    		case IF_TOK:
    		//case first(<assign>):
    		//TODO: find first(<assign>);
    		//case first(<commandseq>)
    		//TODO: resolve left common prefix in
    		//	first(<commandseq>)
    		default: 
    			error("in commandSeq()");
    			break;
    	}
    	/*
    	if (currTok == ?) {
    		command();
    	} else {
    		error("in commandseq()");
    	}
    	*/
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
    			//TODO: Warning: Nested If.
    			//	Not sure if this is done according to correct methodology. 
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
    		case VARIABLE_TOK:
    			//IDENTIFIER
    			match(VARIABLE_TOK);
    		/*
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
    	//TODO: method body.
    }
    private void intexpr() {
    	//TODO: method body. Resolve L-recursion
    }
    private void intexpr2() {
    	//TODO: method body.
    }
    private void intterm() {
    	//TODO: method body.
    }
    private void intterm2() {
    	//TODO: method body.
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
    	switch(currTok) {
    		case INTCONST_TOK:
    			match(INTCONST_TOK);
    			break;
    		case VARIABLE_TOK:
    			match(VARIABLE_TOK);
    			break;
    		case LPAR_TOK:
    			match(LPAR_TOK);
    			intexpr();
    			match(RPAR_TOK);
    			break;
    		case MINUS_TOK:
    			match(MINUS_TOK);
    			//TODO: Recursive call correct here?
    			intelement();
    			break;
    		default:
    			error("in intelement()");
    			break;
    	}
    }
    private void boolexpr() {
    	//TODO: method body.
    }
    private void boolexpr2() {
    	//TODO: method body.
    }
    private void boolterm() {
    	//TODO: method body.
    }
    private void boolterm2() {
    	//TODO: method body.
    }
    private void relation() {
    	//TODO: method body.
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
    /**
     * Instructor provided function below
     * DO NOT MODIFY
     */
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