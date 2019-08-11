package com.reedelk.plugin.graph.deserializer;

public class DeserializationError extends Exception {
    public DeserializationError(Exception e) {
        super(e);
    }
}
