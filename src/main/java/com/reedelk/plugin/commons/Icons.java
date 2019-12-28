package com.reedelk.plugin.commons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Icons {

    public static final Icon ModuleBuilderStart = IconLoader.getIcon("/icons/ModuleBuilderStart.png");
    public static final Icon Module = IconLoader.getIcon("/icons/ModulePackage.png");
    public static final Icon Runtime = IconLoader.getIcon("/icons/Runtime.png");
    public static final Icon ModuleDeploy = IconLoader.getIcon("/icons/ModuleDeploy.png");
    public static final Icon ModuleDeployDisabled = IconLoader.getIcon("/icons/ModuleDeploy.png");
    public static final Icon ModuleUnDeploy = IconLoader.getIcon("/icons/ModuleUndeploy.png");
    public static final Icon ModuleUnDeployDisabled = IconLoader.getIcon("/icons/ModuleUndeploy.png");
    public static final Icon FileTypeFlow = IconLoader.getIcon("/icons/FileTypeFlow.png");
    public static final Icon FileTypeSubFlow = IconLoader.getIcon("/icons/FileTypeSubflow.png");
    public static final Icon FileTypeConfig = AllIcons.Actions.Annotate;

    private Icons() {
    }

    public static class Component {

        private Component() {
        }

        public static final Icon Default = IconLoader.getIcon("/icons/ComponentDefault.png");
        public static final Icon Inbound = AllIcons.General.ArrowRight;
        public static final Icon Processor = AllIcons.General.GearPlain;

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
        public static final Icon ScriptLoadError = IconLoader.getIcon("/icons/ScriptLoadError.png");
    }

    public static class ToolWindow {

        private ToolWindow() {
        }

        public static final Icon Properties = AllIcons.Toolwindows.ToolWindowStructure;
        public static final Icon Components = AllIcons.Toolwindows.ToolWindowPalette;
    }

    public static class Misc {

        private Misc() {
        }

        public static final Icon Info = IconLoader.getIcon("/icons/Info.png");
        public static final Icon ShowPassword = IconLoader.getIcon("/icons/PasswordShow.png");
        public static final Icon HidePassword = IconLoader.getIcon("/icons/PasswordHide.png");
    }
}
