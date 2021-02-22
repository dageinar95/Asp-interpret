package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFuncDef extends AspCompoundStmt {
  public AspName name;
  public ArrayList<AspName> names = new ArrayList<>(); //bruker separat liste for name-loopen i jernbanediagrammet. //dette er de formelle parameterne
  public AspSuite suite;

  AspFuncDef(int n) {
    super(n);
  }

  static AspFuncDef parse(Scanner s) {
    enterParser("func def");
    AspFuncDef fd = new AspFuncDef(s.curLineNum());

    skip(s,defToken);
    fd.name = AspName.parse(s);
    skip(s,leftParToken);

    while (true) {
      if (s.curToken().kind == rightParToken) { break; }
      fd.names.add(AspName.parse(s));
      if (s.curToken().kind != commaToken) { break; }
      skip(s,commaToken);
    }

    skip(s,rightParToken);
    skip(s,colonToken);
    fd.suite = AspSuite.parse(s);

    leaveParser("func def");
    return fd;
  }

  @Override
  public void prettyPrint() {
    prettyWrite("def ");
    name.prettyPrint();
    prettyWrite("(");
    int i = 0;
    for (AspName an: names) {
      if (i > 0) {
        prettyWrite(", ");
      }
      an.prettyPrint();
      i++;
    }
    prettyWrite(")");
    prettyWrite(": ");
    suite.prettyPrint();
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
  	String funcName = name.t.name;
    RuntimeFunc func = new RuntimeFunc(funcName, this, curScope); //trenger egentlig ikke ta med funcname som param
    curScope.assign(funcName, func); //legger funksjonsverdien inn i skopet, foreløpig uten initierte parametre.
    trace("def " + funcName);
    return null;
  }
}
