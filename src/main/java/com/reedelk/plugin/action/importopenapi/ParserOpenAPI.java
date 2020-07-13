package com.reedelk.plugin.action.importopenapi;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.util.function.BiConsumer;

public class ParserOpenAPI {

    private final String inputFilePath;

    public ParserOpenAPI(String inputFilePath) {
        // TODO: Check not empty
        this.inputFilePath = inputFilePath;
    }

    public void parse(BiConsumer<String, PathItem> processor) {
        OpenAPI openAPI = new OpenAPIV3Parser().read(inputFilePath);
        Paths paths = openAPI.getPaths();
        paths.forEach(processor);
    }
}
