package de.codecentric.reedelk.plugin.runconfig.runtime.parameter;

import com.intellij.execution.configurations.ParametersList;
import de.codecentric.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;

public class Java8Parameters extends BaseParameterStrategy {

    @Override
    public void apply(ParametersList parameters, RuntimeRunConfiguration configuration) {
        super.apply(parameters, configuration);
        parameters.add("-Dio.netty.allocator.type=unpooled");
    }
}
