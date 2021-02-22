package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

abstract class AspPrimarySuffix extends AspSyntax {

  AspPrimarySuffix(int n) {
    super(n);
  }

  static AspPrimarySuffix parse(Scanner s) { //returnerer AspPrimarySuffix, siden dette vil dekke både arguments og subscription
    enterParser("primary suffix");
    AspPrimarySuffix ps;

    if (s.curToken().kind == leftParToken) { //skipper ikke '(' eller '[' her, dette skjer i parsingen av hhv. subscription og arguments
      ps = AspArguments.parse(s);
    }
    else {
      ps = AspSubscription.parse(s);
    }

    leaveParser("primary suffix");
    return ps;
  }

  abstract void prettyPrint();
  abstract RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue;

}
