package com.esb.plugin.action;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class DeployModuleAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        // Here enable/disable button
        RunnerAndConfigurationSettings selectedConfiguration = RunManager.getInstance(getEventProject(e)).getSelectedConfiguration();
        ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder
                .createOrNull(DefaultRunExecutor.getRunExecutorInstance(), selectedConfiguration);
        if (builder != null) {
            ExecutionManager.getInstance(getEventProject(e)).restartRunProfile(builder.build());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);


    }
}
