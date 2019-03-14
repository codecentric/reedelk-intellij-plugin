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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

public class ESBFileChangeServiceImpl implements ESBFileChangeService, BulkFileListener, ModuleListener, Disposable {

    private static final String JAVA_SOURCE_EXTENSION = "java";

    private static final Boolean CHANGED = true;
    private static final Boolean UNCHANGED = false;

    private Map<String,String> moduleNameRootPathMap = new HashMap<>();
    private Map<String, Boolean> moduleNameChangedMap = new HashMap<>();

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
    public boolean isCompileRequired(String moduleName) {
        return moduleNameChangedMap.getOrDefault(moduleName, CHANGED);
    }

    @Override
    public void unchanged(String moduleName) {
        moduleNameChangedMap.put(moduleName, UNCHANGED);
    }

    @Override
    public void changed(String moduleName) {
        moduleNameChangedMap.put(moduleName, CHANGED);
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        events.forEach(vFileEvent -> {
            if(isJavaSource(vFileEvent.getFile())) {
                isModuleSourceChange(vFileEvent.getFile())
                        .ifPresent(moduleName -> moduleNameChangedMap.put(moduleName, CHANGED));
            }
        });
    }

    @Override
    public void moduleAdded(@NotNull Project project, @NotNull Module module) {
        moduleNameChangedMap.putIfAbsent(module.getName(), CHANGED);
    }

    @Override
    public void moduleRemoved(@NotNull Project project, @NotNull Module module) {
        moduleNameChangedMap.remove(module.getName());
        moduleNameRootPathMap.remove(module.getName());
    }

    @Override
    public void modulesRenamed(@NotNull Project project, @NotNull List<Module> modules, @NotNull Function<Module, String> oldNameProvider) {
        modules.forEach(((Consumer<Module>) module -> {
            String oldModuleName = oldNameProvider.fun(module);
            moduleNameChangedMap.remove(oldModuleName);
            moduleNameRootPathMap.remove(oldModuleName);
        }).andThen(new RegisterModuleConsumer()));
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

    class RegisterModuleConsumer implements Consumer<Module> {

        @Override
        public void accept(Module module) {
            if (module.getModuleFile() != null) {
                moduleNameChangedMap.putIfAbsent(module.getName(), CHANGED);
                moduleNameRootPathMap.putIfAbsent(module.getName(), module.getModuleFile().getParent().getPath());
            }
        }
    }
}
