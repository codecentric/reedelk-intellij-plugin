package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.plugin.commons.Icons;
import com.intellij.openapi.diagnostic.Logger;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

class ComponentIconsAnalyzer {

    private static final Logger LOG = Logger.getInstance(ComponentIconsAnalyzer.class);

    private static final String ICONS_EXTENSION = "png";

    private Map<String, Image> nameMap = new HashMap<>();

    ComponentIconsAnalyzer(ScanResult scanResult) {
        ResourceList allImages = scanResult.getResourcesWithExtension(ICONS_EXTENSION);
        extract(allImages);
    }

    private void extract(ResourceList resources) {
        Map<String, ResourceList> resourceNameAndData = resources.asMap();
        for (Map.Entry<String, ResourceList> entry : resourceNameAndData.entrySet()) {
            String key = entry.getKey();
            int lastSlash = key.lastIndexOf("/");

            ResourceList value = entry.getValue();
            Resource resource = value.get(0);
            try {
                Image read = ImageIO.read(resource.open());
                nameMap.put(key.substring(lastSlash + 1, key.length() - 4), read);
            } catch (IOException e) {
                LOG.error(format("Could not read image for resource named %s", key), e);
            }
        }
    }

    Image getImageByFullyQualifiedName(String fullyQualifiedName) {
        return nameMap.getOrDefault(fullyQualifiedName, getDefaultComponentImage());
    }

    Icon getIconByFullyQualifiedName(String fullyQualifiedName) {
        if (nameMap.containsKey(fullyQualifiedName + "-icon")) {
            return new ImageIcon(nameMap.get(fullyQualifiedName + "-icon"));
        } else {
            return getDefaultComponentIcon();
        }
    }

    private static Icon getDefaultComponentIcon() {
        return Icons.DEFAULT_COMPONENT_ICON.get();
    }

    private static Image getDefaultComponentImage() {
        return Icons.DEFAULT_COMPONENT_IMAGE.get();
    }
}
