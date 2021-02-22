package no.uio.ifi.asp.runtime;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeStringValue extends RuntimeValue {
  String strValue;

  public RuntimeStringValue(String v) {
    strValue = v;
  }

  @Override
  public boolean getBoolValue(String what, AspSyntax where) {
    if (strValue.equals("")) {
      return false;
    }
    return true;
  }

  @Override
  public long getIntValue(String what, AspSyntax where) {
    try {
      return Integer.valueOf(strValue);
    } catch (Exception e) {
      runtimeError("String could not be converted to integer!", where);
    }
    return 0;
  }

  @Override
  public double getFloatValue(String what, AspSyntax where) {
    try {
      return Float.valueOf(strValue);
    } catch (Exception e) {
      runtimeError("String could not be converted to float!", where);
    }
    return 0;
  }


  @Override
  public String getStringValue(String what, AspSyntax where) {
    return strValue;
  }


  @Override
  public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
    return new RuntimeStringValue(strValue + v.getStringValue("+ operand", where));
  }

  @Override
  public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
    if (strValue.equals(v.getStringValue("== operand", where))) {
      return new RuntimeBoolValue(true);
    }
    else if (v instanceof RuntimeNoneValue) {
      return new RuntimeBoolValue(false);
    }
    return new RuntimeBoolValue(false);
  }

  @Override
  public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
    if (strValue.compareTo(v.getStringValue("> operand", where)) > 0) {
      return new RuntimeBoolValue(true);
    }
    return new RuntimeBoolValue(false);
  }

  @Override
  public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
    if (strValue.compareTo(v.getStringValue(">= operand", where)) >= 0) {
      return new RuntimeBoolValue(true);
    }
    return new RuntimeBoolValue(false);
  }

  @Override
  public RuntimeValue evalLen(AspSyntax where) {
    return new RuntimeIntValue(strValue.length());
  }

  @Override
  public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
    if (strValue.compareTo(v.getStringValue("< operand", where)) < 0) {
      return new RuntimeBoolValue(true);
    }
    return new RuntimeBoolValue(false);
  }

  @Override
  public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
    if (strValue.compareTo(v.getStringValue("<= operand", where)) <= 0) {
      return new RuntimeBoolValue(true);
    }
    return new RuntimeBoolValue(false);
  }

  @Override
  public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
    String s = strValue;
    if (v instanceof RuntimeIntValue) {
      for (int i = 1; i < v.getIntValue("* operand", where); i++) {
        s += strValue;
      }
      return new RuntimeStringValue(s);
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
    if (!strValue.equals(v.getStringValue("!= operand", where))) {
      return new RuntimeBoolValue(true);
    }
    else if (v instanceof RuntimeNoneValue) {
      return new RuntimeBoolValue(true);
    }
    return new RuntimeBoolValue(false);
  }

  @Override
  public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue) {
      return new RuntimeStringValue("" + strValue.charAt((int)v.getIntValue("subscription operand", where)));
    }
    runtimeError("Type error for subscription [].", where);
    return null;
  }

  @Override
  protected String typeName() {
    return "string";
  }

  @Override
  protected String showInfo(ArrayList<RuntimeValue> inUse, boolean toPrint) {
    if (toPrint) {
      return strValue;
    }
    if (strValue.indexOf('\'') >= 0) {
      return '"' + strValue + '"';
    }
    else {
      return "'" + strValue + "'";
    }
  }

}
