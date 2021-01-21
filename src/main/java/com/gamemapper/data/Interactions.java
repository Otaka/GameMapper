package com.gamemapper.data;

import com.gamemapper.data.Interaction.InteractionOperation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * @author Dmitry
 */
public class Interactions {

    private final List<Interaction> interactions = new ArrayList<>();

    public List<Interaction> getInteractions() {
        return interactions;
    }

    public JsonArray serialize() {
        JsonArray result = new JsonArray(interactions.size());
        for (Interaction interaction : interactions) {
            JsonObject jsonInteraction = new JsonObject();
            jsonInteraction.add("var", new JsonPrimitive(interaction.getVariable().getName()));
            jsonInteraction.add("operation", new JsonPrimitive(interaction.getOperation().name()));
            jsonInteraction.add("value", new JsonPrimitive(interaction.getValue()));
            result.add(jsonInteraction);
        }
        return result;
    }

    public void deserialize(JsonArray serializedInteractions, int version) {
        interactions.clear();
        for (int i = 0; i < serializedInteractions.size(); i++) {
            JsonObject serializedInteraction = serializedInteractions.get(i).getAsJsonObject();
            String variableName = serializedInteraction.get("var").getAsString();
            String operationString = serializedInteraction.get("operation").getAsString();
            int value = serializedInteraction.get("value").getAsInt();
            Variable variable = VariablesStorage.get().getVariable(variableName);
            if (variable == null) {
                JOptionPane.showMessageDialog(null, "Variable [" + variableName + "] not found");
                variable = new Variable("BAD_VAR", 0);
            }
            Interaction interaction = new Interaction(variable, InteractionOperation.valueOf(operationString), value);
            interactions.add(interaction);
        }
    }
}
