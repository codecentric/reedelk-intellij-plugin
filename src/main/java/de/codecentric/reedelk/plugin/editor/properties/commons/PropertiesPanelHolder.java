package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import de.codecentric.reedelk.plugin.commons.DisposableUtils;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.PropertyTypeRendererFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.empty;

public class PropertiesPanelHolder extends DisposablePanel {

    private static final Border PANEL_BORDERS = empty(8, 0, 0, 10);

    private final transient Module module;
    private final transient ComponentDataHolder dataHolder;
    private final transient List<PropertyDescriptor> descriptors = new ArrayList<>();

    private transient ContainerContext context;

    public PropertiesPanelHolder(@NotNull Module module,
                                 @NotNull ContainerContext context,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @NotNull List<PropertyDescriptor> descriptors) {
        this.module = module;
        this.context = context;
        this.dataHolder = dataHolder;
        this.descriptors.addAll(descriptors);

        setLayout(new GridBagLayout());
        setBorder(PANEL_BORDERS);
        initAccessors();
        renderProperties();
    }

    private void initAccessors() {
        // We must init accessors so that visibility can be applied during rendering.
        descriptors.forEach(descriptor -> {

            String propertyName = descriptor.getName();

            PropertyTypeDescriptor propertyType = descriptor.getType();

            context.propertyAccessorOf(propertyName, propertyType, dataHolder);
        });
    }

    private void renderProperties() {

        descriptors.forEach(descriptor -> {

            String propertyName = descriptor.getName();

            PropertyTypeDescriptor propertyType = descriptor.getType();

            PropertyTypeRenderer renderer = PropertyTypeRendererFactory.get().from(propertyType);

            PropertyAccessor propertyAccessor = context.propertyAccessorOf(propertyName, propertyType, dataHolder);

            PropertyTypeRenderer.RenderedComponent renderedComponent = renderer.render(module, descriptor, propertyAccessor, context);

            renderer.addToParent(this, renderedComponent, descriptor, context);

        });
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(context);
        this.context = null;
    }
}
