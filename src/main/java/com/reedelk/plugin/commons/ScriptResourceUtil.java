package com.reedelk.plugin.commons;

import com.reedelk.runtime.commons.FileExtension;

public class ScriptResourceUtil {

    private ScriptResourceUtil() {
    }

    /**
     * Input: /test/something/myscript -> Output: test/something/myscript.js
     * Input: test/something/myscript -> Output: test/something/myscript.js
     * Input: myscript -> Output: myscript.js
     */
    public static String normalize(String scriptFileName) {
        String result = FileUtils.appendExtensionToFileName(scriptFileName, FileExtension.SCRIPT);
        if (result.startsWith("/")) {
            result = result.substring(1);
        }
        return result;
    }
}
