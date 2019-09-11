package com.reedelk.plugin.component.domain;

import static java.util.Objects.requireNonNull;

public class TypeComboDescriptor implements TypeDescriptor {

    private final boolean editable;
    private final String[] comboValues;

    public TypeComboDescriptor(boolean editable, String[] comboValues) {
        this.comboValues = requireNonNull(comboValues, "combo values");
        this.editable = editable;
    }

    @Override
    public Class<?> type() {
        return TypeCombo.class;
    }

    @Override
    public Object defaultValue() {
        return null;
    }

    public boolean isEditable() {
        return editable;
    }

    public String[] getComboValues() {
        return comboValues;
    }

    public static class TypeCombo {

    }
}
