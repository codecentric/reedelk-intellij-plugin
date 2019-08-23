package com.reedelk.plugin.commons;

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
    public static final Icon FileTypeSubFlow = IconLoader.getIcon("/icons/icon-subflow-file-16x.png");
    public static final Icon FileTypeFlowConfig = IconLoader.getIcon("/icons/icon-flow-config-file-16x.png");

    private Icons() {
    }

    public static class Component {

        private Component() {
        }

        public static final Icon DefaultComponent = IconLoader.getIcon("/icons/default-component-icon.png");
        public static final Icon InboundTypeComponent = IconLoader.getIcon("/icons/inbound-type-component.png");
        public static final Icon ProcessorTypeComponent = IconLoader.getIcon("/icons/processor-type-component.png");

        private static final Map<String, Icon> KEY_ICON_MAP = new HashMap<>();

        public static void put(String key, Icon icon) {
            KEY_ICON_MAP.put(key, icon);
        }

        public static Icon get(String key) {
            return KEY_ICON_MAP.getOrDefault(key, DefaultComponent);
        }
    }

    public static class Config {

        private Config() {
        }

        public static final Icon Add = IconLoader.getIcon("/icons/config-add.png");
        public static final Icon Delete = IconLoader.getIcon("/icons/config-delete.png");
        public static final Icon DeleteDisabled = IconLoader.getIcon("/icons/config-delete-disabled.png");
        public static final Icon Edit = IconLoader.getIcon("/icons/config-edit.png");
        public static final Icon EditDisabled = IconLoader.getIcon("/icons/config-edit-disabled.png");
    }

    public static class Script {

        private Script() {
        }

        public static final Icon Edit = IconLoader.getIcon("/icons/script-edit.png");
        public static final Icon EditDisabled = IconLoader.getIcon("/icons/script-edit-disabled.png");
        public static final Icon EditSelected = IconLoader.getIcon("/icons/script-edit-selected.png");
        public static final Icon Code = IconLoader.getIcon("/icons/code.png");
    }

    public static class ToolWindow {

        private ToolWindow() {
        }

        public static final Icon Properties = IconLoader.getIcon("/icons/toolbar-properties.png");
    }

    public static class MapType {

        private MapType() {
        }

        public static final Icon AddItem = IconLoader.getIcon("/icons/map-item-add.png");
        public static final Icon RemoveItem = IconLoader.getIcon("/icons/map-item-remove.png");
    }
}
