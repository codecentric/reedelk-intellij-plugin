package com.esb.plugin.component;


public class EnumTypeDescriptor implements PropertyTypeDescriptor {

    public EnumTypeDescriptor() {

    }

    @Override
    public Class<?> type() {
        return Enum.class;
    }
}
