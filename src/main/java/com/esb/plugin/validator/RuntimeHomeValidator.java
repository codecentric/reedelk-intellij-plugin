package com.esb.plugin.validator;

import com.esb.plugin.utils.ESBFileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;

public class RuntimeHomeValidator implements Validator {

    private final String runtimeHomeDirectory;

    public RuntimeHomeValidator(final String runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory;
    }

    @Override
    public void validate(Collection<String> errors) {
        File homeDirectoryFile = new File(runtimeHomeDirectory);
        if (!homeDirectoryFile.exists()) {
            errors.add("Runtime home directory does not exists");
            return;
        }

        if (!homeDirectoryFile.isDirectory()) {
            errors.add("Runtime home directory is not a directory");
            return;
        }

        String[] content = homeDirectoryFile.list();
        if (content == null) {
            errors.add("Runtime home directory");
            return;
        }

        // Home directory must contain: modules/config
        boolean existsConfigDir = existsDirectoryNamed(content, "config");
        if (!existsConfigDir) {
            errors.add("Runtime home directory does not contain 'config' folder");
            return;
        }

        boolean existsModulesDir = existsDirectoryNamed(content, "modules");
        if (!existsModulesDir) {
            errors.add("Runtime home directory does not contain 'modules' folder");
            return;
        }

        boolean existsBinDir = existsDirectoryNamed(content, "bin");
        if (!existsBinDir) {
            errors.add("Runtime home directory does not contain 'bin' folder");
            return;
        }

        // Check that exists runtime jar.
        Optional<String> runtimeJarName = ESBFileUtils.findRuntimeJarName(runtimeHomeDirectory);
        if (!runtimeJarName.isPresent()) {
            errors.add(format("Could not find suitable runtime (home directory: %s)", runtimeHomeDirectory));
        }

        // Runtime Home Directory is valid
    }

    private boolean existsDirectoryNamed(String[] content, String targetDir) {
        return Arrays.stream(content).anyMatch(fileOrDirName -> fileOrDirName.equals(targetDir) &&
                new File(Paths.get(runtimeHomeDirectory, targetDir).toString()).isDirectory());
    }
}
