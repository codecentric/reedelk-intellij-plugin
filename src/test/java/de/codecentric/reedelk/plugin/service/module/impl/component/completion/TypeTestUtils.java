package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.runtime.api.message.MessagePayload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class TypeTestUtils {

    public static List<TrieProvider> ALL_TYPES =
            asList(new ListMyTypeWithMethodsAndProperties(),
                    new MyTypeWithMethodsAndProperties(),
                    new CustomMessageAttributeType2(),
                    new CustomMessageAttributeType1(),
                    new GenericMapTypeFunctions(),
                    new MessageAttributeType(),
                    new ListMyUnknownType(),
                    new ListMapFirstType(),
                    new MyAttributeType(),
                    new MySecondAttributeType(),
                    new ListSimpleType(),
                    new ListMyItemType(),
                    new MapSecondType(),
                    new MapFirstType(),
                    new MessageType(),
                    new MyItemType());

    public interface TrieProvider {
        void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap);
    }

    public static class GenericMapTypeFunctions implements TrieProvider {

        private GenericMapTypeFunctions() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            TypeBuiltIn.BUILT_IN_TYPES.forEach(builtInType -> builtInType.register(trieMap, typeAndTries));
        }
    }

    public static class ListMapFirstType extends ArrayList<MapFirstType> implements TrieProvider {

        private ListMapFirstType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieList(ListMapFirstType.class.getName(), List.class.getName(), null, MapFirstType.class.getName());
            trieMap.put(ListMapFirstType.class.getName(), trie);
        }
    }

    public static class ListSimpleType extends ArrayList<String> implements TrieProvider {

        private ListSimpleType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieList(ListSimpleType.class.getName(), List.class.getName(), null, String.class.getName());
            trieMap.put(ListSimpleType.class.getName(), trie);
        }
    }

    public static class ListMyItemType extends ArrayList<MyItemType> implements TrieProvider {

        private ListMyItemType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieList(ListMyItemType.class.getName(), List.class.getName(), null, MyItemType.class.getName());
            trieMap.put(ListMyItemType.class.getName(), trie);
        }
    }

    public static class ListMyUnknownType extends ArrayList<MyUnknownType> implements TrieProvider {

        private ListMyUnknownType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieList(ListMyUnknownType.class.getName(), List.class.getName(), null, MyUnknownType.class.getName());
            trieMap.put(ListMyUnknownType.class.getName(), trie);
        }
    }


    public static class MapFirstType extends HashMap<String, Serializable> implements TrieProvider {

        private MapFirstType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieMap(MapFirstType.class.getName(), Map.class.getName(), MapFirstType.class.getSimpleName(), String.class.getName(), Serializable.class.getName());
            trie.insert(SuggestionTestUtils.createPropertySuggestion("firstProperty1", String.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("firstProperty2", String.class.getName(), typeAndTries));
            trieMap.put(MapFirstType.class.getName(), trie);
        }
    }

    public static class MapSecondType extends HashMap<String, Serializable> implements TrieProvider {

        private MapSecondType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieMap(MapSecondType.class.getName(), Map.class.getName(), null, String.class.getName(), Serializable.class.getName());
            trie.insert(SuggestionTestUtils.createPropertySuggestion("secondProperty1", String.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("secondProperty2", long.class.getName(), typeAndTries));
            trieMap.put(MapSecondType.class.getName(), trie);
        }
    }

    public static class MyAttributeType extends MessageAttributes implements TrieProvider {

        private MyAttributeType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(MyAttributeType.class.getName(), MessageAttributes.class.getName(), MyAttributeType.class.getSimpleName());
            trie.insert(SuggestionTestUtils.createPropertySuggestion("attributeProperty1", String.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("attributeProperty2", long.class.getName(), typeAndTries));
            trieMap.put(MyAttributeType.class.getName(), trie);
        }
    }

    public static class MySecondAttributeType extends MessageAttributes implements TrieProvider {

        private MySecondAttributeType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(MySecondAttributeType.class.getName(), MessageAttributes.class.getName(), MyAttributeType.class.getSimpleName());
            trie.insert(SuggestionTestUtils.createPropertySuggestion("secondAttributeProperty1", String.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("secondAttributeProperty2", long.class.getName(), typeAndTries));
            trieMap.put(MySecondAttributeType.class.getName(), trie);
        }
    }

    public static class MyItemType implements TrieProvider {

        private MyItemType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Suggestion method1 = SuggestionTestUtils.createFunctionSuggestion("method1", String.class.getName(), typeAndTries);
            Trie trie = new TrieDefault(MyItemType.class.getName());
            trie.insert(method1);
            trieMap.put(MyItemType.class.getName(), trie);
        }
    }

    public static class MyTypeWithMethodsAndProperties implements TrieProvider {

        private MyTypeWithMethodsAndProperties() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(MyTypeWithMethodsAndProperties.class.getName(), Object.class.getName(), MyTypeWithMethodsAndProperties.class.getSimpleName());
            trie.insert(SuggestionTestUtils.createFunctionSuggestion("method1", String.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("property1", String.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("property2", long.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("property3", Double.class.getName(), typeAndTries));
            trieMap.put(MyTypeWithMethodsAndProperties.class.getName(), trie);
        }
    }

    public static class ListMyTypeWithMethodsAndProperties implements TrieProvider {

        private ListMyTypeWithMethodsAndProperties() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieList(ListMyTypeWithMethodsAndProperties.class.getName(), Object.class.getName(), null, MyTypeWithMethodsAndProperties.class.getName());
            trieMap.put(ListMyTypeWithMethodsAndProperties.class.getName(), trie);
        }
    }

    public static class MyUnknownType {
    }

    public static class MessageAttributeType implements TrieProvider {

        private MessageAttributeType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(MessageAttributes.class.getName());
            trie.insert(SuggestionTestUtils.createPropertySuggestion("component", String.class.getName(), typeAndTries));
            trieMap.put(MessageAttributes.class.getName(), trie);
        }
    }

    public static class CustomMessageAttributeType1 implements TrieProvider {

        private CustomMessageAttributeType1() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(CustomMessageAttributeType1.class.getName(), MessageAttributes.class.getName(), null);
            trie.insert(SuggestionTestUtils.createPropertySuggestion("attributeProperty1", String.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("attributeProperty2", long.class.getName(), typeAndTries));
            trieMap.put(CustomMessageAttributeType1.class.getName(), trie);
        }
    }

    public static class CustomMessageAttributeType2 implements TrieProvider {

        private CustomMessageAttributeType2() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(CustomMessageAttributeType2.class.getName(), MessageAttributes.class.getName(), null);
            trie.insert(SuggestionTestUtils.createPropertySuggestion("attributeProperty1", int.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("attributeProperty2", long.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createPropertySuggestion("anotherAttributeProperty3", long.class.getName(), typeAndTries));
            trieMap.put(CustomMessageAttributeType2.class.getName(), trie);
        }
    }

    public static class MessageType implements TrieProvider {

        private MessageType() {
        }

        @Override
        public void register(TypeAndTries typeAndTries, Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(Message.class.getName());
            trie.insert(SuggestionTestUtils.createFunctionSuggestion("payload", MessagePayload.class.getName(), typeAndTries));
            trie.insert(SuggestionTestUtils.createFunctionSuggestion("attributes", MessageAttributes.class.getName(), typeAndTries));
            trieMap.put(Message.class.getName(), trie);
        }
    }
}
