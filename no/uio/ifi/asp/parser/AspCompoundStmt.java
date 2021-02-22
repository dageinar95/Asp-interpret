package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public abstract class AspCompoundStmt extends AspStmt {

  AspCompoundStmt(int n) {
    super(n);
  }

  static AspCompoundStmt parse(Scanner s) {
    enterParser("compound stmt");
    AspCompoundStmt cs;

    if (s.curToken().kind == forToken) {
      cs = AspForStmt.parse(s);
    }
    else if (s.curToken().kind == ifToken) {
      cs = AspIfStmt.parse(s);
    }
    else if (s.curToken().kind == whileToken) {
      cs = AspWhileStmt.parse(s);
    }
    else {
      cs = AspFuncDef.parse(s);
    }

    leaveParser("compound stmt");
    return cs;
  }

  abstract void prettyPrint();
  abstract RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue;
}
