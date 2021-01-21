package com.gamemapper.data;

/**
 * @author Dmitry
 */
public class Condition {

    private Variable variable;
    private ConditionOperation operation;
    private int value;

    public Condition() {
    }

    public Condition(Variable variable, ConditionOperation operation, int value) {
        this.variable = variable;
        this.operation = operation;
        this.value = value;
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public ConditionOperation getOperation() {
        return operation;
    }

    public void setOperation(ConditionOperation operation) {
        this.operation = operation;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public enum ConditionOperation {
        EQUAL, NOT_EQUAL, MORE, MORE_EQUAL, LESS, LESS_EQUAL
    }
}
