package com.reedelk.plugin.commons;

public class Labels {

    private Labels() {
    }

    public class PropertiesPanelConfig {
        private PropertiesPanelConfig() {
        }
        public static final String FIELD_CONFIG_FILE = "Config file";
        public static final String FIELD_CONFIG_TITLE = "Config title";
    }

    public class Hint {
        private Hint() {
        }

        public static final String FLOW_SUBFLOW_TITLE = "My integration flow";
        public static final String FLOW_SUBFLOW_DESCRIPTION = "My integration flow description";
        public static final String COMPONENT_DESCRIPTION = "My component description";
        public static final String CONFIG_FILE_TITLE = "my_listener_config";
        public static final String CONFIG_TITLE = "My Listener Configuration";
    }

    public class Placeholder {
        private Placeholder() {
        }

        public static final String DESCRIPTION_ROUTER_OTHERWISE = "Otherwise subflow";
        public static final String DESCRIPTION_TRY_CATCH_TRY = "Try subflow";
        public static final String DESCRIPTION_TRY_CATCH_CATCH = "Catch subflow";
        public static final String DESCRIPTION_INBOUND = "Inbound component";
    }

    public static final String TOOL_WINDOW_FLOW_PROPERTIES_TITLE = "Flow Properties";

    public static final String PROPERTIES_PANEL_SUBFLOW_TITLE = "Subflow";
    public static final String PROPERTIES_PANEL_FLOW_TITLE = "Flow";
    public static final String PROPERTIES_PANEL_NOTHING_SELECTED = "There are no flow components selected";

    public static final String FLOW_GRAPH_TAB_TITLE = "Title";
    public static final String FLOW_GRAPH_TAB_DESCRIPTION = "Description";

    public static final String DIALOG_TITLE_ADD_CONFIGURATION = "Add Configuration";
    public static final String DIALOG_TITLE_EDIT_CONFIGURATION = "Edit Configuration";
    public static final String DIALOG_TITLE_DELETE_CONFIGURATION = "Delete Configuration";



    public static final String DIALOG_BTN_ADD_CONFIGURATION = "Add";
    public static final String DIALOG_BTN_SAVE_CONFIGURATION = "Save";
    public static final String DIALOG_BTN_SAVE_SCRIPT = "Save";

    public static final String DIALOG_MESSAGE_DELETE_CONFIRM = "Are you sure you want to delete the selected configuration?";

    public static final String ROUTER_DEFAULT_ROUTE = "otherwise";

    public static final String SCRIPT_EDITOR_CONTEXT_VARS_TITLE = "Context variables";

    public static final String FILE_CHOOSER_TITLE = "Choose File";

    public static final String DESIGNER_INFO_BUILDING_FLOW = "Building flow ...";
    public static final String DESIGNER_INFO_FLOW_CONTAINS_ERRORS = "The flow contains error, and it can not not be displayed.";

    public static final String ACTION_CREATE_FLOW_FILE_TITLE = "Reedelk flow file";
    public static final String ACTION_CREATE_FLOW_FILE_DESCRIPTION = "Create a new Reedelk flow file";
    public static final String ACTION_CREATE_FLOW_KIND = "Flow";
    public static final String ACTION_CREATE_SUBFLOW_KIND = "Subflow";

    public static final String WIZARD_RUNTIME_STEP_ADD_NEW_CONFIG = "<Add new configuration>";

    public static final String BALLOON_ERROR = "<p><b>Error:</b><p>%s</p>";
    public static final String CONFIG_NOT_SELECTED_ITEM = "<Not selected>";


    public static final String ACTION_MAP_TABLE_ADD = "Add";
    public static final String ACTION_MAP_TABLE_REMOVE = "Remove";

    public static final String UNKNOWN_COMPONENT = "Unknown component";

    public static final String ROUTER_TABLE_CONTAINER_TITLE = "Condition and route definitions";

}
