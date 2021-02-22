package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspPassStmt extends AspSmallStmt {

  AspPassStmt(int n) {
    super(n);
  }

  static AspPassStmt parse(Scanner s) {
    enterParser("pass stmt");
    AspPassStmt ps = new AspPassStmt(s.curLineNum());
    skip(s,passToken);

    leaveParser("pass stmt");
    return ps;
  }

  @Override
  public void prettyPrint() {
    prettyWrite(" pass");
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
  	trace("pass");
    return null;
  }
}
