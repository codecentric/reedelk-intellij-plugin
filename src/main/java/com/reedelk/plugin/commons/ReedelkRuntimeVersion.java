package com.reedelk.plugin.commons;

import com.reedelk.plugin.message.ReedelkBundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.reedelk.plugin.commons.DefaultConstants.ManifestAttribute;
import static com.reedelk.plugin.commons.DefaultConstants.NameConvention;

public class ReedelkRuntimeVersion {

    private ReedelkRuntimeVersion() {
    }

    public static String from(String runtimeHomeDirectory) {
        return FileUtils.findRuntimeJarName(runtimeHomeDirectory)
                .flatMap(runtimeJarFileName -> {
                    Path runtimeJarFilePath = Paths.get(runtimeHomeDirectory, NameConvention.RUNTIME_PACKAGE_BIN_DIRECTORY, runtimeJarFileName);
                    return getAttributeValue(runtimeJarFilePath.toFile(), ManifestAttribute.IMPLEMENTATION_VERSION);
                }).orElse(ReedelkBundle.message("default.reedelk.version"));
    }

    private static Optional<String> getAttributeValue(File jarFile, String attributeKey) {
        try {
            Attributes attributes = getManifestAttributesOf(jarFile);
            return Optional.ofNullable(attributes.getValue(attributeKey));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private static Attributes getManifestAttributesOf(File file) throws IOException {
        try (JarFile jarFile = new JarFile(file)) {
            Manifest manifest = jarFile.getManifest();
            return manifest.getMainAttributes();
        }
    }
}
