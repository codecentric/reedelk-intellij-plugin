package com.reedelk.plugin.action.openapi.reader;

import com.reedelk.plugin.action.openapi.OpenApiException;

public interface FileReader {

    String read() throws OpenApiException;

}
