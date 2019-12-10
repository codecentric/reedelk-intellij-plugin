package com.reedelk.plugin.commons;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.reedelk.runtime.commons.FileExtension;

import java.nio.file.Paths;

public class Defaults {

    private Defaults() {
    }

    public static final String DEFAULT_ADMIN_PORT_PARAM_NAME = "admin.console.port";
    public static final String DEFAULT_ADMIN_HOST_PARAM_NAME = "admin.console.address";

    public static final String PROJECT_RESOURCES_FOLDER = Paths.get("src", "main", "resources").toString();

    public static final String DEFAULT_DYNAMIC_VALUE_SCRIPT_VIRTUAL_FILE_NAME = "tmp.js";

    public static final FileType JAVASCRIPT_FILE_TYPE =
            FileTypeManager.getInstance().getFileTypeByExtension(FileExtension.SCRIPT.value());


    public static class RestApi {
        private RestApi() {
        }
        public static final String MODULE = "/module";
        public static final String HOT_SWAP = "/hotswap";
    }
}
