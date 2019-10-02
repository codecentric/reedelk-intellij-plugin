package com.reedelk.plugin.assertion.component;

import com.reedelk.plugin.component.domain.*;
import com.reedelk.runtime.api.script.dynamicmap.DynamicMap;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicValue;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class TypeDescriptorMatchers {

    public interface TypeDescriptorMatcher {
        boolean matches(TypeDescriptor given);
    }

    public static TypeDescriptorMatcher ofPrimitiveType(TypePrimitiveDescriptor expected) {
        return given -> {
            if (given instanceof TypePrimitiveDescriptor) {
                TypePrimitiveDescriptor actual = (TypePrimitiveDescriptor) given;
                return same(expected, actual);
            }
            return false;
        };
    }

    public static TypeDescriptorMatcher ofTypeEnum(TypeEnumDescriptor expected) {
        return given -> {
            if (given instanceof TypeEnumDescriptor) {
                TypeEnumDescriptor actual = (TypeEnumDescriptor) given;
                Map<String, String> expectedValueAndDisplayMap = expected.valueAndDisplayMap();
                Map<String, String> actualValueAndDisplayMap = actual.valueAndDisplayMap();
                return same(expected, actual) &&
                        expectedValueAndDisplayMap.equals(actualValueAndDisplayMap);
            }
            return false;
        };
    }

    public static TypeDescriptorMatcher ofTypeFile(TypeFileDescriptor expected) {
        return given -> {
            if (given instanceof TypeFileDescriptor) {
                TypeFileDescriptor actual = (TypeFileDescriptor) given;
                return same(expected, actual);
            }
            return false;
        };
    }

    public static TypeDescriptorMatcher ofTypeCombo(TypeComboDescriptor expected) {
        return given -> {
            if (given instanceof TypeComboDescriptor) {
                TypeComboDescriptor actual = (TypeComboDescriptor) given;
                boolean expectedEditable = expected.isEditable();
                boolean actualEditable = actual.isEditable();
                String[] expectedComboValues = expected.getComboValues();
                String[] actualComboValues = actual.getComboValues();
                return same(expected, actual) &&
                        expectedEditable == actualEditable &&
                        Arrays.equals(expectedComboValues, actualComboValues);
            }
            return false;
        };
    }

    public static TypeDescriptorMatcher ofTypeMap(TypeMapDescriptor expected) {
        return given -> {
            if (given instanceof TypeMapDescriptor) {
                TypeMapDescriptor actual = (TypeMapDescriptor) given;
                String expectedTabGroup = expected.getTabGroup().get();
                String actualTabGroup = actual.getTabGroup().get();
                return same(expected, actual) && expectedTabGroup.equals(actualTabGroup);
            }
            return false;
        };
    }

    public static TypeDescriptorMatcher ofTypeScript(TypeScriptDescriptor expected) {
        return given -> {
            if (given instanceof TypeScriptDescriptor) {
                TypeScriptDescriptor actual = (TypeScriptDescriptor) given;
                return same(expected, actual);
            }
            return false;
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends DynamicValue<?>> TypeDescriptorMatcher ofDynamicType(TypeDynamicValueDescriptor<T> expected) {
        return given -> {
            if (given instanceof TypeDynamicValueDescriptor<?>) {
                TypeDynamicValueDescriptor<T> actual = (TypeDynamicValueDescriptor<T>) given;
                return same(expected, actual);
            }
            return false;
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends DynamicMap<?>> TypeDescriptorMatcher ofDynamicMapType(TypeDynamicMapDescriptor<T> expected) {
        return given -> {
            if (given instanceof TypeDynamicMapDescriptor<?>) {
                TypeDynamicMapDescriptor<T> actual = (TypeDynamicMapDescriptor<T>) given;
                String expectedTabGroup = expected.getTabGroup().get();
                String actualTabGroup = actual.getTabGroup().get();
                return same(expected, actual) && expectedTabGroup.equals(actualTabGroup);
            }
            return false;
        };
    }

    private static boolean same(TypeDescriptor expected, TypeDescriptor actual) {
        Class<?> expectedClazzType = expected.type();
        Class<?> actualClazzType = actual.type();
        Object expectedDefaultValue = expected.defaultValue();
        Object actualDefaultValue = actual.defaultValue();
        return expectedClazzType.equals(actualClazzType) && same(expectedDefaultValue, actualDefaultValue);
    }

    private static boolean same(Object expected, Object actual) {
        return Objects.equals(expected, actual);
    }
}
