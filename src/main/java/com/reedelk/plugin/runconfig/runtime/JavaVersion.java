package com.reedelk.plugin.runconfig.runtime;

import com.intellij.execution.configurations.ParametersList;
import com.intellij.openapi.projectRoots.Sdk;

import static java.util.Arrays.stream;

public enum JavaVersion {

    JAVA_11("11") {
        @Override
        public void apply(ParametersList parameters) {
            parameters.add("--add-opens", "java.base/java.net=ALL-UNNAMED");
            parameters.add("-Dnashorn.args=--no-deprecation-warning");
        }
    },

    JAVA_8("java version \"1.8"),

    UNKNOWN("unknown");

    private final String version;

    JavaVersion(String version) {
        this.version = version;
    }

    public static JavaVersion from(Sdk sdk) {
        return stream(JavaVersion.values())
                .filter(javaVersion ->
                        sdk.getVersionString() != null &&
                                sdk.getVersionString().startsWith(javaVersion.version))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public void apply(ParametersList parameters) {
        // by default no additional parameters to be added.
    }

}
