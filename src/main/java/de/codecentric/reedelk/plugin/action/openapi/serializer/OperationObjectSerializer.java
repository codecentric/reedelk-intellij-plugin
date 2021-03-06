package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiConstants;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiUtils;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.model.OperationObject;
import de.codecentric.reedelk.openapi.v3.model.SecurityRequirementObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.codecentric.reedelk.openapi.v3.model.OperationObject.Properties;
import static de.codecentric.reedelk.plugin.action.openapi.OpenApiUtils.isNotEmpty;
import static java.util.stream.Collectors.toList;

public class OperationObjectSerializer extends de.codecentric.reedelk.openapi.v3.serializer.OperationObjectSerializer {

    @Override
    public Map<String, Object> serialize(SerializerContext context, NavigationPath navigationPath, OperationObject input) {
        Map<String, Object> serialized = super.serialize(context, navigationPath, input);
        serialized.remove(Properties.SECURITY.value());

        // We need to remap security because in the OpenAPI file it is:
        // "security": [
        //    {},
        //    {
        //      "api_key": [
        //        "write:pets",
        //        "read:pets"
        //      ]
        //    }
        //  ]
        // However the RESTListener structure is the following:
        // "security": [
        //  {},
        //  {
        //    "name": "api_key",
        //    "scopes": [
        //      "write:pets",
        //      "read:pets"
        //    ]
        //  }
        //]
        List<Map<String, SecurityRequirementObject>> security = input.getSecurity();
        if (OpenApiUtils.isNotEmpty(security)) {
            List<Map<String, Object>> mappedSecurity = security.stream().map(stringSecurityRequirementObjectMap -> {
                Map<String, Object> securityRequirementObjectSerialized = new HashMap<>();
                stringSecurityRequirementObjectMap.forEach((requirementName, securityRequirementObject) -> {
                    securityRequirementObjectSerialized.put(OpenApiConstants.PROPERTY_SECURITY_REQUIREMENT_NAME, requirementName);
                    securityRequirementObjectSerialized.put(OpenApiConstants.PROPERTY_SECURITY_REQUIREMENT_SCOPES, securityRequirementObject.getScopes());
                });
                return securityRequirementObjectSerialized;
            }).collect(toList());
            serialized.put(Properties.SECURITY.value(), mappedSecurity);
        }

        return serialized;
    }
}
