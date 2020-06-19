package com.reedelk.plugin.runconfig.module.beforetask;

import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.concurrency.Semaphore;
import com.intellij.util.messages.MessageBus;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.RuntimeComboManager;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.runconfig.module.ModuleRunConfiguration;
import com.reedelk.plugin.runconfig.module.runner.ModuleUnDeployExecutor;
import com.reedelk.plugin.service.module.impl.runtimeapi.ModulePackager;
import com.reedelk.plugin.service.project.SourceChangeService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.reedelk.plugin.commons.Topics.COMMIT_COMPONENT_PROPERTIES_EVENTS;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;

public class ModuleBuilderBeforeTaskProvider extends BeforeRunTaskProvider<ModuleBuilderBeforeTask> {

    private static final Key<ModuleBuilderBeforeTask> ID = Key.create("ReedelkModule.BeforeRunTask");

    @Override
    public Key<ModuleBuilderBeforeTask> getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Module Builder";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.Module;
    }

    @Override
    public String getDescription(ModuleBuilderBeforeTask beforeRunTask) {
        return "Build Module package";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Nullable
    @Override
    public ModuleBuilderBeforeTask createTask(@NotNull RunConfiguration runConfiguration) {
        final ModuleBuilderBeforeTask task = new ModuleBuilderBeforeTask(getId());
        task.setEnabled(runConfiguration instanceof ModuleRunConfiguration);
        return task;
    }

    @Override
    public boolean canExecuteTask(@NotNull RunConfiguration runConfiguration, @NotNull ModuleBuilderBeforeTask beforeRunTask) {
        return runConfiguration instanceof ModuleRunConfiguration;
    }

    @Override
    public boolean executeTask(@NotNull DataContext context, @NotNull RunConfiguration configuration, @NotNull ExecutionEnvironment env, @NotNull ModuleBuilderBeforeTask task) {

        if (!(configuration instanceof ModuleRunConfiguration)) return false;


        // We skip this step if the executor is Un-Deploy
        if (ModuleUnDeployExecutor.EXECUTOR_ID.equals(env.getExecutor().getId())) {
            return true;
        }

        ModuleRunConfiguration moduleRunConfiguration = (ModuleRunConfiguration) configuration;

        // Notify error if the module is not selected (or not valid) in the current Runtime Configuration.
        final String moduleName = moduleRunConfiguration.getModuleName();
        final String runtimeConfigName = moduleRunConfiguration.getRuntimeConfigName();

        if (isBlank(moduleName)) {
            String errorMessage = message("module.run.error.module.not.selected", moduleRunConfiguration.getName());
            Project project = env.getProject();
            ApplicationManager.getApplication().invokeLater(() ->
                    ApplicationManager.getApplication().invokeLater(() ->
                            ToolWindowUtils.notifyError(project, errorMessage, moduleRunConfiguration.getName())));
            return false;
        }

        if (RuntimeComboManager.NO_RUNTIME_CONFIG_AVAILABLE.equals(runtimeConfigName)) {
            String errorMessage = message("module.run.error.runtime.to.be.defined", moduleRunConfiguration.getName());
            Project project = env.getProject();
            ApplicationManager.getApplication().invokeLater(() ->
                    ToolWindowUtils.notifyError(project, errorMessage, moduleRunConfiguration.getName()));
            return false;
        }

        if (isBlank(runtimeConfigName)) {
            String errorMessage = message("module.run.error.runtime.not.selected", moduleRunConfiguration.getName());
            Project project = env.getProject();
            ApplicationManager.getApplication().invokeLater(() ->
                    ToolWindowUtils.notifyError(project, errorMessage, moduleRunConfiguration.getName()));
            return false;
        }

        Module moduleByName = ModuleManager.getInstance(env.getProject()).findModuleByName(moduleName);
        if (moduleByName == null) {
            // The Configuration has a module name which has been probably changed, hence it does not exists.
            String errorMessage = message("module.run.error.module.does.not.exists", moduleName, moduleRunConfiguration.getName());
            Project project = env.getProject();
            ApplicationManager.getApplication().invokeLater(() ->
                    ToolWindowUtils.notifyError(project, errorMessage, moduleRunConfiguration.getName()));
            return false;
        }

        final Semaphore targetDone = new Semaphore();
        final boolean[] result = new boolean[]{true};

        ApplicationManager.getApplication().invokeAndWait(() -> {
            // We must commit so that listeners e.g Tables can fire "stopCellEditing" to write
            // the values into the DataHolders so that they can be written into the document.
            MessageBus messageBus = env.getProject().getMessageBus();
            CommitPropertiesListener publisher = messageBus.syncPublisher(COMMIT_COMPONENT_PROPERTIES_EVENTS);
            publisher.onCommit();

            // By saving all documents we force the File listener
            // to commit all files. This way we know if we can hot swap or not.
            FileDocumentManager.getInstance().saveAllDocuments();

            // If the maven artifact does not exists -> then we need to build it.
            // If it is not hot swap -> then we need to re-build it (something has changed in the java source code).
            if (!MavenUtils.existsMavenArtifact(env.getProject(), moduleName) ||
                    !SourceChangeService.getInstance(env.getProject()).isHotSwap(runtimeConfigName, moduleName)) {
                targetDone.down();
                buildPackage(env.getProject(), moduleName, new ModulePackager.OnModulePackaged() {
                    @Override
                    public void onDone() {
                        result[0] = true;
                        targetDone.up();
                    }

                    @Override
                    public void onError(Exception exception) {
                        result[0] = false;
                        targetDone.up();
                    }
                });
            }
        }, ModalityState.NON_MODAL);

        targetDone.waitFor();
        return result[0];
    }

    private void buildPackage(Project project, String moduleName, ModulePackager.OnModulePackaged callback) {
        if (project == null || project.isDisposed()) {
            callback.onError(new PluginException("Project is disposed."));
        } else {
            ModulePackager packager = new ModulePackager(project, moduleName, callback);
            packager.doPackage();
        }
    }
}
