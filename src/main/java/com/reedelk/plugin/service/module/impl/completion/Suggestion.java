package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.module.descriptor.model.AutocompleteVariableDescriptor;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;


public class Suggestion implements TypeAware {

    private final String token;
    private final String type;
    private final String returnType;
    private final String replaceValue;

    private final boolean isGlobal;
    private final int cursorOffset;
    private AutocompleteItemType itemType;

    public static Suggestion create(AutocompleteItemDescriptor descriptor) {
        return new Suggestion(
                descriptor.isGlobal(),
                descriptor.getToken(),
                descriptor.getType(),
                descriptor.getReturnType(),
                descriptor.getReplaceValue(),
                descriptor.getCursorOffset(),
                descriptor.getItemType());
    }

    public static Suggestion create(AutocompleteVariableDescriptor descriptor) {
        return new Suggestion(
                true,
                descriptor.getName(),
                descriptor.getType(),
                descriptor.getType(),
                descriptor.getName(),
                0,
                AutocompleteItemType.VARIABLE);
    }

    private Suggestion(boolean isGlobal, String token, String type, String returnType, String replaceValue, int cursorOffset, AutocompleteItemType itemType) {
        this.type = type;
        this.token = token;
        this.isGlobal = isGlobal;
        this.itemType = itemType;
        this.returnType = returnType;
        this.replaceValue = replaceValue;
        this.cursorOffset = cursorOffset;
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }

    public String getReplaceValue() {
        return replaceValue;
    }

    public int getCursorOffset() {
        return cursorOffset;
    }

    public AutocompleteItemType getItemType() {
        return itemType;
    }
}
