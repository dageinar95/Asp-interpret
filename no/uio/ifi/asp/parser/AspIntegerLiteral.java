package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspIntegerLiteral extends AspAtom {
  Token t;

  AspIntegerLiteral(int n) {
    super(n);
  }

  static AspIntegerLiteral parse(Scanner s) {
    enterParser("integer literal");
    AspIntegerLiteral il = new AspIntegerLiteral(s.curLineNum());
    il.t = s.curToken();
    skip(s,integerToken);

    leaveParser("integer literal");
    return il;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("" + t.integerLit);
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    return new RuntimeIntValue(t.integerLit);
  }

}
