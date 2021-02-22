package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public abstract class AspStmt extends AspSyntax {

    AspStmt(int n) {
	     super(n);
    }

  static AspStmt parse(Scanner s) {
  	enterParser("stmt");
    AspStmt as;

    //Assignment, pass stmt og return stmt
    if ((s.curToken().kind == nameToken && s.anyEqualToken()) || s.curToken().kind == passToken ||
    s.curToken().kind == returnToken) {
      as = AspSmallStmtList.parse(s);
    }

    //if, for, while eller def-stmt
    else if (s.curToken().kind == ifToken || s.curToken().kind == forToken ||
    s.curToken().kind == whileToken || s.curToken().kind == defToken) {
      as = AspCompoundStmt.parse(s);
    }

    //hvis ingen av testene over slår til, skal det parses et ExprStmt, altså via SmallStmtList.
    //Gjør dette i else-sjekken for å slippe ekstra linjer
    else {
      as = AspSmallStmtList.parse(s);
    }

  	leaveParser("stmt");
  	return as;
  }

    abstract void prettyPrint();
    abstract RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue;
}
