package de.codecentric.reedelk.plugin.validator;

import de.codecentric.reedelk.plugin.commons.FileUtils;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static de.codecentric.reedelk.plugin.commons.DefaultConstants.NameConvention;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeHomeValidator implements Validator {

    private final String runtimeHomeDirectory;

    public RuntimeHomeValidator(final String runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory;
    }

    @Override
    public void validate(Collection<String> errors) {

        if (StringUtils.isBlank(runtimeHomeDirectory)) {
            errors.add(message("runtimeBuilder.runtime.home.validator.directory.empty"));
            return;
        }

        File homeDirectoryFile = new File(runtimeHomeDirectory);
        if (!homeDirectoryFile.exists()) {
            errors.add(message("runtimeBuilder.runtime.home.validator.dir.does.not.exists"));
            return;
        }

        if (!homeDirectoryFile.isDirectory()) {
            errors.add(message("runtimeBuilder.runtime.home.validator.dir.is.not.directory"));
            return;
        }

        String[] content = homeDirectoryFile.list();
        if (content == null) {
            errors.add(message("runtimeBuilder.runtime.home.validator.generic"));
            return;
        }

        // Home directory must contain: modules/config
        boolean existsConfigDir = existsDirectoryNamed(content, NameConvention.RUNTIME_PACKAGE_CONFIG_DIRECTORY);
        if (!existsConfigDir) {
            errors.add(message("runtimeBuilder.runtime.home.validator.config.missing"));
            return;
        }

        boolean existsModulesDir = existsDirectoryNamed(content, NameConvention.RUNTIME_PACKAGE_MODULES_DIRECTORY);
        if (!existsModulesDir) {
            errors.add(message("runtimeBuilder.runtime.home.validator.modules.missing"));
            return;
        }

        boolean existsBinDir = existsDirectoryNamed(content, NameConvention.RUNTIME_PACKAGE_BIN_DIRECTORY);
        if (!existsBinDir) {
            errors.add(message("runtimeBuilder.runtime.home.validator.bin.missing"));
            return;
        }

        // Check that exists runtime jar.
        Optional<String> runtimeJarName = FileUtils.findRuntimeJarName(runtimeHomeDirectory);
        if (!runtimeJarName.isPresent()) {
            errors.add(message("runtimeBuilder.runtime.home.validator.runtime.jar.missing", runtimeHomeDirectory));
        }

        // Runtime Home Directory is valid
    }

    private boolean existsDirectoryNamed(String[] content, String targetDir) {
        return Arrays.stream(content).anyMatch(fileOrDirName -> fileOrDirName.equals(targetDir) &&
                new File(Paths.get(runtimeHomeDirectory, targetDir).toString()).isDirectory());
    }
}
