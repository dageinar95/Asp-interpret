package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspBooleanLiteral extends AspAtom {
  Token t;

  AspBooleanLiteral(int n) {
    super(n);
  }

  static AspBooleanLiteral parse(Scanner s) {
    enterParser("boolean literal");
    AspBooleanLiteral bl;
    bl = new AspBooleanLiteral(s.curLineNum());
    bl.t = s.curToken();

    if (s.curToken().kind == falseToken) {
      skip(s,falseToken);
    }

    else {
      skip(s,trueToken);
    }

    leaveParser("boolean literal");
    return bl;
  }

  @Override
  public void prettyPrint() {
    if (t.kind == falseToken) {
      prettyWrite("False");
    }
    else {
      prettyWrite("True");
    }
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    if (t.kind == falseToken) {
      return new RuntimeBoolValue(false);
    }
    return new RuntimeBoolValue(true);
  }

}
