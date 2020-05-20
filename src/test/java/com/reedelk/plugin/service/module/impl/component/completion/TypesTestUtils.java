package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.io.Serializable;
import java.util.*;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createFunctionSuggestion;
import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createPropertySuggestion;

public class TypesTestUtils {

    public static List<TrieProvider> TEST_TYPES = Arrays.asList(
      new GenericMapTypeFunctions(),
            new ListMapFirstType(),
            new ListMyItemType(),
            new ListMyUnknownType(),
            new MapFirstType(),
            new MapSecondType(),
            new MessageAttributeType(),
            new MyAttributeType(),
            new MyItemType(),
            new MessageType());

    public interface TrieProvider {
        void register(Map<String, Trie> trieMap);
    }

    public static class GenericMapTypeFunctions implements TrieProvider {

        private GenericMapTypeFunctions() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            TypeDefault.Types.register(trieMap);
         }
    }

    public static class ListMapFirstType extends ArrayList<MapFirstType> implements TrieProvider {

        private ListMapFirstType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieList(ListMapFirstType.class.getName(), MapFirstType.class.getName());
            trieMap.put(ListMapFirstType.class.getName(), trie);
        }
    }

    public static class ListMyItemType extends ArrayList<MyItemType> implements TrieProvider {

        private ListMyItemType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieList(ListMyItemType.class.getName(), MyItemType.class.getName());
            trieMap.put(ListMyItemType.class.getName(), trie);
        }
    }

    public static class ListMyUnknownType extends ArrayList<MyUnknownType> implements TrieProvider {

        private ListMyUnknownType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieList(ListMyUnknownType.class.getName(), MyUnknownType.class.getName());
            trieMap.put(ListMyUnknownType.class.getName(), trie);
        }
    }


    public static class MapFirstType extends HashMap<String, Serializable> implements TrieProvider {

        private MapFirstType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieMap(MapFirstType.class.getName(), MapFirstType.class.getSimpleName(), String.class.getName(), Serializable.class.getName());
            trie.insert(createPropertySuggestion("firstProperty1", String.class.getName()));
            trie.insert(createPropertySuggestion("firstProperty2", String.class.getName()));
            trieMap.put(MapFirstType.class.getName(), trie);
        }
    }

    public static class MapSecondType extends HashMap<String, Serializable> implements TrieProvider {

        private MapSecondType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(MapSecondType.class.getName(), String.class.getName(), Serializable.class.getName());
            trie.insert(createPropertySuggestion("secondProperty1", String.class.getName()));
            trie.insert(createPropertySuggestion("secondProperty2", long.class.getName()));
            trieMap.put(MapSecondType.class.getName(), trie);
        }
    }

    public static class MessageAttributeType implements TrieProvider {

        private MessageAttributeType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault();
            trie.insert(createPropertySuggestion("component", String.class.getName()));
            trieMap.put(MessageAttributes.class.getName(), trie);
        }
    }

    public static class MyAttributeType extends MessageAttributes implements TrieProvider {

        private MyAttributeType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault(MyAttributeType.class.getName(), MessageAttributes.class.getName(), MyAttributeType.class.getSimpleName());
            trie.insert(createPropertySuggestion("attributeProperty1", String.class.getName()));
            trie.insert(createPropertySuggestion("attributeProperty2", long.class.getName()));
            trieMap.put(MyAttributeType.class.getName(), trie);
        }
    }

    public static class MyItemType implements TrieProvider {

        private MyItemType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault();
            trie.insert(createFunctionSuggestion("method1", String.class.getName()));
            trieMap.put(MyItemType.class.getName(), trie);
        }
    }

    public static class MyUnknownType {
    }

    public static class MessageType implements TrieProvider {

        private MessageType() {
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieDefault();
            trie.insert(createFunctionSuggestion("payload", MessagePayload.class.getName()));
            trie.insert(createFunctionSuggestion("attributes", MessageAttributes.class.getName()));
            trieMap.put(Message.class.getName(), trie);
        }
    }
}
