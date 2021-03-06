package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.exception.PluginException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import de.codecentric.reedelk.runtime.commons.FileExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

import static de.codecentric.reedelk.plugin.commons.DefaultConstants.NameConvention;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isBlank;

public class FileUtils {

    private static final Logger LOG = Logger.getInstance(FileUtils.class);

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

    public static Optional<VirtualFile> createDirectory(VirtualFile root, String suffix) {
        String finalDirectoryPath = Paths.get(root.getPath(), suffix).toString();
        try {
            return Optional.ofNullable(VfsUtil.createDirectoryIfMissing(finalDirectoryPath));
        } catch (IOException exception) {
            String message = message("create.directory.error", finalDirectoryPath, exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }

    public static Optional<VirtualFile> createDirectory(Path directoryPath) {
        String finalDirectoryPath = directoryPath.toString();
        try {
            return Optional.ofNullable(VfsUtil.createDirectoryIfMissing(finalDirectoryPath));
        } catch (IOException exception) {
            String message = message("create.directory.error", finalDirectoryPath, exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }
}
