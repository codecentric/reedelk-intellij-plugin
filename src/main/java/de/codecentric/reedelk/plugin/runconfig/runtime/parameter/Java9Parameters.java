package de.codecentric.reedelk.plugin.runconfig.runtime.parameter;

import de.codecentric.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;
import com.intellij.execution.configurations.ParametersList;

public class Java9Parameters extends Java8Parameters {

    @Override
    public void apply(ParametersList parameters, RuntimeRunConfiguration configuration) {
        super.apply(parameters, configuration);
        parameters.add("--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED");
        parameters.add("--add-opens", "java.base/java.net=ALL-UNNAMED");
    }
}
