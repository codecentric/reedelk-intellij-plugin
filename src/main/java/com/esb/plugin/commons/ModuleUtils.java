package com.esb.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;

import java.nio.file.Paths;
import java.util.List;

public class ModuleUtils {

    public static String getConfigsFolder(Module module) {
        String resourcesFolder = getResourcesFolder(module);
        return Paths.get(resourcesFolder, "configs").toString();
    }

    public static String getResourcesFolder(Module module) {
        List<String> pathsList = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutLibraries()
                .withoutDepModules()
                .sources()
                .getPathsList().getPathList();
        return pathsList.stream()
                .filter(sourcesUrls -> sourcesUrls.endsWith("src/main/resources"))
                .findFirst()
                .get();
    }
}
