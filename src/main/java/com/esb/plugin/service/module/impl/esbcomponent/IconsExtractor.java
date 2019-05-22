package com.esb.plugin.service.module.impl.esbcomponent;

import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IconsExtractor {

    private static final String ICONS_EXTENSION = "png";

    private Map<String, Image> nameMap = new HashMap<>();

    public IconsExtractor(ScanResult scanResult) {
        ResourceList allImages = scanResult.getResourcesWithExtension(ICONS_EXTENSION);
        extract(allImages);
    }

    private void extract(ResourceList resources) {
        Map<String, ResourceList> resourceNameAndData = resources.asMap();
        for (Map.Entry<String, ResourceList> entry : resourceNameAndData.entrySet()) {
            String key = entry.getKey();
            ResourceList value = entry.getValue();
            Resource resource = value.get(0);
            try {
                Image read = ImageIO.read(resource.open());
                nameMap.put(key.substring(0, key.length() - 3), read);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Image getImageByFullyQualifiedName(String fullyQualifiedName) {
        return nameMap.get(fullyQualifiedName);
    }
}
