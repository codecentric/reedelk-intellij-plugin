package com.reedelk.plugin.runconfig.runtime.parameter;

import com.intellij.execution.configurations.ParametersList;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;

public interface ParameterStrategy {

    void apply(ParametersList parameters, RuntimeRunConfiguration configuration);

}
