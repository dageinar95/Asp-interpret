package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFactorOpr extends AspSyntax {
  Token t;

  AspFactorOpr(int n) {
    super(n);
  }

  public static AspFactorOpr parse(Scanner s) {
    enterParser("factor opr");
    AspFactorOpr fo = new AspFactorOpr(s.curLineNum());
    fo.t = s.curToken();

    if (s.curToken().kind == astToken) {
      skip(s,astToken);
    }
    else if (s.curToken().kind == slashToken) {
      skip(s,slashToken);
    }
    else if (s.curToken().kind == doubleSlashToken) {
      skip(s,doubleSlashToken);
    }
    else {
      skip(s,percentToken);
    }

    leaveParser("factor opr");
    return fo;
  }


  @Override
  public void prettyPrint() {
    if (t.kind == astToken) {
      prettyWrite(" * ");
    }
    else if (t.kind == slashToken) {
      prettyWrite(" / ");
    }
    else if (t.kind == doubleSlashToken) {
      prettyWrite(" // ");
    }
    else {
      prettyWrite(" % ");
    }
  }


  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
  	//operatorene sjekkes i factor
  	return null;
  }
}
