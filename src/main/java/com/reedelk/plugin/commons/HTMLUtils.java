package com.reedelk.plugin.commons;

import org.apache.commons.lang.StringEscapeUtils;

public class HTMLUtils {

    // Escapes HTML characters, e.g: List<Message> to List&lt;Message&gt;
    public static String escape(String html) {
        return StringEscapeUtils.escapeHtml(html);
    }
}
