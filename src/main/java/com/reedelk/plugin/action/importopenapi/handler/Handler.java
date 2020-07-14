package com.reedelk.plugin.action.importopenapi.handler;

import com.reedelk.plugin.action.importopenapi.ImporterOpenAPIContext;
import io.swagger.v3.oas.models.PathItem;

public interface Handler {

    boolean isApplicable(PathItem pathEntry);

    void accept(ImporterOpenAPIContext context, String pathEntry, PathItem pathItem);

}
