package com.esb.plugin.commons;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.esb.internal.commons.ModuleProperties.Bundle;

public class ModuleInfo {

    public static boolean IsESBModule(String jarFilePath) {
        try {
            Attributes attributes = getManifestAttributesOf(jarFilePath);
            String isEsbModule = attributes.getValue(Bundle.MODULE_HEADER_NAME);
            return Boolean.parseBoolean(isEsbModule);
        } catch (Exception e) {
            return false;
        }
    }

    public static String GetESBModuleName(String jarFilePath) {
        try {
            Attributes attributes = getManifestAttributesOf(jarFilePath);
            return attributes.getValue("Bundle-SymbolicName");
        } catch (Exception e) {
            return "UnknownSymbolicName";
        }
    }

    private static Attributes getManifestAttributesOf(String jarFilePath) throws IOException {
        try (JarFile jarFile = new JarFile(new File(jarFilePath))) {
            Manifest manifest = jarFile.getManifest();
            return manifest.getMainAttributes();
        }
    }
}
