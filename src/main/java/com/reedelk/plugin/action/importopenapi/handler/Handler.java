package com.reedelk.plugin.action.importopenapi.handler;

import com.intellij.openapi.project.Project;
import io.swagger.v3.oas.models.PathItem;

public interface Handler {

    boolean isApplicable(PathItem pathItem);

    void accept(Project project, String pathEntry, PathItem pathItem);

}
