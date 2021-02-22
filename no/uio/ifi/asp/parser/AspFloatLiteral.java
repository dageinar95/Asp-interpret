package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspFloatLiteral extends AspAtom {
  Token t;

  AspFloatLiteral(int n) {
    super(n);
  }

  static AspFloatLiteral parse(Scanner s) {
    enterParser("float literal");
    AspFloatLiteral fl = new AspFloatLiteral(s.curLineNum());
    fl.t = s.curToken();
    skip(s,floatToken);

    leaveParser("float literal");
    return fl;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("" + t.floatLit);
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    return new RuntimeFloatValue(t.floatLit);
  }

}
