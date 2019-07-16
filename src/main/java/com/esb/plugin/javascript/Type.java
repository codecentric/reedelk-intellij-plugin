package com.esb.plugin.javascript;

public enum Type implements TypeDefinition {

    OBJECT {
        @Override
        public String displayName() {
            return "Object";
        }
    },
    MAP {
        @Override
        public String displayName() {
            return "Map";
        }
    };

}
