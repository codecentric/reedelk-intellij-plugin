package com.esb.plugin.component.scanner;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.commons.Images;
import com.intellij.openapi.diagnostic.Logger;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;

public class ComponentIconAndImageLoader {

    private static final Logger LOG = Logger.getInstance(ComponentIconAndImageLoader.class);

    public ComponentIconAndImageLoader(ScanResult scanResult) {
        ResourceList allImages = scanResult.getResourcesWithExtension(Icons.PNG_EXTENSION);
        extract(allImages);
    }

    private void extract(ResourceList resources) {
        Map<String, ResourceList> resourceNameAndData = resources.asMap();

        for (Map.Entry<String, ResourceList> entry : resourceNameAndData.entrySet()) {
            String resourcePath = entry.getKey();
            String mapKey = getMapKeyFromResourcePath(resourcePath);

            ResourceList value = entry.getValue();
            Resource resource = value.get(0);
            try {
                if (mapKey.endsWith("-icon")) {
                    mapKey = mapKey.substring(0, mapKey.length() - 5);
                    registerIcon(resource, mapKey);
                } else {
                    registerImage(resource, mapKey);
                }
            } catch (IOException e) {
                LOG.error(format("Could not read image for resource named %s", resourcePath), e);
            }
        }
    }

    // From last slash until the dot before the png extension.
    private static String getMapKeyFromResourcePath(String resourcePath) {
        int lastSlash = resourcePath.lastIndexOf("/");
        return resourcePath.substring(lastSlash + 1, resourcePath.length() - 4);
    }

    private void registerImage(Resource resource, String mapKey) throws IOException {
        Image image = ImageIO.read(resource.open());
        Images.Component.put(mapKey, image);
    }

    private void registerIcon(Resource resource, String mapKey) throws IOException {
        Icon icon = new ImageIcon(ImageIO.read(resource.open()));
        Icons.Component.put(mapKey, icon);
    }
}
