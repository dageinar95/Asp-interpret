package no.uio.ifi.asp.runtime;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.*;
import no.uio.ifi.asp.scanner.*;

public class RuntimeFunc extends RuntimeValue {
  AspFuncDef def;
  RuntimeScope defScope;
  String funcName;

  public RuntimeFunc(String fName) { //for biblioteksfunksjoner
    def = null; //trengs ikke fordi de er forhåndsdefinerte
    defScope = null; //bibliotek har ikke noe ytre skop
    funcName = fName;
  }

  public RuntimeFunc(String fName, AspFuncDef d, RuntimeScope s) { //fiks denne også om nødvendig. evt. legg til flere konstruktører
    def = d;
    defScope = s;
    funcName = fName;
  }

  public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
    if (actualParams.size() != def.names.size()) { runtimeError("Incorrect amount of arguments supplied.", where) ;} //om ikke antall akutelle parms == antall formelle params
    RuntimeScope newScope = new RuntimeScope(defScope); //defScope som ytre skop for newScope
    for (int i = 0; i < actualParams.size(); i++) { //initierer de formelle parametrene med verdiene fra actualParams
      newScope.assign(def.names.get(i).t.name, actualParams.get(i));
    }
    try {
      def.suite.eval(newScope); //her utføres funksjonen
    } catch (RuntimeReturnValue rv) {
      return rv.value;
    }
    return new RuntimeNoneValue();
  }

  public String showInfo(ArrayList<RuntimeValue> inUse, boolean toPrint) {
    return funcName;
  }

  public String typeName() {
    return "function";
  }

}
