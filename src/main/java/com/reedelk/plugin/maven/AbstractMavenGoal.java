package com.reedelk.plugin.maven;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.model.MavenExplicitProfiles;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.tasks.TasksBundle;

import java.util.Optional;

public abstract class AbstractMavenGoal {

    private final String goalParams;
    private final Project project;
    private final String moduleName;
    private final MavenPackageGoal.Callback callback;

    protected AbstractMavenGoal(String goalParams, @NotNull Project project, @NotNull String moduleName, @NotNull MavenPackageGoal.Callback callback) {
        this.project = project;
        this.callback = callback;
        this.goalParams = goalParams;
        this.moduleName = moduleName;
    }

    public interface Callback {
        void onComplete(boolean result);
    }

    public void execute() {
        Optional<MavenProject> mavenProjectOpt = MavenUtils.getMavenProject(project, moduleName);
        if (mavenProjectOpt.isPresent()) {
            MavenProject mavenProject = mavenProjectOpt.get();
            MavenTask task = new MavenTask(project, mavenProject);
            task.queue();
        } else {
            callback.onComplete(false);
        }
    }

    class MavenTask extends Task.Backgroundable {

        private final MavenProject mavenProject;

        MavenTask(@Nullable Project project, @NotNull MavenProject mavenProject) {
            super(project, TasksBundle.message("maven.tasks.executing"));
            this.mavenProject = mavenProject;
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            MavenExplicitProfiles explicitProfiles = MavenProjectsManager.getInstance(project).getExplicitProfiles();
            MavenRunnerParameters params = new MavenRunnerParameters(
                    true,
                    mavenProject.getDirectory(),
                    mavenProject.getFile().getName(),
                    ParametersListUtil.parse(goalParams),
                    explicitProfiles.getEnabledProfiles(),
                    explicitProfiles.getDisabledProfiles());

            ProgramRunner.Callback myCallback = descriptor -> {
                ProcessHandler handler = descriptor.getProcessHandler();
                if (handler != null) {
                    handler.addProcessListener(new ProcessAdapter() {
                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            callback.onComplete(event.getExitCode() == 0);
                        }
                    });
                } else {
                    callback.onComplete(false);
                }
            };
            MavenRunConfigurationType.runConfiguration(myProject, params, myCallback);
        }

        @Override
        public boolean shouldStartInBackground() {
            return MavenRunner.getInstance(project).getSettings().isRunMavenInBackground();
        }

        @Override
        public void processSentToBackground() {
            MavenRunner.getInstance(project).getSettings().setRunMavenInBackground(true);
        }
    }
}
