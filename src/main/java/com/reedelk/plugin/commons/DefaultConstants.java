package com.reedelk.plugin.commons;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.reedelk.runtime.commons.FileExtension;

import java.nio.file.Paths;

public class DefaultConstants {

    private DefaultConstants() {
    }

    public static final String PROJECT_SOURCES_FOLDER = Paths.get("src").toString();
    public static final String PROJECT_RESOURCES_FOLDER = Paths.get("src", "main", "resources").toString();
    public static final String DEFAULT_DYNAMIC_VALUE_SCRIPT_VIRTUAL_FILE_NAME = "tmp.js";
    public static final FileType JAVASCRIPT_FILE_TYPE =
            FileTypeManager.getInstance().getFileTypeByExtension(FileExtension.SCRIPT.value());
    public static final Language JAVASCRIPT_LANGUAGE = Language.findLanguageByID("JavaScript");
    public static final int DEFAULT_CHECK_ERROR_DELAY_MILLIS = 500;

    public static class RestApi {

        private RestApi() {
        }

        public static final String MODULE = "/module";
        public static final String MODULE_DEPLOY = "/module/deploy";
        public static final String HOT_SWAP = "/hotswap";
    }

    public static class NameConvention {
        private NameConvention() {
        }
        // NAME_CONVENTION: Depends on build file name used
        public static final String RUNTIME_DISTRIBUTION_ROOT_FOLDER_PREFIX = "reedelk-runtime-";
        // NAME_CONVENTION: Depends on the artifactId of the runtime artifact .jar (runtime) project.
        public static final String RUNTIME_JAR_FILE_PREFIX = "runtime";
        // NAME_CONVENTION: Depends on runtime package structure
        public static final String RUNTIME_PACKAGE_BIN_DIRECTORY = "bin";
        // NAME_CONVENTION: Depends on runtime package structure
        public static final String RUNTIME_PACKAGE_CONFIG_DIRECTORY = "config";
        // NAME_CONVENTION: Depends on runtime package structure
        public static final String RUNTIME_PACKAGE_MODULES_DIRECTORY = "modules";
        // NAME_CONVENTION: Depends on url location of distribution
        public static final String RUNTIME_ONLINE_DISTRIBUTION_URL = "http://reedelk.com/distributions/";
        // NAME_CONVENTION: Depends on latest build file name used
        public static final String RUNTIME_ONLINE_DISTRIBUTION_ZIP_FILE_NAME = "reedelk-runtime-latest.zip";
        // NAME_CONVENTION: Depends on admin console runtime properties
        public static final String DEFAULT_ADMIN_PORT_PARAM_NAME = "admin.console.port";
        // NAME_CONVENTION: Depends on admin console runtime properties
        public static final String DEFAULT_ADMIN_HOST_PARAM_NAME = "admin.console.address";
    }
}
