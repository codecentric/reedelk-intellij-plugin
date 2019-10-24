package com.reedelk.plugin.component.domain;

import static java.util.Objects.requireNonNull;

public class TypeComboDescriptor implements TypeDescriptor {

    private final boolean editable;
    private final String prototype;
    private final String[] comboValues;

    public TypeComboDescriptor(boolean editable, String[] comboValues) {
        this(editable, comboValues, null);
    }

    public TypeComboDescriptor(boolean editable, String[] comboValues, String prototype) {
        this.comboValues = requireNonNull(comboValues, "combo values");
        this.prototype = prototype;
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

    public String getPrototype() {
        return prototype;
    }

    public String[] getComboValues() {
        return comboValues;
    }

    public interface TypeCombo {
    }
}
