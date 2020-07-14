package com.reedelk.plugin.action.importopenapi.handler;

import com.reedelk.plugin.action.importopenapi.ImporterOpenAPIContext;
import io.swagger.v3.oas.models.PathItem;

public class GETHandler implements Handler {


    @Override
    public boolean isApplicable(PathItem pathItem) {
        return pathItem.getGet() != null;
    }

    @Override
    public void accept(ImporterOpenAPIContext context, String pathEntry, PathItem pathItem) {

    }
}
