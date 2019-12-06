package com.reedelk.plugin.commons;

import com.intellij.icons.AllIcons;
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
    public static final Icon FileTypeConfig = AllIcons.Actions.Annotate;

    private Icons() {
    }

    public static class Component {

        private Component() {
        }

        public static final Icon Default = IconLoader.getIcon("/icons/default-component-icon.png");
        public static final Icon Inbound = IconLoader.getIcon("/icons/inbound-type-component.png");
        public static final Icon Processor = IconLoader.getIcon("/icons/processor-type-component.png");

        private static final Map<String, Icon> KEY_ICON_MAP = new HashMap<>();

        public static void put(String key, Icon icon) {
            KEY_ICON_MAP.put(key, icon);
        }

        public static Icon get(String key) {
            return KEY_ICON_MAP.getOrDefault(key, Default);
        }
    }


    public static class Script {

        private Script() {
        }

        public static final Icon Code = AllIcons.Json.Object;
        public static final Icon ScriptLoadError = IconLoader.getIcon("/icons/script-load-error.png");
    }

    public static class ToolWindow {

        private ToolWindow() {
        }

        public static final Icon Properties = IconLoader.getIcon("/icons/toolbar-properties.png");
    }

    public static class Misc {

        private Misc() {
        }

        public static final Icon ShowPassword = IconLoader.getIcon("/icons/show_password.png");
        public static final Icon HidePassword = IconLoader.getIcon("/icons/hide_password.png");
    }
}
