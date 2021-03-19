package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.message.ReedelkBundle;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeConsoleURL {

    private RuntimeConsoleURL() {
    }

    public static String from(String runtimeHostAddress, int runtimeHostPort) {
        return ReedelkBundle.message("module.check.errors.runtime.console.url", runtimeHostAddress, String.valueOf(runtimeHostPort));
    }
}
