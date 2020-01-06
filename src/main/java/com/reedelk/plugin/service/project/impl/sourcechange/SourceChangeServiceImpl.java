package com.reedelk.plugin.service.project.impl.sourcechange;

import com.intellij.ProjectTopics;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.Function;
import com.reedelk.plugin.commons.DefaultConstants;
import com.reedelk.plugin.service.project.SourceChangeService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;
import org.jetbrains.idea.maven.model.MavenConstants;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

public class SourceChangeServiceImpl implements SourceChangeService, BulkFileListener, ModuleListener, Disposable {

    // Everything changed under this folder is ignored,
    // since it does not belong to any project package.
    private final String ignoredFolder = ".idea";

    private static final boolean CHANGED = true;
    private static final boolean UNCHANGED = false;

    private Map<String, String> moduleNameRootPathMap = new HashMap<>();
    private Map<BiKey, Boolean> runtimeModuleNameChangedMap = new HashMap<>();

    public SourceChangeServiceImpl(Project project) {
        stream(ModuleManager.getInstance(project).getModules()).forEach(new RegisterModuleConsumer());
        project.getMessageBus().connect(this).subscribe(ProjectTopics.MODULES, this);
        project.getMessageBus().connect(this).subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    @Override
    public boolean isHotSwap(String runtimeConfigName, String moduleName) {
        return !isCompileRequired(runtimeConfigName, moduleName);
    }

    @Override
    public boolean isCompileRequired(String runtimeConfigName, String moduleName) {
        BiKey key = new BiKey(runtimeConfigName, moduleName);
        return runtimeModuleNameChangedMap.getOrDefault(key, CHANGED);
    }

    @Override
    public void unchanged(String runtimeConfigName, String moduleName) {
        BiKey key = new BiKey(runtimeConfigName, moduleName);
        runtimeModuleNameChangedMap.put(key, UNCHANGED);
    }

    @Override
    public void changed(String runtimeConfigName, String moduleName) {
        BiKey key = new BiKey(runtimeConfigName, moduleName);
        runtimeModuleNameChangedMap.put(key, CHANGED);
    }

    @Override
    public void reset(String runtimeConfigName) {
        runtimeModuleNameChangedMap.entrySet()
                .removeIf(biKeyBooleanEntry -> {
                    BiKey key = biKeyBooleanEntry.getKey();
                    return key.key1.equals(runtimeConfigName);
                });
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        events.stream().map(VFileEvent::getFile).filter(Objects::nonNull)
                .forEach(file -> {
                    if (file.getPath().contains(ignoredFolder)) {
                        // We ignore files changed within the .idea folder.
                        return;
                    }
                    if (!isHotSwappableSource(file)) {
                        isModuleSRCChange(file).ifPresent(this::setToChangedMatching);
                        isModulePOMChange(file).ifPresent(this::setToChangedMatching);
                    }
                });
    }

    @Override
    public void moduleAdded(@NotNull Project project, @NotNull Module module) {
        String moduleDirectory = getModuleDirectory(module);
        moduleNameRootPathMap.putIfAbsent(module.getName(), moduleDirectory);
    }

    @Override
    public void moduleRemoved(@NotNull Project project, @NotNull Module module) {
        moduleNameRootPathMap.remove(module.getName());
    }

    @Override
    public void modulesRenamed(@NotNull Project project, @NotNull List<Module> modules, @NotNull Function<Module, String> oldNameProvider) {
        modules.forEach(module -> {
            // Remove Old Name
            String oldModuleName = oldNameProvider.fun(module);
            moduleNameRootPathMap.remove(oldModuleName);

            // Add new name
            String moduleDirectory = getModuleDirectory(module);
            moduleNameRootPathMap.putIfAbsent(module.getName(), moduleDirectory);

            // Remove from module name
            Set<BiKey> biKeys = runtimeModuleNameChangedMap.keySet();
            biKeys.removeIf(key -> oldModuleName.equals(key.key2));
        });
    }

    @Override
    public void dispose() {
        runtimeModuleNameChangedMap.clear();
        moduleNameRootPathMap.clear();
    }

    /**
     * It checks whether the changed file belongs to the module_full_path/src folder or not.
     */
    private Optional<String> isModuleSRCChange(VirtualFile virtualFile) {
        return startsWith(virtualFile, DefaultConstants.PROJECT_SOURCES_FOLDER + File.separator);
    }


    /**
     * It checks whether the changed file is the module pom file.
     */
    private Optional<String> isModulePOMChange(VirtualFile virtualFile) {
        return startsWith(virtualFile, MavenConstants.POM_XML);
    }

    // Flows, Subflows, Flow config and directories inside the project's
    // resources folder are hot-swappable. Everything else is not hot-swappable.
    private boolean isHotSwappableSource(VirtualFile file) {
        return file != null &&
                // if it is inside resources folder, then it is hot-swappable, otherwise not.
                new File(file.getPath()).getPath().contains(DefaultConstants.PROJECT_RESOURCES_FOLDER);
    }

    private void setToChangedMatching(String moduleName) {
        for (Map.Entry<BiKey, Boolean> entry : runtimeModuleNameChangedMap.entrySet()) {
            if (entry.getKey().key2.equals(moduleName)) {
                entry.setValue(CHANGED);
            }
        }
    }

    private String getModuleDirectory(Module module) {
        @SystemIndependent String moduleFilePath = module.getModuleFilePath();
        File moduleFilePathFile = new File(moduleFilePath);
        String parent = moduleFilePathFile.getParent();
        // On windows it is inside .idea directory.
        if (parent.endsWith(".idea")) {
            return new File(parent).getParent();
        }
        return parent;
    }

    @NotNull
    private Optional<String> startsWith(VirtualFile virtualFile, String subDirectory) {
        for (Map.Entry<String, String> entry : moduleNameRootPathMap.entrySet()) {
            if (new File(virtualFile.getPath()).getPath().startsWith(entry.getValue() + File.separator + subDirectory)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    class RegisterModuleConsumer implements Consumer<Module> {
        @Override
        public void accept(Module module) {
            @SystemIndependent String moduleFilePath = module.getModuleFilePath();
            File moduleFilePathFile = new File(moduleFilePath);
            moduleNameRootPathMap.putIfAbsent(module.getName(), moduleFilePathFile.getParent());
        }
    }

    private static class BiKey {
        String key1;
        String key2;

        BiKey(String key1, String key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BiKey biKey = (BiKey) o;
            return key1.equals(biKey.key1) &&
                    key2.equals(biKey.key2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key1, key2);
        }

        @Override
        public String toString() {
            return "BiKey{" +
                    "key1='" + key1 + '\'' +
                    ", key2='" + key2 + '\'' +
                    '}';
        }
    }
}
