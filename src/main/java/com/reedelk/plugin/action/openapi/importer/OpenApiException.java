package com.reedelk.plugin.action.openapi.importer;

import java.io.IOException;

public class OpenApiException extends Exception {

    public OpenApiException(String message, IOException exception) {
        super(message, exception);
    }
}
