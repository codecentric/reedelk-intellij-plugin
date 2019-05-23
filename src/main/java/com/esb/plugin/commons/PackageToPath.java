package com.esb.plugin.commons;

import static com.google.common.base.Preconditions.checkArgument;

public class PackageToPath {

    public static String convert(String packageName) {
        checkArgument(packageName != null, "Package name");
        return "/" + packageName.replace('.', '/');
    }
}
