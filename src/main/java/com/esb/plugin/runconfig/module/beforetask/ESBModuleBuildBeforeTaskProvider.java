package com.esb.plugin.runconfig.module.beforetask;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.maven.MavenPackageGoal;
import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.esb.plugin.runconfig.module.runner.ESBModuleUnDeployExecutor;
import com.esb.plugin.service.project.sourcechange.SourceChangeService;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.utils.MavenLog;

import javax.swing.*;

public class ESBModuleBuildBeforeTaskProvider extends BeforeRunTaskProvider<ESBModuleBuildBeforeTask> {

    public static final Key<ESBModuleBuildBeforeTask> ID = Key.create("ESBModule.BeforeRunTask");

    @Override
    public Key<ESBModuleBuildBeforeTask> getId() {
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
    public String getDescription(ESBModuleBuildBeforeTask beforeRunTask) {
        return "Build ESB Module package";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Nullable
    @Override
    public ESBModuleBuildBeforeTask createTask(@NotNull RunConfiguration runConfiguration) {
        final ESBModuleBuildBeforeTask task = new ESBModuleBuildBeforeTask(getId());
        task.setEnabled(runConfiguration instanceof ESBModuleRunConfiguration);
        return task;
    }

    @Override
    public boolean canExecuteTask(RunConfiguration runConfiguration, ESBModuleBuildBeforeTask beforeRunTask) {
        return runConfiguration instanceof ESBModuleRunConfiguration;
    }


    @Override
    public boolean executeTask(DataContext context, @NotNull RunConfiguration configuration, @NotNull ExecutionEnvironment env, @NotNull ESBModuleBuildBeforeTask task) {

        if (!(configuration instanceof ESBModuleRunConfiguration)) return false;


        // We should also skip this step if there are no files changed in the src directory

        // We skip this step if the executor is Un-Deploy
        if (ESBModuleUnDeployExecutor.EXECUTOR_ID.equals(env.getExecutor().getId())) {
            return true;
        }

        ESBModuleRunConfiguration moduleRunConfiguration = (ESBModuleRunConfiguration) configuration;


        final Semaphore targetDone = new Semaphore();
        final boolean[] result = new boolean[]{true};

        try {
            ApplicationManager.getApplication().invokeAndWait(() -> {

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
