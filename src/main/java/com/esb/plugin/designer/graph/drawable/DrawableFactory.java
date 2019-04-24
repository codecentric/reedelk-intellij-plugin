package com.esb.plugin.designer.graph.drawable;

import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class DrawableFactory {

    private static final Class DEFAULT = GenericComponentDrawable.class;
    private static final Map<String, Class<? extends Drawable>> COMPONENT_DRAWABLE_MAP = ImmutableMap.of(
            Choice.class.getName(), ChoiceDrawable.class,
            Fork.class.getName(), ForkJoinDrawable.class,
            FlowReference.class.getName(), FlowReferenceDrawable.class);


    public static <T extends Drawable> T get(ComponentDescriptor descriptor) {
        String componentFullyQualifiedName = descriptor.getFullyQualifiedName();
        Class componentDrawableClazz = COMPONENT_DRAWABLE_MAP.getOrDefault(componentFullyQualifiedName, DEFAULT);
        try {
            return (T) componentDrawableClazz.getConstructor(ComponentDescriptor.class).newInstance(descriptor);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
