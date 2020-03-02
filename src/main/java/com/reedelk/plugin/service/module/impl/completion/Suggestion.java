package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.module.descriptor.model.AutocompleteTypeDescriptor;
import com.reedelk.module.descriptor.model.AutocompleteVariableDescriptor;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;
import com.reedelk.runtime.api.commons.StringUtils;


public class Suggestion {

    private final String type;
    private final String token;
    private final String signature;
    private final String returnType;

    private final boolean isGlobal;
    private final int cursorOffset;
    private AutocompleteItemType itemType;

    public static Suggestion create(AutocompleteTypeDescriptor descriptor) {
        return new Suggestion(
                descriptor.isGlobal(),
                descriptor.getType(),
                descriptor.getType(),
                descriptor.getType(),
                descriptor.getType(),
                0,
                AutocompleteItemType.VARIABLE);
    }

    public static Suggestion create(AutocompleteItemDescriptor descriptor) {
        return new Suggestion(
                false,
                descriptor.getToken() + (descriptor.getItemType() == AutocompleteItemType.FUNCTION ? "()" : StringUtils.EMPTY),
                descriptor.getType(),
                descriptor.getReturnType(),
                descriptor.getSignature(),
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

    private Suggestion(boolean isGlobal, String token, String type, String returnType, String signature, int cursorOffset, AutocompleteItemType itemType) {
        this.type = type;
        this.token = token;
        this.isGlobal = isGlobal;
        this.itemType = itemType;
        this.signature = signature;
        this.returnType = returnType;
        this.cursorOffset = cursorOffset;
    }

    public String getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public String getSignature() {
        return signature;
    }

    public String getReturnType() {
        return returnType;
    }

    public int getCursorOffset() {
        return cursorOffset;
    }

    public AutocompleteItemType getItemType() {
        return itemType;
    }
}
