package com.esb.plugin.commons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Icons {

    public static final Icon Module = IconLoader.getIcon("/icons/icon-16x-blue.png");
    public static final Icon Runtime = IconLoader.getIcon("/icons/play-16x-red.png");
    public static final Icon ModuleDeploy = IconLoader.getIcon("/icons/deploy-16x-blue.png");
    public static final Icon ModuleDeployDisabled = IconLoader.getIcon("/icons/deploy-16x-blue-disabled.png");
    public static final Icon ModuleUnDeploy = IconLoader.getIcon("/icons/undeploy-16x-blue.png");
    public static final Icon ModuleUnDeployDisabled = IconLoader.getIcon("/icons/undeploy-16x-blue-disabled.png");
    public static final Icon FileTypeFlow = IconLoader.getIcon("/icons/icon-flow-file-16x.png");
    public static final Icon FileTypeFlowConfig = IconLoader.getIcon("/icons/icon-flow-config-file-16x.png");

    public static Icon forComponentAsIcon(String componentName) {
        try {
            return IconLoader.getIcon("/icons/component/" + componentName + "-icon.png");
        } catch (Exception e) {
            return IconLoader.getIcon("/icons/default-component-icon.png");
        }
    }

    public static Image forComponentAsImage(String componentName) {
        URL resource = componentIconResource(componentName);
        if (resource == null) {
            URL defaultResource = componentIconResource("default-component");
            return Toolkit.getDefaultToolkit().getImage(defaultResource);
        }
        return Toolkit.getDefaultToolkit().getImage(resource);
    }


    private static URL componentIconResource(String componentName) {
        return Icons.class.getResource("/icons/component/" + componentName + ".png");
    }
}
