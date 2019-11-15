package com.reedelk.plugin.runconfig.runtime.parameter;

import com.intellij.execution.configurations.ParametersList;
import com.reedelk.plugin.commons.Defaults;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;
import com.reedelk.runtime.api.commons.StringUtils;

abstract class BaseParameterStrategy implements ParameterStrategy {

    @Override
    public void apply(ParametersList parameters, RuntimeRunConfiguration configuration) {
        if (StringUtils.isNotBlank(configuration.getRuntimePort())) {
            parameters.add("-D" + Defaults.DEFAULT_ADMIN_PORT_PARAM_NAME + "=" + configuration.getRuntimePort());
        }
        if (StringUtils.isNotBlank(configuration.getRuntimeBindAddress())) {
            parameters.add("-D" + Defaults.DEFAULT_ADMIN_HOST_PARAM_NAME + "=" + configuration.getRuntimeBindAddress());
        }
    }
}
