package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspSuite extends AspSyntax {
  ArrayList<AspStmt> stmts = new ArrayList<>();
  AspSmallStmtList smallstmtlist = null;

  AspSuite(int n) {
    super(n);
  }

  public static AspSuite parse(Scanner s) {
    enterParser("suite");
    AspSuite as = new AspSuite(s.curLineNum());

    //Assignment, pass stmt og return stmt
    // if ((s.curToken().kind == nameToken && s.anyEqualToken()) || s.curToken().kind == passToken ||
    // s.curToken().kind == returnToken || s.isFactorPrefix() || s.curToken().kind == integerToken
    // || s.curToken().kind == floatToken || s.curToken().kind == stringToken
    // || s.curToken().kind == trueToken || s.curToken().kind == falseToken
    // || s.curToken().kind == noneToken || s.curToken().kind == leftParToken
    // || s.curToken().kind == leftBracketToken og til slutt test for innerExpr) {
    //   as.smallstmtlist = AspSmallStmtList.parse(s);
    // }

    //newlineToken
    if (s.curToken().kind == newLineToken) {
      skip(s,newLineToken);
      skip(s,indentToken);
      while (true) {
        as.stmts.add(AspStmt.parse(s));
        if (s.curToken().kind == dedentToken) { break; }
      }
      skip(s,dedentToken);
    }

    //ExprStmt (sparer linjer ved å pakke inn denne i else-setning)
    else {
     as.smallstmtlist = AspSmallStmtList.parse(s);
    }

    leaveParser("suite");
    return as;
  }


  @Override
  public void prettyPrint() {
    if (smallstmtlist != null) {
      smallstmtlist.prettyPrint();
    }

    else {
      prettyWriteLn();
      prettyIndent();
      for (AspStmt stmt: stmts) {
        stmt.prettyPrint();
      }
      prettyDedent();
    }
  }


  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue v = null;
  	if (smallstmtlist != null) {
      smallstmtlist.eval(curScope);
    }
    for (AspStmt as : stmts) {
      as.eval(curScope);
    }
    return null;
  }

}
