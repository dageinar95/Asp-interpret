package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspArguments extends AspPrimarySuffix {
  ArrayList<AspExpr> exprs = new ArrayList<>();

  AspArguments(int n) {
    super(n);
  }

  static AspArguments parse(Scanner s) {
    enterParser("arguments");
    skip(s,leftParToken);
    AspArguments aa = new AspArguments(s.curLineNum());

    if (s.curToken().kind == rightParToken) {
      skip(s,rightParToken);
      leaveParser("arguments");
      return aa;
    }

    while (true) {
      aa.exprs.add(AspExpr.parse(s));
      if (s.curToken().kind != commaToken) { break; }
      skip(s,commaToken);
    }
    skip(s,rightParToken);

    leaveParser("arguments");
    return aa;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("(");
    int i = 0;
    for (AspExpr expr: exprs) {
      if (i > 0) {
        prettyWrite(", ");
      }
      expr.prettyPrint();
      i++;
    }
    prettyWrite(")");
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    ArrayList<RuntimeValue> args = new ArrayList();
    for (AspExpr e : exprs) {
      args.add(e.eval(curScope));
    }
    return new RuntimeListValue(args);
  }

}
