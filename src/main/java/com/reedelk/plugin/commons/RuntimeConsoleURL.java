package com.reedelk.plugin.commons;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeConsoleURL {


    private RuntimeConsoleURL() {
    }

    public static String from(String runtimeHostAddress, int runtimeHostPort) {
        return message("module.check.errors.runtime.console.url", runtimeHostAddress, runtimeHostPort);
    }
}
