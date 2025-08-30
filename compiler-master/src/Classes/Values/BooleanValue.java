package Classes.Values;

import Classes.GenericStatements.GenericStatement;

public class BooleanValue extends ValueType {
    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
} 