package com.reedelk.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.commons.ModuleProperties.*;

public class ModuleUtils {

    private static final String PROJECT_RESOURCES_FOLDER = Paths.get("src", "main", "resources").toString();

    private ModuleUtils() {
    }
    public static Optional<String> getConfigsFolder(Module module) {
        return getResourcesFolder(module).map(resources ->
                Paths.get(resources, Config.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getFlowsFolder(Module module) {
        return getResourcesFolder(module).map(resources ->
                Paths.get(resources, Flow.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getScriptsFolder(Module module) {
        return getResourcesFolder(module).map(resources ->
                Paths.get(resources, Script.RESOURCE_DIRECTORY).toString());
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
}
