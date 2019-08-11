package com.reedelk.plugin.commons;

import static com.google.common.base.Preconditions.checkArgument;

public class PackageToPath {

    private PackageToPath() {
    }

    public static String convert(String packageName) {
        checkArgument(packageName != null, "Package name");
        return "/" + packageName.replace('.', '/');
    }
}
