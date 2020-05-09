package com.reedelk.plugin.service.module.impl.component.completion.commons;

import com.reedelk.runtime.api.commons.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PresentableType {

    private PresentableType() {
    }

    // Converts a fully qualified name type e.g. com.my.component.MyType to MyType.
    public static String from(String originalType) {
        if (originalType == null) return StringUtils.EMPTY;
        String[] splits = originalType.split(","); // might be multiple types
        List<String> tmp = new ArrayList<>();
        for (String split : splits) {
            String[] segments = split.split("\\.");
            tmp.add(segments[segments.length - 1]);
        }
        return String.join(",", tmp);
    }
}
