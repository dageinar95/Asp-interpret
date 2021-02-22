package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspFactor extends AspSyntax {
  ArrayList<AspFactorPrefix> facPrfxs = new ArrayList<>();
  ArrayList<AspPrimary> prims= new ArrayList<>();
  ArrayList<AspFactorOpr> facOprs = new ArrayList<>();

  AspFactor(int n) {
    super(n);
  }

  static AspFactor parse(Scanner s) {
    enterParser("factor");
    AspFactor af = new AspFactor(s.curLineNum());

    while (true) {
      if (s.isFactorPrefix()) {
        af.facPrfxs.add(AspFactorPrefix.parse(s));
      }

      else {
        af.facPrfxs.add(null); //legger til null om påfølgende primary ikke hadde et factor prefix
        //dette er nyttig for prettyprint, for å vite om en gitt primary hadde prefix eller ikke
      }

      af.prims.add(AspPrimary.parse(s));

      if (s.isFactorOpr()) {
        af.facOprs.add(AspFactorOpr.parse(s));
      }
      else { break; }
    }

    leaveParser("factor");
    return af;
  }

  @Override
  public void prettyPrint() {
    for (int i = 0; i < prims.size(); i++) {
      if (i > 0 && (i-1 < prims.size())) {
        facOprs.get(i-1).prettyPrint();
      }
      if (facPrfxs.get(i) != null) {
        facPrfxs.get(i).prettyPrint();
        prims.get(i).prettyPrint();
      }
      else {
        prims.get(i).prettyPrint();
      }
    }
  }

  @Override
  public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
    RuntimeValue v = prims.get(0).eval(curScope);
    RuntimeValue v2 = null;

    if (facPrfxs.get(0) != null) { //om primary har et prefix
      TokenKind k = facPrfxs.get(0).t.kind;
      switch (k) {
        case minusToken:
          v = v.evalNegate(this); break;
        case plusToken:
          v = v.evalPositive(this); break;
        default:
          Main.panic("Illegal factor prefix: " + k + "!");
      }
    }

    for (int i = 1; i < prims.size(); i++) {
        v2 = prims.get(i).eval(curScope);

        if (facPrfxs.get(i) != null) {
          TokenKind k = facPrfxs.get(i).t.kind;
          switch (k) {
            case minusToken:
              v2 = v2.evalNegate(this); break;
            case plusToken:
              v2 = v2.evalPositive(this); break;
            default:
              Main.panic("Illegal factor prefix: " + k + "!");
          }
        }

        TokenKind k = facOprs.get(i-1).t.kind;
        switch (k) {
          case astToken:
            v = v.evalMultiply(v2, this); break;
          case slashToken:
            v = v.evalDivide(v2, this); break;
          case percentToken:
            v = v.evalModulo(v2, this); break;
          case doubleSlashToken:
            v = v.evalIntDivide(v2, this); break;
          default:
            Main.panic("Illegal factor operator: " + k + "!");
        }
      }
      return v;
    }

}
