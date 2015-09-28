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
    	//if (currTok == first(<command>))
    	switch(currTok) {
    		case SKIP_TOK:
    		case READ_TOK:
    		case WRITE_TOK:
    		case WHILE_TOK:
    		case IF_TOK:
    		//case first(<assign>):
    		case VARIABLE_TOK:
    			match(currTok);
    			commandseq2();
    			break;
    		default: 
    			error("in commandSeq()");
    			break;
    	}
    }
    private void commandseq2() {
    	if (currTok == COLON_TOK) {
    		match(COLON_TOK);
    		commandseq();
    	} else {
    		//lambda. Do nothing.
    	}
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
    			//TODO: Verify that this is correct.
    			match(IF_TOK);
    			boolexpr();
    			match(THEN_TOK);
    			commandseq();
    			if2();
    			break;
    		//if (curTok == first(<assign>))
    		case VARIABLE_TOK:
    			assign();
    			break;
    		default:
    			error("in command()");
    			break;
    	}
    }
    private void if2() {
    	//TODO: Verify that this is correct
    	if (currTok == END_TOK) {
    		match(END_TOK);
    		match(IF_TOK);
    	} else if (currTok == ELSE_TOK) {
    		match(ELSE_TOK);
    		commandseq();
    		match(END_TOK);
    		match(IF_TOK);
    	} else {
    		error("in if2()");
    	}
    }
    /**
     * Resolve left-common prefix:
     * 	<assign> ::= IDENTIFIER = <intexpr>
     * 			| IDENTIFIER :=: <boolexpr>
     * Where first(<assign>) = <assign2>
     */
    private void assign() {
    	if (currTok == VARIABLE_TOK) {
    		match(VARIABLE_TOK);
    		assign2();
    	} else {
    		error("in assign()");
    	}
    }
    /**
     * <assign2> ::= :=: <boolexpr> | = <intexpr>
     * first(<assign2>) = COLON_TOK | EQUAL_TOK
     */
    private void assign2() {
    	//Resolved common left prefix.
    	if (currTok == INTASSIGN_TOK) {
    		intexpr();
    	} else if (currTok == BOOLASSIGN_TOK) {
    		boolexpr();
    	} else {
    		error("in assign2()");
    	}
    }
    /**
     * <intexpr> ::= <intterm> | <intexpr> <weak_op> <intterm>
     * Resolved L-recursion.
     */
    private void intexpr() {
    	//TODO: Verify this is correct.
    	//if (currTok == first(<intterm>))
    	switch(currTok) {
    		case INTCONST_TOK:
    		case VARIABLE_TOK:
    		case LPAR_TOK:
    		case MINUS_TOK:
    			intexpr2();
    			break;
    		default:
    			error("in intexpr()");
    			break;
    	}
    }
    private void intexpr2() {
    	//TODO: Verify this is correct.
    	//if (currTok == first(<weak_op>))
    	if (currTok == PLUS_TOK || currTok == MINUS_TOK) {
    		weak_op();
    		intterm();
    		intexpr2();
    	} else {
    		//lambda, do nothing.
    	}
    }
    /**
     * <intterm> ::= <intterm> <strongop> <element> | <element>
     * Resolved L-recursion
     */
    private void intterm() {
    	//TODO: Verify this is correct.
    	//if (currTok == first(<element>)
    	switch(currTok) {
    		case INTCONST_TOK:
    		case VARIABLE_TOK:
    		case LPAR_TOK:
    		case MINUS_TOK:
    			intelement();
    			intterm2();
    			break;
    		default:
    			error("in intterm()");
    			break;
    	}
    }
    private void intterm2() {
    	//TODO: method body.
    	//if (currTok == first(<strong_op>))
    	if (currTok == MUL_TOK || currTok == DIV_TOK) {
    		strong_op();
    		intelement();
    		intterm2();
    	} else {
    		//lambda, do nothing. 
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
    			intelement();
    			break;
    		default:
    			error("in intelement()");
    			break;
    	}
    }
    /**
     * <boolexpr> ::= <boolexpr> or <boolterm> | <boolterm>
     * Left recursion problem. A->AaB | B
     */
    private void boolexpr() {
    	//TODO: Verify this is correct.
    	//if (currTok == first(<boolterm>))
    	switch(currTok) {
    		case TRUE_TOK:
    		case FALSE_TOK:
    		case NOT_TOK:
    		case LBRACK_TOK:
    		case VARIABLE_TOK:
    		//first(<intexpr>)
    		case INTCONST_TOK:
    		//case VARIABLE_TOK:
    		case LPAR_TOK:
    		case MINUS_TOK:
    			boolterm();
    			boolexpr2();
    			break;
    		default:
    			error("in boolexpr()");
    			break;
    	}
    }
    private void boolexpr2() {
    	//TODO: Verify this is correct.
    	if (currTok == OR_TOK) {
    		boolterm();
    		boolexpr2();
    	} else {
    		//lambda, do nothing. 
    	}
    }
    /**
     * <boolterm> ::= <boolterm> and <boolelement> | <boolelement>
     * Left recursion problem. A->AaB | B
     * 
     */
    private void boolterm() {
    	//Resolved L-recursion
    	//if first(<boolelement>)
    	switch(currTok) {
    		case TRUE_TOK:
    		case FALSE_TOK:
    		case NOT_TOK:
    		case LBRACK_TOK:
    		case VARIABLE_TOK:
    		//first(<intexpr>)?? 
    		case INTCONST_TOK:
    		//case VARIABLE_TOK
    		case LPAR_TOK:
    		case MINUS_TOK:
    			boolterm();
    			boolterm2();
    			break;
    	}
    }
    private void boolterm2() {
    	if(currTok == AND_TOK) {
    		match(AND_TOK);
    		boolelement();
    		boolterm2();
    	} else {
    		//lambda, do nothing.
    	}
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
    } /**
     * Fix common left prefix problem:
     * 	<relation> ::= <= | < | = | <> | >= | >
     * So that:
     * 	<relation> ::= <relation2> | = | <relation3>
     * Hence:
     *  first(<relation>) = first(<relation2>) U EQ_TOK U first(<relation3>)
     */
    private void relation() {
    	//TODO: Is this done correctly?
    	switch (currTok) {
    		case LE_TOK:
    			match(LE_TOK);
    			break;
    		case LT_TOK:
    			match(LT_TOK);
    			relation2();
    			break;
    		case EQ_TOK:
    			match(EQ_TOK);
    			break;
    		case GE_TOK:
    			match(GE_TOK);
    			break;
    		case GT_TOK:
    			match(GT_TOK);
    			break;
    		default:
    			error("in relation()");
    			break;
    	}
    }
    private void relation2() {
    	if (currTok == LT_TOK) {
    		match(LT_TOK);
    	} else {
    		//lambda, do nothing. 
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
    private void strong_op() {
    	if (currTok == MUL_TOK) {
    		match(MUL_TOK);
    	} else if (currTok == DIV_TOK) {
    		match(DIV_TOK);
    	} else {
    		error("in strong_op()");
    	}
    }
}