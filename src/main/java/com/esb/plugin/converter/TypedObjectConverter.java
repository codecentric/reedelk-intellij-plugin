package com.esb.plugin.converter;

import com.esb.plugin.component.domain.TypeObjectDescriptor;
import org.json.JSONObject;

// TODO: Implement me
public class TypedObjectConverter implements ValueConverter<TypeObjectDescriptor.TypeObject> {

    @Override
    public String toText(Object value) {
        return null;
    }

    @Override
    public TypeObjectDescriptor.TypeObject from(String value) {
        return null;
    }

    @Override
    public TypeObjectDescriptor.TypeObject from(String propertyName, JSONObject object) {
        return null;
    }
}
