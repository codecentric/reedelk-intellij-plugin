package com.esb.plugin.designer.icon;

import java.awt.*;
import java.net.URL;

public class IconProvider {
    /**
     * private static final Image DEFAULT = Toolkit.getDefaultToolkit()
     * .getImage(componentIconResource("default"));
     * <p>
     * private static final Map<String,Image> ICONS;
     * static {
     * Map<String, Image> tmp = new HashMap<>();
     * tmp.put(CHOICE.qualifiedName(), forComponent(CHOICE.qualifiedName()));
     * tmp.put(FORK.qualifiedName(), forComponent(CHOICE.qualifiedName()));
     * tmp.put(FLOW_REFERENCE.qualifiedName(), forComponent(FLOW_REFERENCE.qualifiedName()));
     * ICONS = tmp;
     * }
     * <p>
     * public static Image forComponent(String componentName) {
     * if (!ICONS.containsKey(componentName)) {
     * URL resource = componentIconResource(componentName);
     * Image image = Toolkit.getDefaultToolkit().getImage(resource);
     * ICONS.put(componentName, image);
     * }
     * return ICONS.getOrDefault(componentName,DEFAULT);
     * }
     */
    public static Image forComponent(String componentName) {
        URL resource = componentIconResource(componentName);
        if (resource == null) {
            URL defaultResource = componentIconResource("default");
            return Toolkit.getDefaultToolkit().getImage(defaultResource);
        }
        return Toolkit.getDefaultToolkit().getImage(resource);
    }

    private static URL componentIconResource(String componentName) {
        return IconProvider.class.getResource("/icons/component/" + componentName + ".png");
    }
}
