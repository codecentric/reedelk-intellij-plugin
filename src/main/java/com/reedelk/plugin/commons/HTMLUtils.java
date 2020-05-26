package com.reedelk.plugin.commons;

import org.apache.commons.lang.StringEscapeUtils;

public class HTMLUtils {

    private HTMLUtils() {
    }

    // TODO: Can we remove this clazz?
    public static String escape(String html) {
        // Escapes HTML characters to be used in a string, e.g '<' to '&lt;' or '>' to '&gt;'.
        return StringEscapeUtils.escapeHtml(html);
    }
}
