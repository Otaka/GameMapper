package com.gamemapper.utils;

import com.gamemapper.settings.SettingsManager;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Dmitry
 */
public class FileChooser extends JFileChooser {

    private final String folderSettingName;

    public FileChooser(String folderSettingName) {
        this.folderSettingName = folderSettingName;
        String savedFolder = SettingsManager.readGlobalPreference(folderSettingName, null);
        if (savedFolder != null && new File(savedFolder).exists()) {
            setCurrentDirectory(new File(savedFolder));
        }
    }

    public void rememberFolder() {
        SettingsManager.writeGlobalPreference(folderSettingName, getSelectedFile().getParentFile().getAbsolutePath());
    }
}
