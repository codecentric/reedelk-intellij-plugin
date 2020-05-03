package com.reedelk.plugin.commons;

import com.reedelk.runtime.api.commons.StringUtils;

public class ToPresentableType {

    private ToPresentableType() {
    }

    // Converts a fully qualified name type e.g. com.my.component.MyType to MyType.
    public static String from(String originalType) {
        if (originalType == null) return StringUtils.EMPTY;
        String[] segments = originalType.split("\\.");
        return segments[segments.length - 1];
    }
}
