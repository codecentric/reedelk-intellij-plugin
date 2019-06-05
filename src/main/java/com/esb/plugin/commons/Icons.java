package com.esb.plugin.commons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Icons {

    public static final String PNG_EXTENSION = "png";

    public static final Icon Module = IconLoader.getIcon("/icons/icon-16x-blue.png");
    public static final Icon Runtime = IconLoader.getIcon("/icons/play-16x-red.png");
    public static final Icon ModuleDeploy = IconLoader.getIcon("/icons/deploy-16x-blue-disabled.png");
    public static final Icon ModuleDeployDisabled = IconLoader.getIcon("/icons/deploy-16x-blue-disabled.png");
    public static final Icon ModuleUnDeploy = IconLoader.getIcon("/icons/undeploy-16x-blue-disabled.png");
    public static final Icon ModuleUnDeployDisabled = IconLoader.getIcon("/icons/undeploy-16x-blue-disabled.png");
    public static final Icon FileTypeFlow = IconLoader.getIcon("/icons/icon-flow-file-16x.png");
    public static final Icon FileTypeFlowConfig = IconLoader.getIcon("/icons/icon-flow-config-file-16x.png");

    public static class Component {

        public static final Icon DefaultComponentIcon = IconLoader.getIcon("/icons/default-component-icon.png");

        private static final Map<String, Icon> KEY_ICON_MAP = new HashMap<>();

        public static void put(String key, Icon icon) {
            KEY_ICON_MAP.put(key, icon);
        }

        public static Icon get(String key) {
            return KEY_ICON_MAP.getOrDefault(key, DefaultComponentIcon);
        }
    }

}
