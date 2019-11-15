package com.reedelk.plugin.runconfig.runtime.parameter;

import com.intellij.execution.configurations.ParametersList;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;

public class Java11Parameters extends Java9Parameters {

    @Override
    public void apply(ParametersList parameters, RuntimeRunConfiguration configuration) {
        super.apply(parameters, configuration);
        parameters.add("--add-opens", "java.base/java.net=ALL-UNNAMED");
        parameters.addParametersString("-Dnashorn.args=\"--no-deprecation-warning\"");
    }
}
