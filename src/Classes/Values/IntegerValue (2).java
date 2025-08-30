package Classes.Values;

import Classes.GenericStatements.GenericStatement;

public class IntegerValue extends ValueType {
    private int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
} 