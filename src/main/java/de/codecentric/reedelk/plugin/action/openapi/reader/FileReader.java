package de.codecentric.reedelk.plugin.action.openapi.reader;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiException;

public interface FileReader {

    String read() throws OpenApiException;

}
