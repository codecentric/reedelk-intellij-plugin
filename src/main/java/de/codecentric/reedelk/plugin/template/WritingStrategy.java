package de.codecentric.reedelk.plugin.template;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Optional;

public interface WritingStrategy {

    Optional<VirtualFile> write(String templateText, VirtualFile destinationDir, String fileName) throws IOException;
}
