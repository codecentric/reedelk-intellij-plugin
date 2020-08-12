package com.reedelk.plugin.action.openapi;

import java.io.IOException;

public class OpenApiException extends Exception {

    public OpenApiException(String message) {
        super(message);
    }

    public OpenApiException(String message, IOException exception) {
        super(message, exception);
    }
}
