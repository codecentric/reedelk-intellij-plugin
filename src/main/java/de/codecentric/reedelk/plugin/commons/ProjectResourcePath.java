package de.codecentric.reedelk.plugin.commons;

public class ProjectResourcePath {

    // Project files are referenced using '/' front slash because they belong to the .jar package and
    // they are NOT OS dependent. Project files are scripts or assets.
    public static String normalizeProjectFilePath(String value) {
        return value.replaceAll("\\\\", "\\/");
    }
}
