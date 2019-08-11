package com.reedelk.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.commons.ModuleProperties.Config;

public class ModuleUtils {

    private static final String PROJECT_RESOURCES_FOLDER = "src/main/resources";

    private ModuleUtils() {
    }

    public static Optional<String> getConfigsFolder(Module module) {
        Optional<String> resourcesFolder = getResourcesFolder(module);
        return resourcesFolder.map(resources ->
                Paths.get(resources, Config.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getResourcesFolder(Module module) {
        List<String> pathsList = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutLibraries()
                .withoutDepModules()
                .sources()
                .getPathsList()
                .getPathList();
        return pathsList.stream()
                .filter(sourcesUrls -> sourcesUrls.endsWith(PROJECT_RESOURCES_FOLDER))
                .findFirst();
    }

    public static String getModuleRoot(Module module) {
        return ModuleRootManager.getInstance(module).getContentRoots()[0].getPresentableUrl();
    }
}
