package com.reedelk.plugin.runconfig.module.beforetask;

import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.concurrency.Semaphore;
import com.intellij.util.messages.MessageBus;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.NotificationUtils;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.maven.MavenPackageGoal;
import com.reedelk.plugin.runconfig.module.ModuleRunConfiguration;
import com.reedelk.plugin.runconfig.module.runner.ModuleUnDeployExecutor;
import com.reedelk.plugin.service.project.SourceChangeService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.utils.MavenLog;

import javax.swing.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.topic.ReedelkTopics.COMMIT_COMPONENT_PROPERTIES_EVENTS;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;

public class ModuleBuilderBeforeTaskProvider extends BeforeRunTaskProvider<ModuleBuilderBeforeTask> {

    private static final Key<ModuleBuilderBeforeTask> ID = Key.create("ESBModule.BeforeRunTask");

    @Override
    public Key<ModuleBuilderBeforeTask> getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "ESB Module Builder";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.Module;
    }

    @Override
    public String getDescription(ModuleBuilderBeforeTask beforeRunTask) {
        return "Build ESB Module package";
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
    public boolean executeTask(DataContext context, @NotNull RunConfiguration configuration, @NotNull ExecutionEnvironment env, @NotNull ModuleBuilderBeforeTask task) {

        if (!(configuration instanceof ModuleRunConfiguration)) return false;


        // We skip this step if the executor is Un-Deploy
        if (ModuleUnDeployExecutor.EXECUTOR_ID.equals(env.getExecutor().getId())) {
            return true;
        }

        ModuleRunConfiguration moduleRunConfiguration = (ModuleRunConfiguration) configuration;

        // Notify error if the module is not selected (or not valid) in the current Runtime Configuration.
        if (isBlank(moduleRunConfiguration.getModuleName())) {
            String errorMessage = message("module.run.error.module.not.selected", moduleRunConfiguration.getName());
            Project project = env.getProject();
            SwingUtilities.invokeLater(() -> NotificationUtils.notifyError(errorMessage, project));
            return false;
        }

        if (isBlank(moduleRunConfiguration.getRuntimeConfigName())) {
            String errorMessage = message("module.run.error.runtime.not.selected", moduleRunConfiguration.getName());
            Project project = env.getProject();
            SwingUtilities.invokeLater(() -> NotificationUtils.notifyError(errorMessage, project));
            return false;
        }

        final Semaphore targetDone = new Semaphore();
        final boolean[] result = new boolean[]{true};

        try {
            ApplicationManager.getApplication().invokeAndWait(() -> {

                // We must commit so that listeners e.g Tables can fire "stopCellEditing" to write
                // the values into the DataHolders so that they can be written into the document.
                MessageBus messageBus = env.getProject().getMessageBus();
                CommitPropertiesListener publisher = messageBus.syncPublisher(COMMIT_COMPONENT_PROPERTIES_EVENTS);
                publisher.onCommit();

                // By saving all documents we force the File listener
                // to commit all files. This way we know if we can hot swap or not.
                FileDocumentManager.getInstance().saveAllDocuments();

                String moduleName = moduleRunConfiguration.getModuleName();
                String runtimeConfigName = moduleRunConfiguration.getRuntimeConfigName();

                // No Need to re-compile and build the project.
                if (SourceChangeService.getInstance(env.getProject()).isHotSwap(runtimeConfigName, moduleName)) {
                    return;
                }

                final Project project = CommonDataKeys.PROJECT.getData(context);
                if (project == null || project.isDisposed()) return;

                targetDone.down();
                MavenPackageGoal packageGoal = new MavenPackageGoal(project, moduleName, goalResult -> {
                    result[0] = goalResult;
                    targetDone.up();
                });
                packageGoal.execute();

            }, ModalityState.NON_MODAL);

        } catch (Exception e) {
            MavenLog.LOG.error(e);
            return false;
        }

        targetDone.waitFor();
        return result[0];
    }
}
