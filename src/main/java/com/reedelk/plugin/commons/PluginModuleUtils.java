package com.reedelk.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Optional;

import static com.reedelk.plugin.commons.DefaultConstants.PROJECT_RESOURCES_FOLDER;
import static com.reedelk.runtime.commons.ModuleProperties.*;

public class PluginModuleUtils {

    private PluginModuleUtils() {
    }

    public static Optional<String> getFlowsFolder(Module module) {
        return getResourcesFolder(module).map(resources ->
                Paths.get(resources, Flow.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getConfigsFolder(Module module) {
        return getResourcesFolder(module).map(resources ->
                Paths.get(resources, Config.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getScriptsFolder(Module module) {
        return getResourcesFolder(module).map(resources ->
                Paths.get(resources, Script.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getResourcesFolder(@NotNull Module module) {
        return ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutLibraries()
                .withoutDepModules()
                .sources()
                .getPathsList()
                .getPathList()
                .stream()
                .filter(sourcesUrls -> sourcesUrls.endsWith(PROJECT_RESOURCES_FOLDER))
                .findFirst();
    }
}
