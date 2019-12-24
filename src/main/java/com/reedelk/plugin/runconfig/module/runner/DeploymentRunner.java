package com.reedelk.plugin.runconfig.module.runner;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.reedelk.plugin.runconfig.module.ModuleRunConfiguration;
import org.jetbrains.annotations.NotNull;


public class DeploymentRunner extends DefaultProgramRunner {

    @NotNull
    @Override
    public String getRunnerId() {
        return "DeploymentRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return (ModuleDeployExecutor.EXECUTOR_ID.equals(executorId) ||
                ModuleUnDeployExecutor.EXECUTOR_ID.equals(executorId)) &&
                profile instanceof ModuleRunConfiguration;
    }
}
