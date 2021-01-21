package com.gamemapper.data;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Dmitry
 */
public class SerializationContext {

    private File file;
    private File folder;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public String createRelativePath(String path) {
        try {
            Path savedFileFolderPath = Paths.get(folder.getAbsolutePath());
            Path currentPath = Paths.get(path);
            Path relative = savedFileFolderPath.relativize(currentPath);
            return relative.toString();
        } catch (IllegalArgumentException ex) {
            //if in different roots(d:/ c:/) then relativize will throw exception
            return path;
        }
    }

    public String resolveRelativePath(String path) {
        File _file = new File(folder, path).getAbsoluteFile();
        if(_file.exists()){
            return _file.getAbsolutePath();
        }
        return path;
    }
}
