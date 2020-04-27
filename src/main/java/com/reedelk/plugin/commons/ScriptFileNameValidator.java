package com.reedelk.plugin.commons;

import com.reedelk.runtime.api.commons.StringUtils;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class ScriptFileNameValidator {

    private static final Collection<String> SCRIPT_RESERVED_KEYWORDS = unmodifiableList(asList(
            "as", "assert", "break", "case", "catch", "class", "const", "continue",
            "def", "default", "do", "else", "enum", "extends", "false", "finally",
            "for", "goto", "if", "implements", "import", "in", "instanceof",
            "interface", "new", "null", "package", "return", "super", "switch",
            "this", "throw", "throws", "trait", "true", "try", "var", "while"));

    private static final Pattern FUNCTION_NAME_VALIDATOR = Pattern.compile("^[$a-zA-Z_][0-9a-zA-Z_$]*$");


    private ScriptFileNameValidator() {
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
            return matcher.matches() && !SCRIPT_RESERVED_KEYWORDS.contains(fileNameWithoutExtension);
        }
    }

    public static String getFileNameWithoutExtensionFrom(String scriptFileNameWithPathToAdd) {
        Path path = Paths.get(scriptFileNameWithPathToAdd);
        return FilenameUtils.removeExtension(path.getFileName().toString());
    }
}
