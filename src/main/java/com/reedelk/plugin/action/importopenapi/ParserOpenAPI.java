package com.reedelk.plugin.action.importopenapi;


import com.reedelk.runtime.api.commons.StringUtils;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.util.function.BiConsumer;

public class ParserOpenAPI {

    private final String inputFilePath;
    private OpenAPI openAPI;

    public ParserOpenAPI(String inputFilePath) {
        // TODO: Check not empty
        this.inputFilePath = inputFilePath;
    }

    public void parse() {
        openAPI = new OpenAPIV3Parser().read(inputFilePath);
    }

    public void forEachPath(BiConsumer<String, PathItem> processor) {
        Paths paths = openAPI.getPaths();
        paths.forEach(processor);
    }

    public String getTitle() {
        return openAPI.getInfo() != null ? openAPI.getInfo().getTitle() : StringUtils.EMPTY;
    }
}
