package com.esb.plugin.javascript;

public enum Type implements TypeDefinition {

    ANY {
        @Override
        public String displayName() {
            return "Any";
        }
    },
    MAP {
        @Override
        public String displayName() {
            return "Map";
        }
    };

}
