package com.reedelk.plugin.component.scanner;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.Images;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;

public class ComponentIconAndImageProvider {

    private static final Logger LOG = Logger.getInstance(ComponentIconAndImageProvider.class);

    private ComponentIconAndImageProvider() {
    }

    static void loadFrom(ScanResult scanResult) {
        ResourceList allImages = scanResult.getResourcesWithExtension(Icons.PNG_EXTENSION);
        load(allImages);
    }

    private static void load(ResourceList resources) {
        Map<String, ResourceList> resourceNameAndData = resources.asMap();

        for (Map.Entry<String, ResourceList> entry : resourceNameAndData.entrySet()) {
            String resourcePath = entry.getKey();
            String mapKey = getMapKeyFromResourcePath(resourcePath);

            ResourceList value = entry.getValue();
            try (Resource resource = value.get(0)) {
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

    private static void registerImage(Resource resource, String mapKey) throws IOException {
        Image image = ImageIO.read(resource.open());
        Images.Component.put(mapKey, image);
    }

    private static void registerIcon(Resource resource, String mapKey) throws IOException {
        Icon icon = new ImageIcon(ImageIO.read(resource.open()));
        Icons.Component.put(mapKey, icon);
    }
}