package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspSmallStmtList extends AspStmt {
  ArrayList<AspSmallStmt> smallstmts = new ArrayList<>();
  boolean finalSemicolon = false; //denne settes lik true om det er et semikolon rett før newline

  AspSmallStmtList(int n) {
    super(n);
  }

  static AspSmallStmtList parse(Scanner s) {
    enterParser("small stmt list");
    AspSmallStmtList sl = new AspSmallStmtList(s.curLineNum());

    while (true) {
      sl.smallstmts.add(AspSmallStmt.parse(s));

      if (s.curToken().kind != semicolonToken) { break; }

      skip(s,semicolonToken);

      if (s.curToken().kind == newLineToken) {
        sl.finalSemicolon = true;
        break;
      }
    }

    skip(s,newLineToken);

    leaveParser("small stmt list");
    return sl;
  }

  @Override
  public void prettyPrint() {
    int i = 0;
    for (AspSmallStmt smallstmt: smallstmts) {
      if (i > 0) {
        prettyWrite("; ");
      }
      smallstmt.prettyPrint();
      i++;
    }
    if (finalSemicolon) {
      prettyWrite("; ");
    }
    prettyWriteLn();
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    for (AspSmallStmt ss : smallstmts) {
      ss.eval(curScope);
    }
    return null;
  }
}
