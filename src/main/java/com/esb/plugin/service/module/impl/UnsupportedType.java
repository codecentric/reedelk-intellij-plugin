package com.esb.plugin.service.module.impl;

public class UnsupportedType extends RuntimeException {

    public UnsupportedType(String unsupportedTypeQualifiedName) {
        super(String.format("PropertyTypeDescriptor could not be found for type %s.", unsupportedTypeQualifiedName));
    }

    public UnsupportedType(Class<?> unsupportedTypeClazz) {
        this(unsupportedTypeClazz.getName());
    }
}
