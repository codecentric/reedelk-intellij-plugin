package com.reedelk.plugin.commons;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;

import java.nio.file.Paths;

public class DefaultConstants {

    private DefaultConstants() {
    }

    public static final FileType SCRIPT_FILE_TYPE = FileTypeManager.getInstance().getFileTypeByExtension("reedelk");
    public static final String SCRIPT_TMP_FILE_NAME = "%s." + SCRIPT_FILE_TYPE.getDefaultExtension();

    public static final String PROJECT_SOURCES_FOLDER = Paths.get("src").toString();
    public static final String PROJECT_RESOURCES_FOLDER = Paths.get("src", "main", "resources").toString();

    public static final int DEFAULT_CHECK_ERROR_DELAY_MILLIS = 500;

    public static class RestApi {

        private RestApi() {
        }

        private static final String ADMIN_CONSOLE_TEMPLATE_URL = "http://%s:%d/api";
        public static final String MODULE = "/module";
        public static final String MODULE_DEPLOY = "/module/deploy";
        public static final String HOT_SWAP = "/hotswap";
        public static final String HEALTH = "/health";

        public static String apiURLOf(String address, int port, String path) {
            return String.format(ADMIN_CONSOLE_TEMPLATE_URL, address, port) + path;
        }
    }

    public static class NameConvention {

        private NameConvention() {
        }
        // NAME_CONVENTION: Depends on launcher main class fully qualified name
        public static final String RUNTIME_LAUNCHER_CLASS = "com.reedelk.runtime.Launcher";
        // NAME_CONVENTION: Depends on build file name used
        public static final String RUNTIME_DISTRIBUTION_ROOT_FOLDER_PREFIX = "reedelk-runtime-";
        // NAME_CONVENTION: Depends on the artifactId of the runtime artifact .jar (runtime) project.
        public static final String RUNTIME_JAR_FILE_PREFIX = "runtime";
        // NAME_CONVENTION: Depends on runtime package structure
        public static final String RUNTIME_PACKAGE_BIN_DIRECTORY = "bin";
        // NAME_CONVENTION: Depends on runtime package structure
        public static final String RUNTIME_PACKAGE_LIB_DIRECTORY = "lib";
        // NAME_CONVENTION: Depends on runtime package structure
        public static final String RUNTIME_PACKAGE_CONFIG_DIRECTORY = "config";
        // NAME_CONVENTION: Depends on runtime package structure
        public static final String RUNTIME_PACKAGE_MODULES_DIRECTORY = "modules";
        // NAME_CONVENTION: Depends on admin console runtime properties
        public static final String DEFAULT_ADMIN_PORT_PARAM_NAME = "admin.console.port";
        // NAME_CONVENTION: Depends on admin console runtime properties
        public static final String DEFAULT_ADMIN_HOST_PARAM_NAME = "admin.console.address";
    }

    public static class ManifestAttribute {

        private ManifestAttribute() {
        }

        public static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    }

    public static class OpenApi {

        private OpenApi() {
        }

        public static final String TARGET_DIRECTORY = "my-api";
        public static final String LOCALHOST = "localhost";
        public static final String ANY_ADDRESS = "0.0.0.0";
        public static final String BASE_PATH = "/v1";
        public static final int HTTP_PORT = 8484;
    }

    public static class Template {

        private Template() {
        }

        public static final String DEFAULT_HOST = "0.0.0.0";
        public static final int DEFAULT_PORT = 8282;
    }
}
