package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspDictDisplay extends AspAtom {
  ArrayList<AspStringLiteral> stringLits = new ArrayList<>();
  ArrayList<AspExpr> exprs = new ArrayList<>();

  AspDictDisplay(int n) {
    super(n);
  }

  static AspDictDisplay parse(Scanner s) {
    enterParser("dict display");
    skip(s,leftBraceToken);
    AspDictDisplay dd = new AspDictDisplay(s.curLineNum());

    if (s.curToken().kind == rightBraceToken) { //om det er en tom ordbok
      skip(s,rightBraceToken);
      leaveParser("dict display");
      return dd;
    }

    while (true) {
      dd.stringLits.add(AspStringLiteral.parse(s));
      skip(s,colonToken);
      dd.exprs.add(AspExpr.parse(s));
      if (s.curToken().kind != commaToken) { break; }
      skip(s,commaToken);
    }
    skip(s,rightBraceToken);

    leaveParser("dict display");
    return dd;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("{");
    for (int i = 0; i < stringLits.size(); i++) {
      if (i > 0) {
        prettyWrite(", ");
      }
      stringLits.get(i).prettyPrint();
      prettyWrite(": ");
      exprs.get(i).prettyPrint();
    }
    prettyWrite("}");

  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    if (exprs.isEmpty()) { //om det er en tom ordbok
      return new RuntimeDictValue();
    }
    RuntimeValue sv = stringLits.get(0).eval(curScope);
    RuntimeDictValue v = new RuntimeDictValue(sv, exprs.get(0).eval(curScope)); //oppretter ordbok med første nøkkel og verdi
    for (int i = 1; i < exprs.size(); i++) {
      sv = stringLits.get(i).eval(curScope);
      v = new RuntimeDictValue(v, sv, exprs.get(i).eval(curScope)); //lager nytt dictValue-objekt med den foreløpige lista og ny nøkkel og verdi
    }
    return v;
  }

}
