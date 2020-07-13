package com.reedelk.plugin.action.importopenapi.handler;

import com.intellij.openapi.project.Project;
import io.swagger.v3.oas.models.PathItem;

public class GETHandler implements Handler {


    @Override
    public boolean isApplicable(PathItem pathItem) {
        return pathItem.getGet() != null;
    }

    @Override
    public void accept(Project project, String pathEntry, PathItem pathItem) {

    }
}
