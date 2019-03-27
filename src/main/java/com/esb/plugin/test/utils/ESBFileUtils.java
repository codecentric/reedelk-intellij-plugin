package com.esb.plugin.test.utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public class ESBFileUtils {

    public static Optional<String> findRuntimeJarName(String runtimeHomeDirectory) {
        String binDirectoryPath = Paths.get(runtimeHomeDirectory, "bin").toString();
        File binDirectory = new File(binDirectoryPath);
        String[] runtimeFile = binDirectory.list((dir, name) -> name.startsWith("runtime-") && name.endsWith(".jar"));
        if (runtimeFile != null && runtimeFile.length == 1) {
            return Optional.of(runtimeFile[0]);
        }
        return Optional.empty();
    }
}
