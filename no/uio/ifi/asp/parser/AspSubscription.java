package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspSubscription extends AspPrimarySuffix {
  AspExpr expr;

  AspSubscription(int n) {
    super(n);
  }

  static AspSubscription parse(Scanner s) {
    enterParser("subscription");
    skip(s,leftBracketToken);
    AspSubscription as = new AspSubscription(s.curLineNum());
    as.expr = AspExpr.parse(s);
    skip(s,rightBracketToken);

    leaveParser("subscription");
    return as;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("[");
    expr.prettyPrint();
    prettyWrite("]");
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue v = expr.eval(curScope);
    return v;
  }

}
