package com.esb.plugin;

import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.tasks.MavenBeforeRunTask;
import org.jetbrains.idea.maven.tasks.MavenBeforeRunTasksProvider;

public class ESBModuleBuildBeforeTaskProvider extends MavenBeforeRunTasksProvider {

    public ESBModuleBuildBeforeTaskProvider(Project project) {
        super(project);
    }

    @Override
    public MavenBeforeRunTask createTask(@NotNull RunConfiguration runConfiguration) {
        MavenBeforeRunTask task = super.createTask(runConfiguration);
        if (task != null && runConfiguration instanceof ESBModuleRunConfiguration) {
            task.setGoal("package -DskipTests=true");
            task.setProjectPath("/Users/lorenzo/IdeaProjects/sample-module/another-child-module/another-one-child");
            task.setEnabled(true);
        }
        return task;
    }
}
