package de.codecentric.reedelk.plugin.runconfig.runtime.parameter;

import com.intellij.execution.configurations.ParametersList;
import de.codecentric.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;

public interface ParameterStrategy {

    void apply(ParametersList parameters, RuntimeRunConfiguration configuration);

}
