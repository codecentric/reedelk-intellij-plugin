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

    public enum ModuleRunConfiguration implements FormattedMessage {
        ERROR_MODULE_NOT_SELECTED("Error while executing operation. Please select a valid project module in the 'Module Run Configuration' named=[%s]."),
        ERROR_RUNTIME_NOT_SELECTED("Error while executing operation. Please select a valid ESB runtime in the 'Module Run Configuration' named=[%s]."),
        ERROR_GENERIC_MODULE_NOT_SELECTED("Could not execute action '%s'. Please select a valid project module in the run configuration.");

        private String msg;

        ModuleRunConfiguration(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }
}
