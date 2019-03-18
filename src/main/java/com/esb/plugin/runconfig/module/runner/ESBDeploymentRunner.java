package com.esb.plugin.runconfig.module.runner;

import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;
import org.jetbrains.annotations.NotNull;


public class ESBDeploymentRunner extends DefaultProgramRunner {

    @NotNull
    @Override
    public String getRunnerId() {
        return "ESBDeploymentRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return (ESBModuleDeployExecutor.EXECUTOR_ID.equals(executorId) ||
                ESBModuleUnDeployExecutor.EXECUTOR_ID.equals(executorId)) &&
                profile instanceof ESBModuleRunConfiguration;
    }

}
