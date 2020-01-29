package com.reedelk.plugin.service.module.impl.runtimeapi;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ModuleManifest extends Manifest {

    public ModuleManifest(String moduleVersion, String moduleName) {
        addAttribute(Attributes.Name.MANIFEST_VERSION, "1.0");
        addAttribute(BND_LAST_MODIFIED, String.valueOf(System.currentTimeMillis()));
        addAttribute(BUILD_JDK, System.getProperty("java.version"));
        addAttribute(BUILT_BY, System.getProperty("user.name"));
        addAttribute(BUNDLE_MANIFEST_VERSION, "2");
        addAttribute(BUNDLE_NAME, moduleName);
        addAttribute(BUNDLE_SYMBOLIC_NAME, moduleName);
        addAttribute(BUNDLE_VERSION, normalizeVersion(moduleVersion));
        addAttribute(CREATED_BY, message("plugin.display.name"));
        addAttribute(ESB_MODULE, Boolean.TRUE.toString());
    }

    private void addAttribute(Attributes.Name name, String value) {
        getMainAttributes().put(name, value);
    }

    private static String normalizeVersion(String version) {
        return version.replaceAll("-", ".");
    }

    private static final Attributes.Name BUILT_BY = new Attributes.Name("Built-By");
    private static final Attributes.Name BUILD_JDK = new Attributes.Name("Build-Jdk");
    private static final Attributes.Name CREATED_BY = new Attributes.Name("Created-By");
    private static final Attributes.Name ESB_MODULE = new Attributes.Name("ESB-Module");
    private static final Attributes.Name BUNDLE_NAME = new Attributes.Name("Bundle-Name");
    private static final Attributes.Name BUNDLE_VERSION = new Attributes.Name("Bundle-Version");
    private static final Attributes.Name BND_LAST_MODIFIED = new Attributes.Name("Bnd-LastModified");
    private static final Attributes.Name BUNDLE_SYMBOLIC_NAME = new Attributes.Name("Bundle-SymbolicName");
    private static final Attributes.Name BUNDLE_MANIFEST_VERSION = new Attributes.Name("Bundle-ManifestVersion");
}

