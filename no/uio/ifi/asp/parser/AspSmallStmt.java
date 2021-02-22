package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public abstract class AspSmallStmt extends AspSyntax {

  AspSmallStmt(int n) {
    super(n);
  }

  static AspSmallStmt parse(Scanner s) {
    enterParser("small stmt");
    AspSmallStmt ss;

    if (s.curToken().kind == nameToken && s.anyEqualToken()) { //om det er et equal-token, vet vi det er assigment og ikke expr stmt
      ss = AspAssignment.parse(s);
    }
    else if (s.curToken().kind == passToken) {
      ss = AspPassStmt.parse(s);
    }
    else if (s.curToken().kind == returnToken) {
      ss = AspReturnStmt.parse(s);
    }
    else {
      ss = AspExprStmt.parse(s);
    }

    leaveParser("small stmt");
    return ss;
  }

  abstract void prettyPrint();
  abstract RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue;
}
