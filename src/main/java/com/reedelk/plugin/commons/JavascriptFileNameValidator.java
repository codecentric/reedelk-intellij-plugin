package com.reedelk.plugin.commons;

import com.reedelk.runtime.api.commons.StringUtils;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavascriptFileNameValidator {

    private static final Collection<String> JAVASCRIPT_RESERVED_KEYWORDS = Arrays.asList(
            "abstract", "arguments", "await", "boolean", "break","byte","case","catch",
            "char","class","const","continue", "debugger","default","delete","do",
            "double","else","enum","eval", "export","extends","false", "final","finally",
            "float","for","function", "goto","if","implements","import","in","instanceof",
            "int","interface", "let","long","native","new","null","package","private",
            "protected", "public", "return", "short", "static","super","switch","synchronized",
            "this", "throw","throws","transient","true", "try","typeof","var","void", "volatile",
            "while","with","yield");

    private static final Pattern FUNCTION_NAME_VALIDATOR = Pattern.compile("^[$a-zA-Z_][0-9a-zA-Z_$]*$");


    private JavascriptFileNameValidator() {
    }

    // Validates only the file name at the end of the path.
    public static boolean validate(String scriptFileNameWithPathToAdd) {
        if (StringUtils.isBlank(scriptFileNameWithPathToAdd)) {
            return false;
        } else if (scriptFileNameWithPathToAdd.endsWith("/") ||
                scriptFileNameWithPathToAdd.endsWith("\\")) {
            return false;
        } else {
            String fileNameWithoutExtension = getFileNameWithoutExtensionFrom(scriptFileNameWithPathToAdd);
            Matcher matcher = FUNCTION_NAME_VALIDATOR.matcher(fileNameWithoutExtension);
            return matcher.matches() && !JAVASCRIPT_RESERVED_KEYWORDS.contains(fileNameWithoutExtension);
        }
    }

    public static String getFileNameWithoutExtensionFrom(String scriptFileNameWithPathToAdd) {
        Path path = Paths.get(scriptFileNameWithPathToAdd);
        return FilenameUtils.removeExtension(path.getFileName().toString());
    }
}
