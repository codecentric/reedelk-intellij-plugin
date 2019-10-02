package com.reedelk.plugin.commons;

import java.util.Map;

public class MapUtils {

    private MapUtils() {
    }

    public static <T> T getFirstKeyOrDefault(Map<T, ?> map, T defaultValue) {
        return map.isEmpty() ? defaultValue :
                map.entrySet().iterator().next().getKey();
    }
}
