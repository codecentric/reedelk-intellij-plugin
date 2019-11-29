package com.reedelk.plugin.commons;

import com.reedelk.plugin.exception.PluginException;
import com.reedelk.runtime.commons.FileExtension;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import static com.reedelk.runtime.api.commons.StringUtils.isBlank;

public class FileUtils {

    private FileUtils() {
    }

    public static Optional<String> findRuntimeJarName(String runtimeHomeDirectory) {
        String binDirectoryPath = Paths.get(runtimeHomeDirectory, "bin").toString();
        File binDirectory = new File(binDirectoryPath);
        String[] runtimeFile = binDirectory.list((dir, name) -> name.startsWith("runtime-") && name.endsWith(".jar"));
        if (runtimeFile != null && runtimeFile.length == 1) {
            return Optional.of(runtimeFile[0]);
        }
        return Optional.empty();
    }

    public static String appendExtensionToFileName(String fileName, FileExtension extension) {
        if (isBlank(fileName)) {
            throw new PluginException(Messages.Misc.FILE_NAME_NOT_EMPTY.format());
        }
        return fileName.endsWith("." + extension.value()) ?
                fileName :
                fileName + "." + extension.value();
    }
}
