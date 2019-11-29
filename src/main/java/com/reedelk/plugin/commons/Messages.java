package com.reedelk.plugin.commons;

public class Messages {

    private Messages() {
    }

    private static String formatMessage(String template, Object... args) {
        return String.format(template, args);
    }

    interface FormattedMessage {
        String format(Object... args);
    }

    public enum ModuleRun implements FormattedMessage {

        ERROR_MODULE_NOT_SELECTED("Error while executing operation. Please select a valid project module in the 'Module Run Configuration' named=[%s]."),
        ERROR_RUNTIME_NOT_SELECTED("Error while executing operation. Please select a valid ESB runtime in the 'Module Run Configuration' named=[%s]."),
        ERROR_GENERIC_MODULE_NOT_SELECTED("Could not execute action '%s'. Please select a valid project module in the run configuration."),
        ERROR_MAVEN_PROJECT_NOT_FOUND("Maven project could not be found for module=[%s]."),
        ERROR_RUNTIME_CONFIG_NOT_FOUND("Could not find runtime config with name=[%s]. Please check the module run configuration.");

        private String msg;

        ModuleRun(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }

    public enum RuntimeRun implements FormattedMessage {

        ERROR_SDK_NOT_SELECTED("Project SDK not defined. Please select a Java SDK in the project settings.");

        private String msg;

        RuntimeRun(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }

    public enum Script implements FormattedMessage {

        ERROR_REMOVE("Could not remove script named=[%s]: %s"),
        ERROR_FILE_NAME_EMPTY("The script file name must not be empty."),
        NOT_FOUND("Not found"),
        NOT_SELECTED("<Not selected>"),
        DIALOG_EDIT_TITLE("Edit Script"),
        DIALOG_DELETE_TITLE("Delete Script"),
        DIALOG_ADD_NEW_TITLE("Add New Script"),
        DIALOG_EDIT_WARNING_RETURN_KEYWORD("Script must include the 'return' keyword to return the expected value from its evaluation."),
        DIALOG_DELETE_CONFIRM_MESSAGE("Are you sure you want to delete the selected script?");


        private String msg;

        Script(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }

    public enum Misc implements FormattedMessage {

        COULD_NOT_CREATE_DIRECTORY("Could not create directory=[%s]"),
        FILE_NAME_NOT_EMPTY("File name must not be empty");

        private String msg;

        Misc(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }

}
