package Classes.Values;

import Classes.GenericStatements.GenericStatement;

public class FloatValue extends ValueType {
    private float value;

    public FloatValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
} 