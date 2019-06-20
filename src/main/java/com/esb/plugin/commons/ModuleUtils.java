package com.esb.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;

import java.nio.file.Paths;
import java.util.List;

import static com.esb.internal.commons.ModuleProperties.Config;

public class ModuleUtils {

    private static final String PROJECT_RESOURCES_FOLDER = "src/main/resources";

    public static String getConfigsFolder(Module module) {
        String resourcesFolder = getResourcesFolder(module);
        return Paths.get(resourcesFolder, Config.RESOURCE_DIRECTORY).toString();
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
                .filter(sourcesUrls -> sourcesUrls.endsWith(PROJECT_RESOURCES_FOLDER))
                .findFirst()
                .get();
    }
}
