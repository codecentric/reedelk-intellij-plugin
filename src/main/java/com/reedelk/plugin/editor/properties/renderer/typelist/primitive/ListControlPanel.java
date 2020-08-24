package com.reedelk.plugin.editor.properties.renderer.typelist.primitive;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBList;
import com.reedelk.module.descriptor.model.property.ListDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FileChooseInputFieldWithEraseBtn;
import com.reedelk.plugin.editor.properties.commons.StringInputField;
import com.reedelk.runtime.api.annotation.ListInputType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.editor.properties.commons.ClickableLabel.OnClickAction;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Optional.ofNullable;
import static javax.swing.SwingConstants.VERTICAL;

public class ListControlPanel extends DisposablePanel {

    public ListControlPanel(JBList<Object> list,
                            DefaultListModel<Object> model,
                            String inputHint,
                            PropertyDescriptor descriptor,
                            @NotNull Module module) {

        final ListDescriptor propertyType = descriptor.getType();

        JComponent inputField;
        AddActionProvider addActionProvider;

        if (ListInputType.ListInput.FILE.equals(propertyType.getListInput())) {
            HandleInputFile handleInputFile = new HandleInputFile(module, propertyType).invoke();
            addActionProvider = handleInputFile.getAddActionProvider();
            inputField = handleInputFile.getComponent();

        } else {
            HandleInputText handleInputText = new HandleInputText(inputHint).invoke();
            addActionProvider = handleInputText.getAddActionProvider();
            inputField = handleInputText.getComponent();
        }

        ControlsContainer controls = new ControlsContainer(list, model, addActionProvider);
        setLayout(new BorderLayout());
        setBorder(Borders.empty(1, 0));
        add(controls, BorderLayout.WEST);
        add(inputField, BorderLayout.CENTER);
    }

    static class ControlsContainer extends DisposablePanel {

        ControlsContainer(JBList<Object> list, DefaultListModel<Object> model, AddActionProvider provider) {
            ClickableLabel delete = new ClickableLabel(message("properties.type.list.delete"), Remove, Remove);
            delete.setBorder(Borders.emptyRight(4));
            delete.setAction(new RemoveItemListener(list, model, delete));

            ClickableLabel add = new ClickableLabel(message("properties.type.list.add"), Add, Add);
            add.setBorder(Borders.empty(0, 4));

            OnClickAction addAction = provider.provide(list, model, add);
            add.setAction(addAction);

            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            add(delete);
            add(new JSeparator(VERTICAL));
            add(add);
        }
    }

    interface AddActionProvider {
        OnClickAction provide(JBList<Object> list, DefaultListModel<Object> model, ClickableLabel addLabel);
    }

    private static class HandleInputFile {

        private Module module;
        private ListDescriptor propertyType;
        private AddActionProvider addActionProvider;
        private FileChooseInputFieldWithEraseBtn component;

        public HandleInputFile(Module module, ListDescriptor propertyType) {
            this.module = module;
            this.propertyType = propertyType;
        }

        public AddActionProvider getAddActionProvider() {
            return addActionProvider;
        }

        public FileChooseInputFieldWithEraseBtn getComponent() {
            return component;
        }

        public HandleInputFile invoke() {
            String chooseFileHint = ofNullable(propertyType.getHintBrowseFile())
                    .orElse(message("properties.type.resource.choose.file.hint"));

            String chooseFileDialogTitle = ofNullable(propertyType.getHintBrowseFile())
                    .orElse(message("properties.type.resource.choose.file.dialog"));

            FileChooseInputFieldWithEraseBtn.PropertyAccessorInMemory propertyAccessorInMemory =
                    new FileChooseInputFieldWithEraseBtn.PropertyAccessorInMemory();
            String chooseFileRootDirectory = PluginModuleUtils.getResourcesDirectory(module)
                    .orElseThrow(() -> new IllegalStateException(message("error.resource.dir.not.found")));

            component = new FileChooseInputFieldWithEraseBtn(
                    module.getProject(),
                    chooseFileDialogTitle,
                    chooseFileHint,
                    chooseFileRootDirectory,
                    propertyAccessorInMemory);

            addActionProvider =
            (theList, theModel, theLabel) ->
                    new ItemListenerFileChoose(theList, theModel, theLabel, component, propertyAccessorInMemory);
            return this;
        }
    }

    private static class HandleInputText {
        private String inputHint;
        private AddActionProvider addActionProvider;
        private StringInputField component;

        public HandleInputText(String inputHint) {
            this.inputHint = inputHint;
        }

        public AddActionProvider getAddActionProvider() {
            return addActionProvider;
        }

        public StringInputField getComponent() {
            return component;
        }

        public HandleInputText invoke() {
            component = new StringInputField(inputHint);
            addActionProvider = (theList, theModel, addLabel) ->
                    new ItemListenerTextField(theList, theModel, addLabel, component);
            return this;
        }
    }
}
