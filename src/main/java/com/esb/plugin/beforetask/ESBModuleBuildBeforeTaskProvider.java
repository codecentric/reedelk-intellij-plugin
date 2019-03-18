package com.esb.plugin.beforetask;

import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.esb.plugin.runconfig.module.runner.ESBModuleUnDeployExecutor;
import com.esb.plugin.service.project.filechange.ESBFileChangeService;
import com.esb.plugin.utils.ESBIcons;
import com.esb.plugin.utils.ESBModuleUtils;
import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.concurrency.Semaphore;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.model.MavenExplicitProfiles;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.tasks.TasksBundle;
import org.jetbrains.idea.maven.utils.MavenLog;

import javax.swing.*;
import java.util.Collections;
import java.util.Optional;

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
        return ESBIcons.Module;
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

                // By saving all documents we trigger the File listener to commit files,
                // Hence we know if we can hotswap.
                FileDocumentManager.getInstance().saveAllDocuments();

                String moduleName = moduleRunConfiguration.getModuleName();
                String runtimeConfigName = moduleRunConfiguration.getRuntimeConfigName();

                // No Need to re-compile and build the project.
                if (ESBFileChangeService.getInstance(env.getProject()).isHotSwap(runtimeConfigName, moduleName)) {
                    return;
                }

                final Project project = CommonDataKeys.PROJECT.getData(context);
                if (project == null || project.isDisposed()) return;

                Optional<MavenProject> optionalMavenProject = ESBModuleUtils.getMavenProject(env.getProject(), moduleName);

                if (!optionalMavenProject.isPresent()) return;

                MavenProject mavenProject = optionalMavenProject.get();


                final MavenExplicitProfiles explicitProfiles = MavenProjectsManager.getInstance(project).getExplicitProfiles();
                final MavenRunner mavenRunner = MavenRunner.getInstance(project);

                targetDone.down();
                new Task.Backgroundable(project, TasksBundle.message("maven.tasks.executing"), true) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            MavenRunnerParameters params = new MavenRunnerParameters(
                                    true,
                                    mavenProject.getDirectory(),
                                    mavenProject.getFile().getName(),
                                    ParametersListUtil.parse("package -DskipTests=true"),
                                    explicitProfiles.getEnabledProfiles(),
                                    explicitProfiles.getDisabledProfiles());

                            result[0] = mavenRunner.runBatch(Collections.singletonList(params),
                                    null,
                                    null,
                                    TasksBundle.message("maven.tasks.executing"),
                                    indicator);
                        }
                        finally {
                            targetDone.up();
                        }
                    }

                    @Override
                    public boolean shouldStartInBackground() {
                        return MavenRunner.getInstance(project).getSettings().isRunMavenInBackground();
                    }

                    @Override
                    public void processSentToBackground() {
                        MavenRunner.getInstance(project).getSettings().setRunMavenInBackground(true);
                    }
                }.queue();
            }, ModalityState.NON_MODAL);
        }
        catch (Exception e) {
            MavenLog.LOG.error(e);
            return false;
        }
        targetDone.waitFor();
        return result[0];
    }


}
