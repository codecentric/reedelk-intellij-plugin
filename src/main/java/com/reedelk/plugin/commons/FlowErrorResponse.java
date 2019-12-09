package com.reedelk.plugin.commons;

import com.reedelk.runtime.api.commons.FlowError;
import org.json.JSONObject;

import java.util.Optional;

public class FlowErrorResponse {

    private FlowErrorResponse() {
    }

    public static Optional<FlowError> from(String httpResponseBody) {
        try {
            JSONObject object = new JSONObject(httpResponseBody);
            long moduleId = object.has(FlowError.Properties.moduleId) ? object.getLong(FlowError.Properties.moduleId) : -1;
            String moduleName = object.has(FlowError.Properties.moduleName) ? object.getString(FlowError.Properties.moduleName) : null;
            String flowId = object.has(FlowError.Properties.flowId) ? object.getString(FlowError.Properties.flowId) : null;
            String flowTitle = object.has(FlowError.Properties.flowTitle) ? object.getString(FlowError.Properties.flowTitle) : null;
            String errorType = object.has(FlowError.Properties.errorType) ? object.getString(FlowError.Properties.errorType) : null;
            String errorMessage = object.has(FlowError.Properties.errorMessage) ? object.getString(FlowError.Properties.errorMessage) : null;
            return Optional.of(new FlowError(moduleId, moduleName, flowId, flowTitle, errorType, errorMessage));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
