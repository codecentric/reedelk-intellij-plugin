package com.reedelk.plugin.builder;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public abstract class AbstractProjectBuilderHelper {

    private static final Logger LOG = Logger.getInstance(MavenProjectBuilderHelper.class);

    // TODO: This logic is duplicated
    public Optional<VirtualFile> createDirectory(VirtualFile root, String suffix) {
        try {
            String finalDirectoryPath = Paths.get(root.getPath(), suffix).toString();
            return Optional.ofNullable(VfsUtil.createDirectories(finalDirectoryPath));
        } catch (IOException exception) {
            String message = message("moduleBuilder.hello.world.create.dir.error", suffix, exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }
}
