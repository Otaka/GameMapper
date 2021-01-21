package com.gamemapper.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * @author Dmitry
 */
public class Conditions {

    private final List<Condition> conditionList = new ArrayList<>();

    public List<Condition> getConditions() {
        return conditionList;
    }

    public JsonArray serialize() {
        JsonArray result = new JsonArray(conditionList.size());
        for (Condition condition : conditionList) {
            JsonObject jsonCondition = new JsonObject();
            jsonCondition.add("var", new JsonPrimitive(condition.getVariable().getName()));
            jsonCondition.add("operation", new JsonPrimitive(condition.getOperation().name()));
            jsonCondition.add("value", new JsonPrimitive(condition.getValue()));
            result.add(jsonCondition);
        }
        return result;
    }

    public void deserialize(JsonArray serializedConditions, int version) {
        conditionList.clear();
        for (int i = 0; i < serializedConditions.size(); i++) {
            JsonObject serializedCondition = serializedConditions.get(i).getAsJsonObject();
            String variableName = serializedCondition.get("var").getAsString();
            String operationString = serializedCondition.get("operation").getAsString();
            int value = serializedCondition.get("value").getAsInt();
            Variable variable = VariablesStorage.get().getVariable(variableName);
            if (variable == null) {
                JOptionPane.showMessageDialog(null, "Variable [" + variableName + "] not found");
                variable = new Variable("BAD_VAR", 0);
            }
            Condition condition = new Condition(variable, Condition.ConditionOperation.valueOf(operationString), value);
            conditionList.add(condition);
        }
    }
}
