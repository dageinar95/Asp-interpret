package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFactorPrefix extends AspSyntax {
  Token t;

  AspFactorPrefix(int n) {
    super(n);
  }

  public static AspFactorPrefix parse(Scanner s) {
    enterParser("factor prefix");
    AspFactorPrefix fp = new AspFactorPrefix(s.curLineNum());
    fp.t = s.curToken();

    if (s.curToken().kind == plusToken) {
      skip(s,plusToken);
    }
    else {
      skip(s,minusToken);
    }

    leaveParser("factor prefix");
    return fp;
  }

  @Override
  public void prettyPrint() {
    if (t.kind == plusToken) {
      prettyWrite(" + ");
    }
    else {
      prettyWrite(" - ");
    }
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    //prefixes sjekkes i factor
  	return null;
  }
}
