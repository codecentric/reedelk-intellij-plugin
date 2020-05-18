package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.service.module.impl.component.metadata.TypeProxy;

import javax.swing.*;

import static com.reedelk.runtime.api.commons.Preconditions.checkNotNull;

public class Suggestion {

    private final Type type;
    // the tail text shown next to the lookup token (e.g: function arguments)
    private final String tailText;
    // the value inserted in the editor when the user presses enter
    private final String insertValue;
    // the value shown in the suggestion popup
    private final String lookupToken;
    // the type proxy of the return type
    private final TypeProxy returnType;
    // the display value to be shown in the suggestion popup of the return value
    private final String returnTypeDisplayValue;
    // for functions taking arguments, the cursor could be placed between ( and ) by setting an offset to the cursor.
    private final int cursorOffset;

    private Suggestion(Type type,
                       String insertValue,
                       String lookupToken,
                       TypeProxy returnType,
                       String returnTypeDisplayValue,
                       String tailText,
                       int cursorOffset) {
        this.type = type;
        this.insertValue = insertValue;
        this.lookupToken = lookupToken;
        this.returnType = returnType;
        this.returnTypeDisplayValue = returnTypeDisplayValue;
        this.cursorOffset = cursorOffset;
        this.tailText = tailText;
    }

    public Type getType() {
        return type;
    }

    public String getTailText() {
        return tailText;
    }

    public String getInsertValue() {
        return insertValue;
    }

    public String getLookupToken() {
        return lookupToken;
    }

    public TypeProxy getReturnType() {
        return returnType;
    }

    public String getReturnTypeDisplayValue() {
        return returnTypeDisplayValue;
    }

    public int getCursorOffset() {
        return cursorOffset;
    }

    public enum Type {

        FUNCTION(Icons.Script.Function),
        PROPERTY(Icons.Script.Property),
        GLOBAL(Icons.Script.Global);

        public final Icon icon;

        Type(Icon icon) {
            this.icon = icon;
        }
    }

    public static Builder create(Type type) {
        return new Builder(type);
    }

    public static class Builder {

        private Type type;
        private String tailText;
        private String insertValue;
        private String lookupToken;
        private TypeProxy returnType;
        private String returnTypeDisplayValue;
        private int cursorOffset;


        public Builder(Type type) {
            this.type = type;
        }

        public Builder tailText(String tailText) {
            this.tailText = tailText;
            return this;
        }

        public Builder returnType(TypeProxy returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder insertValue(String insertValue) {
            this.insertValue = insertValue;
            return this;
        }

        public Builder cursorOffset(int cursorOffset) {
            this.cursorOffset = cursorOffset;
            return this;
        }

        public Builder lookupToken(String lookupToken) {
            this.lookupToken = lookupToken;
            return this;
        }

        public Builder returnTypeDisplayValue(String returnTypeDisplayValue) {
            this.returnTypeDisplayValue = returnTypeDisplayValue;
            return this;
        }

        public Suggestion build() {
            checkNotNull(type, "type");
            checkNotNull(returnType, "returnType (" + insertValue + ")");
            checkNotNull(insertValue, "insertValue");
            if (lookupToken == null) lookupToken = insertValue;
            return new Suggestion(type, insertValue, lookupToken, returnType, returnTypeDisplayValue, tailText, cursorOffset);
        }
    }
}
