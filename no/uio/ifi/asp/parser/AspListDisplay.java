package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspListDisplay extends AspAtom {
  ArrayList<AspExpr> exprs = new ArrayList<>();

  AspListDisplay(int n) {
    super(n);
  }

  static AspListDisplay parse(Scanner s) {
    enterParser("list display");
    skip(s,leftBracketToken);
    AspListDisplay ld = new AspListDisplay(s.curLineNum());

    if (s.curToken().kind == rightBracketToken) { //om det er en tom liste
      skip(s,rightBracketToken);
      leaveParser("list display");
      return ld;
    }

    while (true) {
      ld.exprs.add(AspExpr.parse(s)); //denne parser neste expr og når ferdig vil s.curToken() gi oss neste komma om det er flere expr.
      if (s.curToken().kind != commaToken) { break; }
      skip(s,commaToken);
    }
    skip(s,rightBracketToken);

    leaveParser("list display");
    return ld;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("[");
    int i = 0;
    for (AspExpr expr: exprs) {
      if (i > 0) {
        prettyWrite(", ");
      }
      expr.prettyPrint();
      i++;
    }
    prettyWrite("]");
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    if (exprs.isEmpty()) { //om det er en tom liste
      return new RuntimeListValue();
    }
    RuntimeListValue v = new RuntimeListValue(exprs.get(0).eval(curScope)); //oppretter ListValue med første expr
    for (int i = 1; i < exprs.size(); i++) {
      v = new RuntimeListValue(v, exprs.get(i).eval(curScope)); //lager nytt ListValue-objekt med den foreløpige lista og neste expr
    }
    return v;
  }

}
