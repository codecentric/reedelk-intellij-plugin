package com.reedelk.plugin.template;

import java.util.Properties;

public class DockerfileProperties extends Properties {

    public DockerfileProperties(String reedelkRuntimeVersion) {
        setProperty("reedelkVersion", reedelkRuntimeVersion);
    }
}
