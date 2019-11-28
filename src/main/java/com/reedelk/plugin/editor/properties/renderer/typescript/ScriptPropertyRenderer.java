package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions.ScriptActionsPanel;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.plugin.service.module.impl.ScriptResource;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

public class ScriptPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        List<ScriptResource> scripts = ScriptService.getInstance(module).getScripts();

        ScriptActionsPanel scriptActionsPanel = new ScriptActionsPanel(module);
        scriptActionsPanel.onSelect(propertyAccessor.get()); // we set the current selected script.

        ScriptSelectorCombo scriptSelectorCombo = new ScriptSelectorCombo(scripts);
        scriptSelectorCombo.setSelectedItem(propertyAccessor.get());
        scriptSelectorCombo.addListener(value -> {
            propertyAccessor.set(value);
            scriptActionsPanel.onSelect((String) value);
        });

        JPanel container = new DisposablePanel();
        container.setLayout(new BorderLayout());
        container.add(scriptSelectorCombo, CENTER);
        container.add(scriptActionsPanel, EAST);
        return container;
    }

    private ScriptResource findResourceMatching(List<ScriptResource> scriptResources, String path) {
        if (StringUtils.isBlank(path)) return ScriptSelectorCombo.UNSELECTED;
        return scriptResources.stream()
                .filter(new Predicate<ScriptResource>() {
                    @Override
                    public boolean test(ScriptResource scriptResource) {
                        return scriptResource.getPath().equals(path);
                    }
                }).findFirst()
                .orElseGet(new Supplier<ScriptResource>() {
                    @Override
                    public ScriptResource get() {
                        ScriptResource unknownResource = new ScriptResource(path);
                        return unknownResource;
                    }
                });
    }
}
