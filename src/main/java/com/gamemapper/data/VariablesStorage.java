package com.gamemapper.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class VariablesStorage {

    private static final VariablesStorage instance = new VariablesStorage();
    private List<Variable> variables = new ArrayList<>();

    public static VariablesStorage get() {
        return instance;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public Variable getVariable(String name) {
        for (Variable var : variables) {
            if (var.getName().equals(name)) {
                return var;
            }
        }

        return null;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public String chooseNewVariableName() {
        for (int i = 0;; i++) {
            String variableName = "Var_" + i;
            if (!variableNameExists(variableName)) {
                return variableName;
            }
        }
    }

    private boolean variableNameExists(String name) {
        for (Variable var : variables) {
            if (var.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public JsonArray serialize(SerializationContext context) {
        JsonArray result = new JsonArray(variables.size());
        for (Variable variable : variables) {
            JsonObject jsonVariable = new JsonObject();
            jsonVariable.add("var", new JsonPrimitive(variable.getName()));
            jsonVariable.add("value", new JsonPrimitive(variable.getValue()));
            result.add(jsonVariable);
        }
        return result;
    }

    public void deserialize(JsonArray serializedData, int version) {
        variables.clear();
        for (JsonElement jsonVar : serializedData) {
            JsonObject jsonVarObject = jsonVar.getAsJsonObject();
            Variable var = new Variable(jsonVarObject.get("var").getAsString(), jsonVarObject.get("value").getAsInt());
            variables.add(var);
        }
    }

    public boolean checkConditions(Conditions conditions) {
        for (Condition condition : conditions.getConditions()) {
            switch (condition.getOperation()) {
                case EQUAL:
                    if (condition.getVariable().getValue() != condition.getValue()) {
                        return false;
                    }
                    break;
                case NOT_EQUAL:
                    if (condition.getVariable().getValue() == condition.getValue()) {
                        return false;
                    }
                    break;
                case MORE:
                    if (condition.getVariable().getValue() <= condition.getValue()) {
                        return false;
                    }
                    break;
                case MORE_EQUAL:
                    if (condition.getVariable().getValue() < condition.getValue()) {
                        return false;
                    }
                    break;
                case LESS:
                    if (condition.getVariable().getValue() >= condition.getValue()) {
                        return false;
                    }
                    break;
                case LESS_EQUAL:
                    if (condition.getVariable().getValue() > condition.getValue()) {
                        return false;
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown condition operation " + condition.getOperation());
            }
        }
        return true;
    }
}
