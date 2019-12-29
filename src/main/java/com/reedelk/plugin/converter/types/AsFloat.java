package com.reedelk.plugin.converter.types;

public class AsFloat extends AbstractNumericValueConverter<Float> {

    @Override
    Class<Float> getClazz() {
        return float.class;
    }
}
