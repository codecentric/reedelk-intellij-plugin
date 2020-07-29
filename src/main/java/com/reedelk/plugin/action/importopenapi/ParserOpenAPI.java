package com.reedelk.plugin.action.importopenapi;


import com.reedelk.openapi.OpenApiDeserializer;
import com.reedelk.openapi.v3.OpenApiObject;
import com.reedelk.openapi.v3.OperationObject;
import com.reedelk.openapi.v3.PathsObject;
import com.reedelk.openapi.v3.RestMethod;
import com.reedelk.runtime.api.commons.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;

public class ParserOpenAPI {

    private final String inputFilePath;
    private OpenApiObject openApiObject;

    public ParserOpenAPI(String inputFilePath) {
        // TODO: Check not empty
        this.inputFilePath = inputFilePath;
    }

    public void parse() {
        try {
            String content = org.apache.commons.io.FileUtils.readFileToString(new File(inputFilePath), StandardCharsets.UTF_8);
            openApiObject = OpenApiDeserializer.from(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forEachPath(BiConsumer<String, Map<RestMethod, OperationObject>> processor) {
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach(processor);
    }

    public String getTitle() {
        return openApiObject.getInfo() != null ?
                openApiObject.getInfo().getTitle() : StringUtils.EMPTY;
    }
}
