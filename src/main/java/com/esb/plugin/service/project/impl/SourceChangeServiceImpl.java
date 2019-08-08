package com.esb.plugin.service.project.impl;

import com.esb.plugin.service.project.SourceChangeService;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

public class SourceChangeServiceImpl implements SourceChangeService, BulkFileListener, ModuleListener, Disposable {

    private static final String JAVA_SOURCE_EXTENSION = "java";

    private static final Boolean CHANGED = true;
    private static final Boolean UNCHANGED = false;

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
        events.stream()
                .map(VFileEvent::getFile)
                .forEach(file -> {
                    if (isJavaSource(file)) {
                        isModuleSourceChange(file).ifPresent(this::setToChangedMatching);
                    }
                });
    }

    @Override
    public void moduleAdded(@NotNull Project project, @NotNull Module module) {
        @SystemIndependent String moduleFilePath = module.getModuleFilePath();
        File moduleFilePathFile = new File(moduleFilePath);
        moduleNameRootPathMap.putIfAbsent(module.getName(), moduleFilePathFile.getParent());
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
            @SystemIndependent String moduleFilePath = module.getModuleFilePath();
            File moduleFilePathFile = new File(moduleFilePath);
            moduleNameRootPathMap.putIfAbsent(module.getName(), moduleFilePathFile.getParent());

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

    private Optional<String> isModuleSourceChange(VirtualFile virtualFile) {
        for (Map.Entry<String, String> entry : moduleNameRootPathMap.entrySet()) {
            if (virtualFile.getPath().startsWith(entry.getValue())) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    private boolean isJavaSource(VirtualFile file) {
        return hasExtension(file, JAVA_SOURCE_EXTENSION);
    }

    private static boolean hasExtension(VirtualFile file, String extensionToTest) {
        return file != null &&
                file.getExtension() != null &&
                file.getExtension().equals(extensionToTest);
    }

    private void setToChangedMatching(String moduleName) {
        for (Map.Entry<BiKey, Boolean> entry : runtimeModuleNameChangedMap.entrySet()) {
            if (entry.getKey().key2.equals(moduleName)) {
                entry.setValue(CHANGED);
            }
        }
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
