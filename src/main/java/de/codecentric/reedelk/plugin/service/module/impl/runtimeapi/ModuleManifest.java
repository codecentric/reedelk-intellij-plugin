package de.codecentric.reedelk.plugin.service.module.impl.runtimeapi;

import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import de.codecentric.reedelk.runtime.commons.ModuleProperties;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

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
        addAttribute(CREATED_BY, ReedelkBundle.message("plugin.display.name"));
        addAttribute(INTEGRATION_MODULE, Boolean.TRUE.toString());
    }

    private void addAttribute(Attributes.Name name, String value) {
        getMainAttributes().put(name, value);
    }

    private static String normalizeVersion(String version) {
        return version.replaceAll("-", ".");
    }

    private static final Attributes.Name INTEGRATION_MODULE = new Attributes.Name(ModuleProperties.Bundle.INTEGRATION_MODULE_HEADER);

    private static final Attributes.Name BUILT_BY = new Attributes.Name(ModuleProperties.Bundle.BUILT_BY);
    private static final Attributes.Name BUILD_JDK = new Attributes.Name(ModuleProperties.Bundle.BUILD_JDK);
    private static final Attributes.Name CREATED_BY = new Attributes.Name(ModuleProperties.Bundle.CREATED_BY);
    private static final Attributes.Name BUNDLE_NAME = new Attributes.Name(ModuleProperties.Bundle.BUNDLE_NAME);
    private static final Attributes.Name BUNDLE_VERSION = new Attributes.Name(ModuleProperties.Bundle.MODULE_VERSION);
    private static final Attributes.Name BND_LAST_MODIFIED = new Attributes.Name(ModuleProperties.Bundle.BND_LAST_MODIFIED);
    private static final Attributes.Name BUNDLE_SYMBOLIC_NAME = new Attributes.Name(ModuleProperties.Bundle.MODULE_SYMBOLIC_NAME);
    private static final Attributes.Name BUNDLE_MANIFEST_VERSION = new Attributes.Name(ModuleProperties.Bundle.BUNDLE_MANIFEST_VERSION);
}

