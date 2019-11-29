package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.PopupUtils;
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
import java.util.Collection;

import static com.reedelk.plugin.commons.Messages.Script;
import static com.reedelk.plugin.service.module.impl.ScriptServiceImpl.ScriptResourceChangeListener;
import static com.reedelk.plugin.service.module.impl.ScriptServiceImpl.TOPIC_SCRIPT_RESOURCE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

public class ScriptPropertyRenderer extends AbstractPropertyTypeRenderer {

    private static final ScriptResource UNSELECTED_SCRIPT_RESOURCE = new UnselectedScriptResource();

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        ScriptInput panel = new ScriptInput(module, propertyAccessor);
        ScriptService.getInstance(module).fetchScriptResources();
        return panel;
    }

    class ScriptInput extends DisposablePanel implements ScriptResourceChangeListener {

        private final MessageBusConnection connect;
        private final PropertyAccessor propertyAccessor;
        private final ScriptActionsPanel scriptActionsPanel;
        private final ScriptSelectorCombo scriptSelectorCombo;

        ScriptInput(Module module, PropertyAccessor propertyAccessor) {
            this.propertyAccessor = propertyAccessor;
            this.scriptActionsPanel = new ScriptActionsPanel(module);
            this.scriptSelectorCombo = new ScriptSelectorCombo();

            this.connect = module.getMessageBus().connect();
            this.connect.subscribe(TOPIC_SCRIPT_RESOURCE, this);

            setLayout(new BorderLayout());
            add(scriptSelectorCombo, CENTER);
            add(scriptActionsPanel, EAST);
        }

        @Override
        public void onScriptResources(Collection<ScriptResource> scriptResources) {
            DefaultComboBoxModel<ScriptResource> comboModel = new DefaultComboBoxModel<>();
            comboModel.addElement(UNSELECTED_SCRIPT_RESOURCE);
            scriptResources.forEach(comboModel::addElement);
            scriptSelectorCombo.setModel(comboModel);

            ScriptResource resourceMatching = findResourceMatching(scriptResources, propertyAccessor.get());
            if (!scriptResources.contains(resourceMatching)) {
                scriptResources.add(resourceMatching);
            }
            scriptActionsPanel.onSelect(resourceMatching);
            scriptSelectorCombo.setSelectedItem(resourceMatching);

            scriptSelectorCombo.addListener(value -> {
                propertyAccessor.set(((ScriptResource) value).getPath());
                scriptActionsPanel.onSelect((ScriptResource) value);
            });
        }

        @Override
        public void onAddError(Exception exception) {
            PopupUtils.error(exception, scriptActionsPanel);
        }

        @Override
        public void onRemoveError(Exception exception) {
            PopupUtils.error(exception, scriptActionsPanel);
        }

        @Override
        public void dispose() {
            super.dispose();
            connect.disconnect();
        }
    }

    private ScriptResource findResourceMatching(Collection<ScriptResource> scriptResources, String path) {
        if (StringUtils.isBlank(path)) return UNSELECTED_SCRIPT_RESOURCE;
        return scriptResources.stream()
                .filter(scriptResource -> scriptResource.getPath().equals(path)).findFirst()
                .orElseGet(() -> new NotFoundScriptResource(path));
    }

    public class NotFoundScriptResource extends ScriptResource {

        NotFoundScriptResource(String path) {
            super(path, Script.NOT_FOUND.format());
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isRemovable() {
            return false;
        }
    }

    static class UnselectedScriptResource extends ScriptResource {

        UnselectedScriptResource() {
            super(StringUtils.EMPTY, Script.NOT_SELECTED.format());
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public boolean isRemovable() {
            return false;
        }
    }
}
