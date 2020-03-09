package com.reedelk.plugin.commons;

public class BuildVersion {

    public static String get() {
        return BuildVersion.class.getPackage().getImplementationVersion();
    }
}
