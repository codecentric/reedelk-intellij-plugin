package de.codecentric.reedelk.plugin.runconfig.runtime.parameter;

import com.intellij.execution.configurations.ParametersList;
import de.codecentric.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import static de.codecentric.reedelk.plugin.commons.DefaultConstants.NameConvention;

abstract class BaseParameterStrategy implements ParameterStrategy {

    @Override
    public void apply(ParametersList parameters, RuntimeRunConfiguration configuration) {
        if (StringUtils.isNotBlank(configuration.getRuntimePort())) {
            parameters.add("-D" + NameConvention.DEFAULT_ADMIN_PORT_PARAM_NAME + "=" + configuration.getRuntimePort());
        }
        if (StringUtils.isNotBlank(configuration.getRuntimeBindAddress())) {
            parameters.add("-D" + NameConvention.DEFAULT_ADMIN_HOST_PARAM_NAME + "=" + configuration.getRuntimeBindAddress());
        }
        if (StringUtils.isNotBlank(configuration.getVmOptions())) {
            parameters.add(configuration.getVmOptions());
        }
    }
}
