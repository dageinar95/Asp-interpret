package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspPrimary extends AspSyntax {
  ArrayList<AspPrimarySuffix> primSfxs = new ArrayList<>();
  AspAtom atom;

  AspPrimary(int n) {
    super(n);
  }

  static AspPrimary parse(Scanner s) {
    enterParser("primary");
    AspPrimary ap = new AspPrimary(s.curLineNum());
    ap.atom = AspAtom.parse(s);

    if (s.curToken().kind != leftBracketToken && s.curToken().kind != leftParToken) { //om primary ikke inneholder primary suffix
      leaveParser("primary");
      return ap;
    }

    while (true) {
      ap.primSfxs.add(AspPrimarySuffix.parse(s));
      if (s.curToken().kind != leftBracketToken && s.curToken().kind != leftParToken) { break; }
      //hvis neste token ikke er bracket eller parentes, er det ikke flere primarysuffixes. skipper ikke her
    }

    leaveParser("primary");
    return ap;
  }

  @Override
  public void prettyPrint() {
    atom.prettyPrint();
    for (AspPrimarySuffix primarySuffix: primSfxs) {
      primarySuffix.prettyPrint();
    }
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue v = atom.eval(curScope);
    int funcCount = 0;

    if (primSfxs.isEmpty()) {
      return v;
    }

    for (AspPrimarySuffix ap : primSfxs) {
      if (ap instanceof AspSubscription) {
        v = v.evalSubscription(ap.eval(curScope), this);
      }
      else if (ap instanceof AspArguments) { //om vi har en funksjon. atom må da være et name
        if (funcCount > 0) { //om vi har flere arguments på rad. F.eks: f(a,b,c)(x,y,z). Da returnerer det første kallet en funksjon, som så kalles med x,y,z som argumenter.
          if (!(v instanceof RuntimeFunc)) { Main.panic("For function calls with multiple sets of arguments, each previous call must return a function."); } //i så fall må hvert kall, utenom det siste, returnere en funksjon
          AspName nameAtom = (AspName) atom;
          RuntimeListValue args = (RuntimeListValue) ap.eval(curScope);
          trace("Call function " + nameAtom.t.name + " with params " + args.showInfo());
          v = v.evalFuncCall(args.list, this);
          funcCount++;
        }
        else {
          AspName nameAtom = (AspName) atom; //caster Atom til AspName for å få tilgang til token.name.
          RuntimeListValue args = (RuntimeListValue) ap.eval(curScope); //henter aktuelle params
          RuntimeFunc func = (RuntimeFunc) curScope.find(nameAtom.t.name, this); //finner funksjonen som ble deklarert/assigned i AspFuncDef
          trace("Call function " + nameAtom.t.name + " with params " + args.showInfo());
          v = func.evalFuncCall(args.list, this);
          funcCount++;
        }
      }
    }
    return v;
  }

}
