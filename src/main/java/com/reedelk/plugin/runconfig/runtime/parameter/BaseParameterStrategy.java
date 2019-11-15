package com.reedelk.plugin.runconfig.runtime.parameter;

import com.intellij.execution.configurations.ParametersList;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;

abstract class BaseParameterStrategy implements ParameterStrategy {

    @Override
    public void apply(ParametersList parameters, RuntimeRunConfiguration configuration) {
        parameters.add("-Dadmin.console.bind.port=" + configuration.getRuntimePort());
    }
}
