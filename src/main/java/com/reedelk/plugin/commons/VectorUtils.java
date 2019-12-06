package com.reedelk.plugin.commons;

import com.reedelk.runtime.api.commons.StringUtils;

import java.util.Vector;

public class VectorUtils {

    private VectorUtils() {
    }

    public static String getOrEmptyIfNull(Vector<String> vector, int index) {
        String value = vector.get(index);
        return value == null ? StringUtils.EMPTY : value;
    }
}
