package com.esb.plugin.commons;

import com.esb.internal.commons.ModuleProperties;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ESBModuleInfo {

    public static boolean fromJarFilePath(String jarFilePath) {
        try {
            JarFile jarFile = new JarFile(new File(jarFilePath));
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            String isEsbModule = mainAttributes.getValue(ModuleProperties.Bundle.MODULE_HEADER_NAME);
            return Boolean.parseBoolean(isEsbModule);
        } catch (Exception e) {
            return false;
        }
    }

}
