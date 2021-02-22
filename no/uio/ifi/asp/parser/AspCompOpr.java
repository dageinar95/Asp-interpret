package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspCompOpr extends AspSyntax {
  Token t;

  AspCompOpr(int n) {
    super(n);
  }

  public static AspCompOpr parse(Scanner s) {
    enterParser("comp opr");
    AspCompOpr co = new AspCompOpr(s.curLineNum());
    co.t = s.curToken();

    if (s.curToken().kind == lessToken) {
      skip(s,lessToken);
    }
    else if (s.curToken().kind == greaterToken) {
      skip(s,greaterToken);
    }
    else if (s.curToken().kind == doubleEqualToken) {
      skip(s,doubleEqualToken);
    }
    else if (s.curToken().kind == greaterEqualToken) {
      skip(s,greaterEqualToken);
    }
    else if (s.curToken().kind == lessEqualToken) {
      skip(s,lessEqualToken);
    }
    else {
      skip(s,notEqualToken);
    }

    leaveParser("comp opr");
    return co;
  }


  @Override
  public void prettyPrint() {
    if (t.kind == lessToken) {
      prettyWrite(" < ");
    }
    else if(t.kind == greaterToken) {
      prettyWrite(" > ");
    }
    else if(t.kind == doubleEqualToken) {
      prettyWrite(" == ");
    }
    else if(t.kind == greaterEqualToken) {
      prettyWrite(" >= ");
    }
    else if(t.kind == lessEqualToken) {
      prettyWrite(" <= ");
    }
    else {
      prettyWrite(" != ");
    }
  }


  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
  	// her trengs det ikke noe innhold. Operatorene sjekkes i comparison
  	return null;
  }
}
