package com.reedelk.plugin.commons;

public class BuildVersion {

    private BuildVersion() {
    }

    public static String get() {
        return BuildVersion.class.getPackage().getImplementationVersion();
    }
}
