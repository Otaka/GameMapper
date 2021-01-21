package com.gamemapper.settings;

import com.gamemapper.Main;
import com.gamemapper.data.SerializationContext;
import com.gamemapper.utils.Utils;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author Dmitry
 */
public class SettingsManager {

    public static Color notVisitedRoomsColor = new Color(127, 127, 0, 127);
    public static Color visitedRoomsColor = new Color(0, 127, 127, 127);
    public static Color arrowsColor = new Color(0, 255, 0, 255);
    private static final Preferences globalPreferences;
    private static boolean dirty = false;
    private static final Timer preferencesFlushTimer;

    static {
        globalPreferences = Preferences.userNodeForPackage(Main.class);
        preferencesFlushTimer = new Timer(true);
        preferencesFlushTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public synchronized void run() {
                flushPreferencesChangesIfNeeded();
            }
        }, 5 * 1000, 5 * 1000);
    }

    public static JsonObject serialize(SerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("notVisitedRoomsColor", Utils.serializeColor(notVisitedRoomsColor));
        result.add("visitedRoomsColor", Utils.serializeColor(visitedRoomsColor));
        result.add("arrowsColor", Utils.serializeColor(arrowsColor));
        return result;
    }

    public static void deserialize(JsonObject serializedSettings, int version) {
        notVisitedRoomsColor = Utils.deserializeColor(serializedSettings.getAsJsonArray("notVisitedRoomsColor"));
        visitedRoomsColor = Utils.deserializeColor(serializedSettings.getAsJsonArray("visitedRoomsColor"));
        arrowsColor = Utils.deserializeColor(serializedSettings.getAsJsonArray("arrowsColor"));
    }

    public synchronized static void writeGlobalPreference(String key, String value) {
        globalPreferences.put(key, value);
        dirty = true;
    }

    public synchronized static void writeGlobalPreference(String key, int value) {
        globalPreferences.putInt(key, value);
        dirty = true;
    }

    public synchronized static String readGlobalPreference(String key, String defaultValue) {
        return globalPreferences.get(key, defaultValue);
    }

    public synchronized static int readGlobalPreference(String key, int defaultValue) {
        return globalPreferences.getInt(key, defaultValue);
    }

    private static synchronized void flushPreferencesChangesIfNeeded() {
        if (dirty) {
            try {
                globalPreferences.flush();
            } catch (BackingStoreException ex) {
                ex.printStackTrace(System.err);
            }
            dirty = false;
        }
    }
}
