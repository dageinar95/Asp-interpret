package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeFloatValue extends RuntimeValue {
  double floatValue;

  public RuntimeFloatValue(double v) {
    floatValue = round(v,2); //runner av til to desimaler.
  }

  @Override
  public boolean getBoolValue(String what, AspSyntax where) {
    if (floatValue != 0.0) {
      return true;
    }
    return false;
  }

  @Override
  public long getIntValue(String what, AspSyntax where) {
    return (int) round(floatValue,0);
  }

  @Override
  public String getStringValue(String what, AspSyntax where) {
    return "" + floatValue;
  }

  @Override
  public double getFloatValue(String what, AspSyntax where) {
    return floatValue;
  }

  @Override
  public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      return new RuntimeFloatValue(floatValue + v.getFloatValue("+ operand", where));
    }
    runtimeError("Type error for +.", where);
    return null;
  }

  @Override
  public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      return new RuntimeFloatValue(floatValue / v.getFloatValue("/ operand", where));
    }
    runtimeError("Type error for /.", where);
    return null;
  }

  @Override
  public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      if (floatValue == v.getFloatValue("== operand", where)) {
        return new RuntimeBoolValue(true);
      }
      return new RuntimeBoolValue(false);
    }
    else if (v instanceof RuntimeNoneValue) {
      return new RuntimeBoolValue(false);
    }
    runtimeError("Type error for ==.", where);
    return null;
  }

  @Override
  public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      if (floatValue > v.getFloatValue("> operand", where)) {
        return new RuntimeBoolValue(true);
      }
      return new RuntimeBoolValue(false);
    }
    runtimeError("Type error for >.", where);
    return null;
  }

  @Override
  public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      if (floatValue >= v.getFloatValue(">= operand", where)) {
        return new RuntimeBoolValue(true);
      }
      return new RuntimeBoolValue(false);
    }
    runtimeError("Type error for >=.", where);
    return null;
  }

  @Override
  public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      return new RuntimeFloatValue(Math.floor(floatValue / v.getFloatValue("// operand", where)));
    }
    runtimeError("Type error for //.", where);
    return null;
  }

  @Override
  public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue ) {
      if (floatValue < v.getFloatValue("< operand", where)) {
        return new RuntimeBoolValue(true);
      }
      return new RuntimeBoolValue(false);
    }
    runtimeError("Type error for <.", where);
    return null;
  }

  @Override
  public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      if (floatValue <= v.getFloatValue("<= operand", where)) {
        return new RuntimeBoolValue(true);
      }
      return new RuntimeBoolValue(false);
    }
    runtimeError("Type error for <=.", where);
    return null;
  }

  @Override
  public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue) {
      return new RuntimeFloatValue(
      floatValue - v.getIntValue("% operand", where) *
      Math.floor(floatValue / v.getIntValue("% operand", where))
      );
    }
    else if (v instanceof RuntimeFloatValue) {
      return new RuntimeFloatValue(
      floatValue - v.getFloatValue("% operand", where) *
      Math.floor(floatValue / v.getFloatValue("% operand", where))
      );
    }
    runtimeError("Type error for %.", where);
    return null;
  }

  @Override
  public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      return new RuntimeFloatValue(floatValue * v.getFloatValue("* operand", where));
    }

    runtimeError("Type error for *.", where);
    return null;
  }

  @Override
  public RuntimeValue evalNegate(AspSyntax where) {
    return new RuntimeFloatValue(-floatValue);
  }

  @Override
  public RuntimeValue evalNot(AspSyntax where) {
    return new RuntimeBoolValue(!this.getBoolValue("not operand", where));
  }

  @Override
  public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      if (floatValue != v.getFloatValue("!= operand", where)) {
        return new RuntimeBoolValue(true);
      }
      return new RuntimeBoolValue(false);
    }
    else if (v instanceof RuntimeNoneValue) {
       return new RuntimeBoolValue(true);
    }
    runtimeError("Type error for !=.", where);
    return null;
  }

  @Override
  public RuntimeValue evalPositive(AspSyntax where) {
    return new RuntimeFloatValue(floatValue);
  }

  @Override
  public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
    if (v instanceof RuntimeIntValue || v instanceof RuntimeFloatValue) {
      return new RuntimeFloatValue(floatValue - v.getFloatValue("- operand", where));
    }
    runtimeError("Type error for -.", where);
    return null;
  }

  @Override
  protected String typeName() {
    return "float";
  }

  @Override
  protected String showInfo(ArrayList<RuntimeValue> inUse, boolean toPrint) {
    return "" + floatValue;
  }

  public static double round(double value, int places) { //hjelpemetode for avrunding
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

}
