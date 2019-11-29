package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.Messages;
import com.reedelk.plugin.commons.PopupUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions.ScriptActionsPanel;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.plugin.service.module.impl.ScriptResource;
import com.reedelk.plugin.service.module.impl.ScriptServiceImpl;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

import static com.reedelk.plugin.service.module.impl.ScriptServiceImpl.TOPIC_SCRIPT_RESOURCE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

public class ScriptInputField extends DisposablePanel implements ScriptServiceImpl.ScriptResourceChangeListener {

    private static final ScriptResource UNSELECTED_SCRIPT_RESOURCE = new UnselectedScriptResource();


    private transient final Module module;
    private transient final MessageBusConnection connect;
    private transient final PropertyAccessor propertyAccessor;
    private transient final ScriptActionsPanel scriptActionsPanel;
    private transient final ScriptSelectorCombo scriptSelectorCombo;

    ScriptInputField(Module module, PropertyAccessor propertyAccessor) {
        this.module = module;
        this.propertyAccessor = propertyAccessor;
        this.scriptActionsPanel = new ScriptActionsPanel(module);
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
        propertyAccessor.set(StringUtils.EMPTY);
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
        // We must remove any previous listener.
        scriptSelectorCombo.removeListener();

        // Update the model
        DefaultComboBoxModel<ScriptResource> comboModel = new DefaultComboBoxModel<>();
        scriptResources.forEach(comboModel::addElement);
        if (!scriptResources.contains(selected)) {
            comboModel.addElement(selected);
        }
        scriptSelectorCombo.setModel(comboModel);
        scriptSelectorCombo.setSelectedItem(selected);
        scriptActionsPanel.onSelect(selected);

        // Add back the listener
        scriptSelectorCombo.addListener(value -> {
            propertyAccessor.set(((ScriptResource) value).getPath());
            scriptActionsPanel.onSelect((ScriptResource) value);
        });
    }

    private ScriptResource findResourceMatching(Collection<ScriptResource> scriptResources, String path) {
        if (StringUtils.isBlank(path)) return UNSELECTED_SCRIPT_RESOURCE;
        return scriptResources.stream()
                .filter(scriptResource -> scriptResource.getPath().equals(path)).findFirst()
                .orElseGet(() -> new NotFoundScriptResource(path));
    }

    public class NotFoundScriptResource extends ScriptResource {

        NotFoundScriptResource(String path) {
            super(path, Messages.Script.NOT_FOUND.format());
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
            super(StringUtils.EMPTY, Messages.Script.NOT_SELECTED.format());
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
