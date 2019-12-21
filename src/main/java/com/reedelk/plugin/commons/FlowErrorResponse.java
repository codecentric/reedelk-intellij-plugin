package com.reedelk.plugin.commons;

import com.reedelk.runtime.api.commons.FlowError;
import org.json.JSONObject;

import java.util.Optional;

import static com.reedelk.runtime.api.commons.FlowError.Properties;

public class FlowErrorResponse {

    private FlowErrorResponse() {
    }

    public static Optional<FlowError> from(String httpResponseBody) {
        try {
            JSONObject object = new JSONObject(httpResponseBody);
            long moduleId = object.has(Properties.moduleId) ? object.getLong(Properties.moduleId) : -1;
            String moduleName = object.has(Properties.moduleName) ? object.getString(Properties.moduleName) : null;
            String flowId = object.has(Properties.flowId) ? object.getString(Properties.flowId) : null;
            String flowTitle = object.has(Properties.flowTitle) ? object.getString(Properties.flowTitle) : null;
            String correlationId = object.has(Properties.correlationId) ? object.getString(Properties.correlationId) : null;
            String errorType = object.has(Properties.errorType) ? object.getString(Properties.errorType) : null;
            String errorMessage = object.has(Properties.errorMessage) ? object.getString(Properties.errorMessage) : null;
            return Optional.of(new FlowError(moduleId, moduleName, flowId, flowTitle, correlationId, errorType, errorMessage));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
