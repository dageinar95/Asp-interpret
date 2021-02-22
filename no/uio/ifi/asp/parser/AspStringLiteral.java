package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspStringLiteral extends AspAtom {
  Token t;

  AspStringLiteral(int n) {
    super(n);
  }

  static AspStringLiteral parse(Scanner s) {
    enterParser("string literal");
    AspStringLiteral sl = new AspStringLiteral(s.curLineNum());
    sl.t = s.curToken();
    skip(s,stringToken);

    leaveParser("string literal");
    return sl;
  }

  @Override
  public void prettyPrint() {
    char c = '"';
    prettyWrite(c + t.stringLit + c);
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    return new RuntimeStringValue(t.stringLit);
  }

}
