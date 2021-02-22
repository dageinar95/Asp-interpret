package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspExprStmt extends AspSmallStmt {
  AspExpr expr;

  AspExprStmt(int n) {
    super(n);
  }

  static AspExprStmt parse(Scanner s) {
    enterParser("expr stmt");
    AspExprStmt es = new AspExprStmt(s.curLineNum());
    es.expr = AspExpr.parse(s);

    leaveParser("expr stmt");
    return es;
  }

  @Override
  public void prettyPrint() {
    expr.prettyPrint();
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
  	RuntimeValue v = expr.eval(curScope);
    trace("" + v.showInfo());
    return v;
  }
}
