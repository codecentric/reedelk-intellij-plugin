package de.codecentric.reedelk.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Optional;

import static de.codecentric.reedelk.plugin.commons.DefaultConstants.PROJECT_RESOURCES_FOLDER;
import static de.codecentric.reedelk.runtime.commons.ModuleProperties.*;

public class PluginModuleUtils {

    private PluginModuleUtils() {
    }

    public static Optional<String> getAssetsDirectory(Module module) {
        return getResourcesDirectory(module).map(resources ->
                Paths.get(resources, Assets.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getFlowsDirectory(Module module) {
        return getResourcesDirectory(module).map(resources ->
                Paths.get(resources, Flow.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getConfigsDirectory(Module module) {
        return getResourcesDirectory(module).map(resources ->
                Paths.get(resources, Config.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getScriptsDirectory(Module module) {
        return getResourcesDirectory(module).map(resources ->
                Paths.get(resources, Script.RESOURCE_DIRECTORY).toString());
    }

    public static Optional<String> getResourcesDirectory(@NotNull Module module) {
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
