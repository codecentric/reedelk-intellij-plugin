package com.reedelk.plugin.runconfig.runtime.parameter;

import com.intellij.execution.configurations.ParametersList;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;

public class Java9Parameters extends Java8Parameters {

    @Override
    public void apply(ParametersList parameters, RuntimeRunConfiguration configuration) {
        super.apply(parameters, configuration);
        parameters.add("--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED");
        parameters.add("--add-opens", "java.base/java.net=ALL-UNNAMED");
    }
}
