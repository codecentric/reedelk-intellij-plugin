package com.esb.plugin.runconfig;

import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.esb.plugin.utils.ESBIcons;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.tasks.MavenBeforeRunTask;
import org.jetbrains.idea.maven.tasks.MavenBeforeRunTasksProvider;

import javax.swing.*;

public class ESBModuleBuildBeforeTaskProvider extends MavenBeforeRunTasksProvider {

    private static final Key<MavenBeforeRunTask> ID = Key.create("ESB.BeforeRunTask");

    public ESBModuleBuildBeforeTaskProvider(Project project) {
        super(project);
    }

    @Override
    public Key<MavenBeforeRunTask> getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Super";
    }

    @Override
    public String getDescription(MavenBeforeRunTask task) {
        return "ESB Build Module";
    }

    @Override
    public Icon getIcon() {
        return ESBIcons.Module;
    }

    @Nullable
    @Override
    public Icon getTaskIcon(MavenBeforeRunTask task) {
        return ESBIcons.Module;
    }


    @Override
    public boolean isConfigurable() {
        return super.isConfigurable();
    }

    @Nullable
    @Override
    public MavenBeforeRunTask createTask(@NotNull RunConfiguration runConfiguration) {
        MavenBeforeRunTask task = new MavenBeforeRunTask();
        task.setGoal("package -DskipTests=true");

        task.setProjectPath("/Users/lorenzo/IdeaProjects/sample-module/another-child-module/another-one-child");
        task.setEnabled(runConfiguration instanceof ESBModuleRunConfiguration);
        return task;
    }

}
