package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspInnerExpr extends AspAtom {
  AspExpr expr;

  AspInnerExpr(int n) {
    super(n);
  }

  static AspInnerExpr parse(Scanner s) {
    enterParser("inner expr");
    skip(s,leftParToken);
    AspInnerExpr ie = new AspInnerExpr(s.curLineNum());
    ie.expr = AspExpr.parse(s);
    skip(s,rightParToken);

    leaveParser("inner expr");
    return ie;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("(");
    expr.prettyPrint();
    prettyWrite(")");
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue v = expr.eval(curScope);
    return v;
  }

}
