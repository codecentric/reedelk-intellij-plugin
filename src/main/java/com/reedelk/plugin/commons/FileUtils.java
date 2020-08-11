package com.reedelk.plugin.commons;

import com.reedelk.plugin.exception.PluginException;
import com.reedelk.runtime.commons.FileExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

import static com.reedelk.plugin.commons.DefaultConstants.NameConvention;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;

public class FileUtils {

    private FileUtils() {
    }

    public static Optional<String> findRuntimeJarName(String runtimeHomeDirectory) {
        String binDirectoryPath = Paths.get(runtimeHomeDirectory, NameConvention.RUNTIME_PACKAGE_BIN_DIRECTORY).toString();
        File binDirectory = new File(binDirectoryPath);
        String[] runtimeFile = binDirectory.list((dir, name) ->
                name.startsWith(NameConvention.RUNTIME_JAR_FILE_PREFIX) && FileExtension.JAR.is(name));
        if (runtimeFile != null && runtimeFile.length == 1) {
            return Optional.of(runtimeFile[0]);
        }
        return Optional.empty();
    }

    public static String appendExtensionToFileName(String fileName, FileExtension extension) {
        if (isBlank(fileName)) {
            throw new PluginException(message("file.name.not.empty"));
        }
        return fileName.endsWith("." + extension.value()) ?
                fileName :
                fileName + "." + extension.value();
    }

    public static String readFileToString(String filePath) throws IOException {
        try (Scanner scanner = new Scanner(Paths.get(filePath), StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }
}
