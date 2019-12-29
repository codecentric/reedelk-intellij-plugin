package com.reedelk.plugin.converter.types;

public class AsFloatObject extends AbstractNumericValueConverter<Float> {

    @Override
    Class<Float> getClazz() {
        return Float.class;
    }
}
