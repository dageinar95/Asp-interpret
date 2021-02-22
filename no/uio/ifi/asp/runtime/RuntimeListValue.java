package no.uio.ifi.asp.runtime;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeListValue extends RuntimeValue {
  public ArrayList<RuntimeValue> list;

  public RuntimeListValue() { //tom liste
    list = new ArrayList<>();
  }

  public RuntimeListValue(RuntimeValue v) { //lager lista, med ett element
    list = new ArrayList<>();
    list.add(v);
  }

  public RuntimeListValue(RuntimeListValue lv, RuntimeValue v) { //om vi skal utvide en liste med et nytt expr/element
    list = lv.list;
    list.add(v);
  }

  public RuntimeListValue(ArrayList<RuntimeValue> lv) {
    list = lv;
  }


  @Override
  public boolean getBoolValue(String what, AspSyntax where) {
    if (!list.isEmpty()) {
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
  public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
    ArrayList<RuntimeValue> newList = new ArrayList<>();
    if (v instanceof RuntimeIntValue) {
      for (int i = 0; i < v.getIntValue("* operand", where); i++) {
        newList.addAll(list);
      }
      return new RuntimeListValue(newList);
    }

    runtimeError("Type error for multiply", where);
    return null;
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
    if (list.size() == (int)inx.getIntValue("assign element", where)) { //om det nye elementet er på "neste ledige" indeks i lista
      list.add(val);
    }
    else if (inx.getIntValue("assign element", where) < list.size()) { //om vi skal overskrive et eksisterende element
      list.set((int)inx.getIntValue("assign element", where), val);
    }
    else {
      runtimeError("List index: [" + val.getIntValue("subscription", where) + "] out of bounds.", where);
    }
  }

  @Override
  public RuntimeValue evalLen(AspSyntax where) {
    return new RuntimeIntValue(list.size());
  }

  @Override
  public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue) {
      if ((int) v.getIntValue("subscription", where) < list.size()) {
        return list.get((int)v.getIntValue("subscription", where));
      }
      runtimeError("List index: [" + v.getIntValue("subscription", where) + "] out of bounds.", where);
    }
    runtimeError("Type error for subscription [].", where);
    return null;
  }

  @Override
  protected String typeName() {
    return "list";
  }

  @Override
  protected String showInfo(ArrayList<RuntimeValue> inUse, boolean toPrint) {
    String line = "[";
    for (int i = 0; i < list.size(); i++) {
      line += list.get(i).showInfo();
      if (i < list.size() - 1) {
        line += ", ";        
      }
    }
    line += "]";
    return line;
  }

}
