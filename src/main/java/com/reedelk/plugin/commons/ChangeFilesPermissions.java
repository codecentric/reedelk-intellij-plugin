package com.reedelk.plugin.commons;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class ChangeFilesPermissions {

    public static void to(Path filePath, String permissions) {
        Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString(permissions);
        try {
            Files.setPosixFilePermissions(filePath, ownerWritable);
        } catch (Exception ignored) {
            // Error thrown if we cannot change permissions.
            // On Windows this would fail, the user must manually change it.
        }
    }
}
