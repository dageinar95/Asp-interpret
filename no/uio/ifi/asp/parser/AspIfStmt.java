package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspIfStmt extends AspCompoundStmt {
  ArrayList<AspExpr> exprs = new ArrayList<>();
  ArrayList<AspSuite> suites = new ArrayList<>();
  AspSuite elseSuite = null;

  AspIfStmt(int n) {
    super(n);
  }

  static AspIfStmt parse(Scanner s) {
    enterParser("if stmt");
    AspIfStmt is = new AspIfStmt(s.curLineNum());

    skip(s,ifToken);

    while (true) {
      is.exprs.add(AspExpr.parse(s));
      skip(s,colonToken);
      is.suites.add(AspSuite.parse(s));
      if (s.curToken().kind != elifToken) { break; }
      skip(s,elifToken);
    }

    if (s.curToken().kind == elseToken) {
      skip(s,elseToken);
      skip(s,colonToken);
      is.elseSuite = AspSuite.parse(s);
    }

    leaveParser("if stmt");
    return is;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("if ");
    for (int i = 0; i < exprs.size(); i++) {
      if (i > 0 ) {
        prettyWrite("elif ");
      }
      exprs.get(i).prettyPrint();
      prettyWrite(": ");
      suites.get(i).prettyPrint();
    }
    if (elseSuite != null) {
      prettyWrite("else");
      prettyWrite(": ");
      elseSuite.prettyPrint();
    }
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    for (int i = 0; i < exprs.size(); i++) {
      RuntimeValue e = exprs.get(i).eval(curScope);
      if (e.getBoolValue("if stmt", this)) {
        trace("if True alt #" + (i+1) + ": ...");
        suites.get(i).eval(curScope);
        return null;
      }
    }

    if (elseSuite != null) {
      trace("else: ...");
      elseSuite.eval(curScope);
    }

    return null;
  }

}
