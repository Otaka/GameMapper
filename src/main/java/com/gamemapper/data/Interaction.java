package com.gamemapper.data;

/**
 *
 * @author Dmitry
 */
public class Interaction {

    private Variable variable;
    private InteractionOperation operation;
    private int value;

    public Interaction() {
    }

    public Interaction(Variable variable, InteractionOperation operation, int value) {
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

    public InteractionOperation getOperation() {
        return operation;
    }

    public void setOperation(InteractionOperation operation) {
        this.operation = operation;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static enum InteractionOperation {
        ADD
    }
}
