package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspNoneLiteral extends AspAtom {
  Token t;

  AspNoneLiteral(int n) {
    super(n);
  }

  static AspNoneLiteral parse(Scanner s) {
    enterParser("none literal");
    AspNoneLiteral nl = new AspNoneLiteral(s.curLineNum());
    nl.t = s.curToken();
    skip(s,noneToken);

    leaveParser("none literal");
    return nl;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("None");
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    return new RuntimeNoneValue();
  }

}
