package com.esb.plugin.service.project.filechange.impl;

import com.esb.plugin.service.project.filechange.ESBFileChangeService;
import com.intellij.ProjectTopics;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Arrays.stream;

public class ESBFileChangeServiceImpl implements ESBFileChangeService, BulkFileListener, ModuleListener, Disposable {

    private static final String JAVA_SOURCE_EXTENSION = "java";

    private static final Boolean CHANGED = true;
    private static final Boolean UNCHANGED = false;

    private Map<String,String> moduleNameRootPathMap = new HashMap<>();
    private Map<BiKey, Boolean> moduleNameChangedMap = new HashMap<>();

    public ESBFileChangeServiceImpl(Project project) {
        project.getMessageBus()
                .connect(this)
                .subscribe(ProjectTopics.MODULES, this);

        stream(ModuleManager
                .getInstance(project)
                .getModules())
                .forEach(new RegisterModuleConsumer());

        ApplicationManager.getApplication()
                .getMessageBus()
                .connect(this)
                .subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    @Override
    public boolean isHotSwap(String runtimeConfigName, String moduleName) {
        return !isCompileRequired(runtimeConfigName, moduleName);
    }

    @Override
    public boolean isCompileRequired(String runtimeConfigName, String moduleName) {
        BiKey key = new BiKey(runtimeConfigName, moduleName);
        return moduleNameChangedMap.getOrDefault(key, CHANGED);
    }

    @Override
    public void unchanged(String runtimeConfigName, String moduleName) {
        BiKey key = new BiKey(runtimeConfigName, moduleName);
        moduleNameChangedMap.put(key, UNCHANGED);
    }

    @Override
    public void changed(String runtimeConfigName, String moduleName) {
        BiKey key = new BiKey(runtimeConfigName, moduleName);
        moduleNameChangedMap.put(key, CHANGED);
    }

    @Override
    public void reset(String runtimeConfigName) {
        moduleNameChangedMap.entrySet()
                .removeIf(biKeyBooleanEntry -> {
                    BiKey key = biKeyBooleanEntry.getKey();
                    return key.key1.equals(runtimeConfigName);
                });
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        events.forEach(vFileEvent -> {
            if(isJavaSource(vFileEvent.getFile())) {
                isModuleSourceChange(vFileEvent.getFile())
                        .ifPresent(moduleName -> setToChangedMatching(moduleName));
            }
        });
    }

    @Override
    public void moduleAdded(@NotNull Project project, @NotNull Module module) {
        // nothing to do
    }

    @Override
    public void moduleRemoved(@NotNull Project project, @NotNull Module module) {
        // nothing to do
    }

    @Override
    public void modulesRenamed(@NotNull Project project, @NotNull List<Module> modules, @NotNull Function<Module, String> oldNameProvider) {
        // nothing to do
    }

    @Override
    public void dispose() {
        moduleNameChangedMap.clear();
    }

    private Optional<String> isModuleSourceChange(VirtualFile virtualFile) {
        for (Map.Entry<String,String> entry : moduleNameRootPathMap.entrySet()) {
            if (virtualFile.getPath().startsWith(entry.getValue())) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    private boolean isJavaSource(VirtualFile virtualFile) {
        if (virtualFile != null) {
            String extension = virtualFile.getExtension();
            if (extension != null) {
                return extension.equals(JAVA_SOURCE_EXTENSION);
            }
        }
        return false;
    }

    private void setToChangedMatching(String moduleName) {
        for (Map.Entry<BiKey, Boolean> entry : moduleNameChangedMap.entrySet()) {
            if (entry.getKey().key2.equals(moduleName)) {
                entry.setValue(CHANGED);
            }
        }
    }

    class RegisterModuleConsumer implements Consumer<Module> {

        @Override
        public void accept(Module module) {
            if (module.getModuleFile() != null) {
                moduleNameRootPathMap.putIfAbsent(module.getName(), module.getModuleFile().getParent().getPath());
            }
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
    }
}
