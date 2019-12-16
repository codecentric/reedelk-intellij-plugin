package com.reedelk.plugin.commons;

import com.intellij.openapi.util.Version;
import org.jetbrains.annotations.Nullable;

public class Versions {

    private Versions() {
    }

    public static boolean compare(@Nullable Version moduleVersion, @Nullable Version artifactVersion) {
        if (moduleVersion == null) {
            return artifactVersion == null;
        } else if (artifactVersion == null) {
            // Module version would be != null.
            return false;
        } else {
            return moduleVersion.compareTo(artifactVersion) == 0;
        }
    }
}
