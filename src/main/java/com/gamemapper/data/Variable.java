package com.gamemapper.data;

/**
 * @author Dmitry
 */
public class Variable {

    private String name;
    private int value;

    public Variable(String name, int count) {
        this.name = name;
        this.value = count;
    }

    public Variable() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + name;
    }
}
