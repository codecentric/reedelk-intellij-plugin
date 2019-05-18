package com.esb.plugin.converter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class IntegerConverter implements ValueConverter<Integer> {

    @Override
    public String toText(Object value) {
        Integer realValue = (Integer) value;
        return realValue == null ?
                StringUtils.EMPTY :
                Integer.toString(realValue);
    }

    @Override
    public Integer from(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public Integer from(String key, JSONObject object) {
        return object.getInt(key);
    }

}
