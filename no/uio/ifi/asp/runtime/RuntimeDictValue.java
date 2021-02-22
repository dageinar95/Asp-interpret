package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeDictValue extends RuntimeValue {
  HashMap<RuntimeValue, RuntimeValue> dict;

  public RuntimeDictValue() { //tom ordbok
    dict = new HashMap<>();
  }

  public RuntimeDictValue(RuntimeValue v1, RuntimeValue v2) { //om ordboken begynner med ett element
    dict = new HashMap<>();
    dict.put(v1, v2);
  }

  public RuntimeDictValue(RuntimeDictValue dv, RuntimeValue v1, RuntimeValue v2) { //om ordboken begynner med flere
    dict = dv.dict;
    dict.put(v1, v2);
  }

  @Override
  public boolean getBoolValue(String what, AspSyntax where) {
    if (!dict.isEmpty()) {
      return true;
    }
    return false;
  }

  @Override
  public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
     if (v instanceof RuntimeNoneValue) {
        return new RuntimeBoolValue(false);
     }
    runtimeError("Type error for ==.", where);
    return null;  // Required by the compiler
  }

  @Override
  public RuntimeValue evalNot(AspSyntax where) {
    return new RuntimeBoolValue(!this.getBoolValue("not operand", where));
  }

  @Override
  public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
     if (v instanceof RuntimeNoneValue) {
        return new RuntimeBoolValue(true);
     }
     runtimeError("Type error for !=.", where);
     return null;  // Required by the compiler
  }

  @Override
  public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
    dict.put(inx, val);
  }

  @Override
  public RuntimeValue evalLen(AspSyntax where) {
    return new RuntimeIntValue(dict.size());
  }

  @Override
  public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeStringValue) {
      // String key = "'";
      String key = v.getStringValue("subscription", where);
      // key +="'";
      for (Map.Entry e : dict.entrySet()) {
        if (e.getKey().toString().equals(key)) {
          return (RuntimeValue) e.getValue();
        }
      }
      runtimeError("Invalid dictionary key: " + v.getStringValue("subscription", where), where);
    }
    runtimeError("Type error for subscription [].", where);
    return null;
  }


  @Override
  protected String typeName() {
    return "dictionary";
  }

  @Override
  protected String showInfo(ArrayList<RuntimeValue> inUse, boolean toPrint) {
    String s = "{";
    int i = 0;
    for (RuntimeValue v : dict.keySet()) {
      // s += "'";
      s += v.showInfo();
      s += ": ";
      // s += "': ";
      s += dict.get(v);
      if (i < dict.size() - 1) {
        s += ", ";
      }
      i++;
    }
    s += "}";
    return s;
  }

}
