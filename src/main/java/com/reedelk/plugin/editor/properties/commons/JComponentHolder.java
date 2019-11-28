package com.reedelk.plugin.editor.properties.commons;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class JComponentHolder {

    private JComponent component;
    private Map<String, String> metadata = new HashMap<>();

    public JComponentHolder(@NotNull JComponent component) {
        this.component = component;
    }

    public JComponent getComponent() {
        return component;
    }

    public boolean matches(BiPredicate<String, String> metadataPredicate) {
        return metadata.entrySet()
                .stream()
                .anyMatch(entry -> metadataPredicate.test(entry.getKey(), entry.getValue()));
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
}
