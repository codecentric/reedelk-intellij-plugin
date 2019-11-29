package com.reedelk.plugin.commons;

import com.reedelk.runtime.commons.FileExtension;

public class ScriptResourceUtil {

    /**
     * Input: /test/something/myscript -> Output: test/something/myscript.js
     * Input: test/something/myscript -> Output: test/something/myscript.js
     * Input: myscript -> Output: myscript.js
     */
    public static String normalize(String scriptFileName) {
        String fileNameWithExtension = FileUtils.appendExtensionToFileName(scriptFileName, FileExtension.SCRIPT);
        if (fileNameWithExtension.startsWith("/")) {
            return fileNameWithExtension.substring(1);
        } else {
            return fileNameWithExtension;
        }
    }
}
