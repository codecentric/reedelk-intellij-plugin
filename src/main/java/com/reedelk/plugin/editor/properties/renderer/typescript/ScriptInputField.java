package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.commons.PopupUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions.ScriptActionsPanel;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.plugin.service.module.impl.script.ScriptResource;
import com.reedelk.plugin.service.module.impl.script.ScriptServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.topic.ReedelkTopics.TOPIC_SCRIPT_RESOURCE;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

public class ScriptInputField extends DisposablePanel implements ScriptServiceImpl.ScriptResourceChangeListener {

    private static final ScriptResource UNSELECTED_SCRIPT_RESOURCE = new UnselectedScriptResource();

    private final transient Module module;
    private final transient MessageBusConnection connect;
    private final transient PropertyAccessor propertyAccessor;
    private final transient ScriptActionsPanel scriptActionsPanel;
    private final transient ScriptSelectorCombo scriptSelectorCombo;

    ScriptInputField(Module module, PropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, ContainerContext context) {
        this.module = module;
        this.propertyAccessor = propertyAccessor;
        this.scriptActionsPanel = new ScriptActionsPanel(module, context, propertyDescriptor);
        this.scriptSelectorCombo = new ScriptSelectorCombo();

        this.connect = module.getMessageBus().connect();
        this.connect.subscribe(TOPIC_SCRIPT_RESOURCE, this);

        setLayout(new BorderLayout());
        add(scriptSelectorCombo, CENTER);
        add(scriptActionsPanel, EAST);
        ScriptService.getInstance(module).fetchScriptResources();
    }


    @Override
    public void onScriptResources(Collection<ScriptResource> scriptResources) {
        String currentSelectedScriptPath = propertyAccessor.get();
        ScriptResource resourceMatching = findResourceMatching(scriptResources, currentSelectedScriptPath);
        updateWith(scriptResources, resourceMatching);
    }

    @Override
    public void onAddSuccess(ScriptResource resource) {
        propertyAccessor.set(resource.getPath());
        ScriptService.getInstance(module).fetchScriptResources();
    }

    @Override
    public void onRemoveSuccess() {
        // Nothing is selected.
        propertyAccessor.set(EMPTY);
        ScriptService.getInstance(module).fetchScriptResources();
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

    private void updateWith(Collection<ScriptResource> scriptResources, ScriptResource selected) {
        // Prepare model
        List<ScriptResource> updatedResources = new ArrayList<>(scriptResources);
        updatedResources.add(UNSELECTED_SCRIPT_RESOURCE);
        if (!updatedResources.contains(selected)) {
            // The selected resource might be an Unknown script resource object created
            // on the fly by 'findResourceMatching' function.
            updatedResources.add(selected);
        }
        DefaultComboBoxModel<ScriptResource> comboModel = new DefaultComboBoxModel<>();
        updatedResources.forEach(comboModel::addElement);

        SwingUtilities.invokeLater(() -> {
            // We must remove any previous listener.
            scriptSelectorCombo.removeListener();

            // Update the model
            scriptSelectorCombo.setModel(comboModel);
            scriptSelectorCombo.setSelectedItem(selected);
            scriptActionsPanel.onSelect(selected);

            // Add back the listener
            scriptSelectorCombo.addListener(value -> {
                propertyAccessor.set(((ScriptResource) value).getPath());
                scriptActionsPanel.onSelect((ScriptResource) value);
            });
        });
    }

    private ScriptResource findResourceMatching(Collection<ScriptResource> scriptResources, String path) {
        if (isBlank(path)) return UNSELECTED_SCRIPT_RESOURCE;
        return scriptResources.stream()
                .filter(scriptResource -> scriptResource.getPath().equals(path)).findFirst()
                .orElseGet(() -> new NotFoundScriptResource(path));
    }

    public class NotFoundScriptResource extends ScriptResource {

        NotFoundScriptResource(String path) {
            super(path, message("script.not.found"));
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
            super(EMPTY, message("script.not.selected"));
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
