package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspForStmt extends AspCompoundStmt {
  AspName name;
  AspExpr expr;
  AspSuite suite;

  AspForStmt(int n) {
    super(n);
  }

  static AspForStmt parse(Scanner s) {
    enterParser("for stmt");
    AspForStmt fs = new AspForStmt(s.curLineNum());

    skip(s,forToken);
    fs.name = AspName.parse(s);
    skip(s,inToken);
    fs.expr = AspExpr.parse(s);
    skip(s,colonToken);
    fs.suite = AspSuite.parse(s);

    leaveParser("for stmt");
    return fs;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("for ");
    name.prettyPrint();
    prettyWrite(" in ");
    expr.prettyPrint();
    prettyWrite(": ");
    suite.prettyPrint();
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue e = expr.eval(curScope);
    if (!(e instanceof RuntimeListValue)) { Main.panic("Expression in for loop must be a list: for 'x' in 'list'"); }
    // trace("for " + name.t.name + " in " + e.showInfo() + ": ...");
    for (int i = 0; i < e.evalLen(this).getIntValue("for loop expr", this); i++) {
      RuntimeValue v = e.evalSubscription(new RuntimeIntValue(i), this);
      curScope.assign(name.t.name, v);
      trace("For #" + (i+1) + ": " + name.t.name + " = " + v.showInfo());
      suite.eval(curScope);
    }
  	return null;
  }
}
