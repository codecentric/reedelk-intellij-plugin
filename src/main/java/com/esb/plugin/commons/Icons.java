package com.esb.plugin.commons;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
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

    public static Supplier<Image> DEFAULT_COMPONENT_IMAGE = Suppliers.memoize(() -> {
        URL defaultResource = Icons.class.getResource("/icons/default-component.png");
        return Toolkit.getDefaultToolkit().getImage(defaultResource);
    });

    public static Supplier<Icon> DEFAULT_COMPONENT_ICON = Suppliers.memoize(() -> {
        URL resource = Icons.class.getResource("/icons/default-component-icon.png");
        return new ImageIcon(resource);
    });
}
