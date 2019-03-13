package com.esb.plugin.runner;

import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.GenericProgramRunner;
import org.jetbrains.annotations.NotNull;


public class ESBDeploymentRunner extends GenericProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "ESBDeploymentRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(ESBExecutor.EXECUTOR_ID) &&
                profile instanceof ESBModuleRunConfiguration;
    }

}
