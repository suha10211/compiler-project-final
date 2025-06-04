package Classes.GenericStatements.Variables;

import Classes.Values.ValueType;
import Classes.VariableNaming;

public class VariableDecl extends VariableAssignment{
    public VariableNaming variableNaming;
    public ValueType value;

    public VariableDecl(){}

    public VariableDecl(VariableNaming variableNaming, ValueType value){
        this.variableNaming = variableNaming;
        this.value = value;
    }

    @Override
    public String toString(){
        return "\n VariableDecl {" +
                "\n varName: " + (this.variableNaming != null ? this.variableNaming.toString() : "null") +
                "\n , varValue: " + (this.value != null ? this.value.toString() : "null") +
                "\n }";
    }
}
