package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspAssignment extends AspSmallStmt {
  AspName name;
  ArrayList<AspSubscription> subscriptions = new ArrayList<>();
  AspExpr expr;

  AspAssignment(int n) {
    super(n);
  }

  static AspAssignment parse(Scanner s) {
    enterParser("assignment");
    AspAssignment aa = new AspAssignment(s.curLineNum());
    aa.name = AspName.parse(s);

    while (true) {
      if (s.curToken().kind != leftBracketToken) {
        break;
      }
      aa.subscriptions.add(AspSubscription.parse(s));
    }

    skip(s,equalToken);
    aa.expr = AspExpr.parse(s);

    leaveParser("assignment");
    return aa;
  }

  @Override
  public void prettyPrint() {
    name.prettyPrint();
    for (AspSubscription subscription: subscriptions) {
      subscription.prettyPrint();
    }
    prettyWrite(" = ");
    expr.prettyPrint();
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue e = expr.eval(curScope);
    RuntimeValue n = null;
    String subIndexStr = "";
    if (subscriptions.isEmpty()) {
      curScope.assign(name.t.name, e);
      n = name.eval(curScope);
    }

    else if (subscriptions.size() == 1) {
      n = name.eval(curScope);
      n.evalAssignElem(subscriptions.get(0).eval(curScope), e, this);
      subIndexStr += "[" + subscriptions.get(0).eval(curScope).showInfo() + "]";
    }

    else {
      n = name.eval(curScope);
      for (int i = 0; i < subscriptions.size(); i++) {
        if (i == (subscriptions.size() - 1)) { //på siste elemnt kalles evalAssignElem for å tilordne e til n
          subIndexStr += "[" + subscriptions.get(i).eval(curScope).showInfo() + "]";
          n.evalAssignElem(subscriptions.get(i).eval(curScope), e, this);
        }
        subIndexStr += "[" + subscriptions.get(i).eval(curScope).showInfo() + "]";
        n = n.evalSubscription(subscriptions.get(i).eval(curScope), this);
      }
    }

    trace("" + name.t.name + subIndexStr + " = " + e.showInfo());
    return null;
  }
}
