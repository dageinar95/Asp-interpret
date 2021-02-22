package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspNotTest extends AspSyntax {
  AspComparison comparison;
  boolean notTok = false;

  AspNotTest(int n) {
    super(n);
  }

  public static AspNotTest parse(Scanner s) {
    enterParser("not test");
    AspNotTest nt = new AspNotTest(s.curLineNum());

    if (s.curToken().kind == notToken) {
      nt.notTok = true;
      skip(s,notToken);
    }

    nt.comparison = AspComparison.parse(s);

    leaveParser("not test");
    return nt;
  }


  @Override
  public void prettyPrint() {
    if (notTok) {
      prettyWrite(" not ");
    }
    comparison.prettyPrint();
  }


  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue v = comparison.eval(curScope);
    if (notTok) {
      v = v.evalNot(this);
    }
  	return v;
  }
}
