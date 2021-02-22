package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspTermOpr extends AspSyntax {
  Token t;

  AspTermOpr(int n) {
    super(n);
  }

  public static AspTermOpr parse(Scanner s) {
    enterParser("term opr");
    AspTermOpr fp = new AspTermOpr(s.curLineNum());
    fp.t = s.curToken();

    if (s.curToken().kind == plusToken) {
      skip(s,plusToken);
    }
    else {
      skip(s,minusToken);
    }

    leaveParser("term opr");
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
  	//Sjekkes i AspTerm
  	return null;
  }
}
