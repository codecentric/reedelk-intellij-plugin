package com.esb.plugin.maven;

import com.esb.plugin.commons.MavenUtils;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.model.MavenExplicitProfiles;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.tasks.TasksBundle;

import java.util.Collections;
import java.util.Optional;

public class MavenPackageGoal {

    private final Project project;
    private final String moduleName;
    private Callback callback;

    public MavenPackageGoal(@NotNull Project project, @NotNull String moduleName, @Nullable Callback callback) {
        this.project = project;
        this.moduleName = moduleName;
        this.callback = callback;
    }

    public interface Callback {
        void onComplete(boolean result);
    }

    public void execute() {
        Optional<MavenProject> mavenProjectOpt =
                MavenUtils.getMavenProject(project, moduleName);
        if (mavenProjectOpt.isPresent()) {
            MavenProject mavenProject = mavenProjectOpt.get();
            MavenPackageTask task = new MavenPackageTask(project, mavenProject);
            task.queue();
        } else {
            if (callback != null) {
                callback.onComplete(false);
            }
        }
    }

    class MavenPackageTask extends Task.Backgroundable {

        private final MavenProject mavenProject;

        MavenPackageTask(@Nullable Project project, @NotNull MavenProject mavenProject) {
            super(project, TasksBundle.message("maven.tasks.executing"));
            this.mavenProject = mavenProject;
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            boolean result = false;
            try {
                final MavenExplicitProfiles explicitProfiles =
                        MavenProjectsManager.getInstance(project).getExplicitProfiles();

                final MavenRunnerSettings mavenRunnerSettings =
                        new MavenRunnerSettings();

                final MavenGeneralSettings mavenGeneralSettings =
                        new MavenGeneralSettings();

                final MavenRunnerParameters params = new MavenRunnerParameters(
                        true,
                        mavenProject.getDirectory(),
                        mavenProject.getFile().getName(),
                        ParametersListUtil.parse("package -DskipTests=true"),
                        explicitProfiles.getEnabledProfiles(),
                        explicitProfiles.getDisabledProfiles());

                final MavenRunner mavenRunner = MavenRunner.getInstance(project);

                result = mavenRunner.runBatch(Collections.singletonList(params),
                        mavenGeneralSettings,
                        mavenRunnerSettings,
                        TasksBundle.message("maven.tasks.executing"),
                        indicator);

            } finally {
                if (callback != null) {
                    callback.onComplete(result);
                }
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
    }
}