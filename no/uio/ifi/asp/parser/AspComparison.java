package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspComparison extends AspSyntax {
  ArrayList<AspTerm> terms = new ArrayList<>();
  ArrayList<AspCompOpr> compOprs = new ArrayList<>();

  AspComparison(int n) {
    super(n);
  }

  static AspComparison parse(Scanner s) {
    enterParser("comparison");
    AspComparison ac = new AspComparison(s.curLineNum());

    while (true) {
      ac.terms.add(AspTerm.parse(s));

      if (s.isCompOpr()) {
        ac.compOprs.add(AspCompOpr.parse(s));
      }
      else { break; }
    }

    leaveParser("comparison");
    return ac;
  }

  @Override
  public void prettyPrint() {
    for (int i = 0; i < terms.size(); i++) {
      if (i+1 == terms.size()) { //for siste term vil det ikke være en compOpr
        terms.get(i).prettyPrint();
      }
      else {
        terms.get(i).prettyPrint();
        compOprs.get(i).prettyPrint();
      }
    }
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue v1 = terms.get(0).eval(curScope);
    RuntimeValue v2 = null;

    for (int i = 1; i < terms.size(); i++) {
      v2 = terms.get(i).eval(curScope);

      TokenKind k = compOprs.get(i-1).t.kind;
      switch (k) {
        case lessToken:
          v1 = v1.evalLess(v2, this); break;
        case lessEqualToken:
          v1 = v1.evalLessEqual(v2, this); break;
        case greaterToken:
          v1 = v1.evalGreater(v2, this); break;
        case greaterEqualToken:
          v1 = v1.evalGreaterEqual(v2, this); break;
        case doubleEqualToken:
          v1 = v1.evalEqual(v2, this); break;
        case notEqualToken:
          v1 = v1.evalNotEqual(v2, this); break;
        default:
        Main.panic("Illegal comparison operator: " + k + "!");
      }

      if (!v1.getBoolValue("comparison", this)) {
        return new RuntimeBoolValue(false);
      }

      v1 = v2;
    }

    if (terms.size() > 1) {
      return new RuntimeBoolValue(true);
    }

    return v1;
  }

}
