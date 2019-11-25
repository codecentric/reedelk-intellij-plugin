package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptControlPanel;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptSelector;
import com.reedelk.plugin.service.module.ScriptService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

public class ScriptPropertyRenderer extends AbstractTypePropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        List<String> scripts = ScriptService.getInstance(module).listAllScripts();

        List<VariableDefinition> variableDefinitions = propertyDescriptor.getVariableDefinitions();
        ScriptContextManager scriptContext = new ScriptContextManager(module, context, variableDefinitions);

        ScriptControlPanel scriptControlPanel = new ScriptControlPanel(module, scriptContext);

        ScriptSelector scriptSelector = new ScriptSelector(scripts);
        scriptSelector.setSelectedItem(propertyAccessor.get());
        scriptControlPanel.onSelect(propertyAccessor.get());
        scriptSelector.addListener(value -> {
            propertyAccessor.set(value);
            scriptControlPanel.onSelect((String) value);
        });

        JPanel container = new DisposablePanel();
        container.setLayout(new BorderLayout());
        container.add(scriptSelector, CENTER);
        container.add(scriptControlPanel, EAST);
        return container;
    }

    /**
    private ConfigMetadata findMatchingScript(List<ConfigMetadata> configsMetadata, String reference) {
        if (StringUtils.isBlank(reference)) return UNSELECTED_CONFIG;
        return configsMetadata.stream()
                .filter(configMetadata -> configMetadata.getId().equals(reference))
                .findFirst()
                .orElseGet(() -> {
                    TypeObjectDescriptor.TypeObject unselectedConfigDefinition = new TypeObjectDescriptor.TypeObject();
                    unselectedConfigDefinition.set(JsonParser.Config.id(), reference);
                    unselectedConfigDefinition.set(JsonParser.Config.title(), String.format("Unresolved (%s)", reference));
                    return new ConfigMetadata(unselectedConfigDefinition);
                });
    }*/
}
