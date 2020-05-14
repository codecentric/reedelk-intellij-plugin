package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.commons.Icons;
import com.reedelk.runtime.api.commons.Preconditions;

import javax.swing.*;

public class Suggestion {

    private final Type type;
    private final String tailText;
    private final String lookup;
    private final String lookupDisplayValue;
    private final String returnType;
    private final String returnTypeDisplayValue;
    private final int cursorOffset;

    private Suggestion(Type type,
                       String lookup,
                       String lookupDisplayValue,
                       String returnType,
                       String returnTypeDisplayValue,
                       String tailText,
                       int cursorOffset) {
        this.type = type;
        this.lookup = lookup;
        this.lookupDisplayValue = lookupDisplayValue;
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

    public String getLookup() {
        return lookup;
    }

    public String getLookupDisplayValue() {
        return lookupDisplayValue;
    }

    public String getReturnType() {
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
        private String lookup;
        private String lookupDisplayValue;
        private String returnType;
        private String returnTypeDisplayValue;
        private int cursorOffset;


        public Builder(Type type) {
            this.type = type;
        }

        public Builder tailText(String tailText) {
            this.tailText = tailText;
            return this;
        }

        public Builder lookup(String lookup) {
            this.lookup = lookup;
            return this;
        }

        public Builder lookupDisplayValue(String lookupDisplayValue) {
            this.lookupDisplayValue = lookupDisplayValue;
            return this;
        }

        public Builder returnType(String returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder returnTypeDisplayValue(String returnTypeDisplayValue) {
            this.returnTypeDisplayValue = returnTypeDisplayValue;
            return this;
        }

        public Builder cursorOffset(int cursorOffset) {
            this.cursorOffset = cursorOffset;
            return this;
        }

        public Suggestion build() {
            Preconditions.checkNotNull(lookup, "lookupString");
            if (lookupDisplayValue == null) {
                lookupDisplayValue = lookup;
            }
            return new Suggestion(
                    type,
                    lookup,
                    lookupDisplayValue,
                    returnType,
                    returnTypeDisplayValue,
                    tailText,
                    cursorOffset);
        }
    }
}
