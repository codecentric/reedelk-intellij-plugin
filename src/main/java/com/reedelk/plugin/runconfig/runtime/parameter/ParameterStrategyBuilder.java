package com.reedelk.plugin.runconfig.runtime.parameter;

import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;

import java.util.Map;

public class ParameterStrategyBuilder {

    private static final ParameterStrategy DEFAULT_PARAMETER_STRATEGY = new Java8Parameters();

    private static final Map<JavaSdkVersion, ParameterStrategy> VERSION_STRATEGY_MAP =
            ImmutableMap.<JavaSdkVersion, ParameterStrategy>builder()
                    .put(JavaSdkVersion.JDK_1_8, new Java8Parameters())
                    .put(JavaSdkVersion.JDK_1_9, new Java9Parameters())
                    .put(JavaSdkVersion.JDK_10, new Java10Parameters())
                    .put(JavaSdkVersion.JDK_11, new Java11Parameters())
                    .put(JavaSdkVersion.JDK_12, new Java12Parameters())
                    .build();

    private ParameterStrategyBuilder() {
    }

    public static ParameterStrategy from(Sdk sdk) {
        JavaSdkVersion sdkVersion = JavaSdk.getInstance().getVersion(sdk);
        return VERSION_STRATEGY_MAP.getOrDefault(sdkVersion, DEFAULT_PARAMETER_STRATEGY);
    }
}
