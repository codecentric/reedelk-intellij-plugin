package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createFunctionSuggestion;
import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createPropertySuggestion;

public class TypesTestUtils {

    public interface TrieProvider {
        void register(Map<String, Trie> trieMap);
    }

    public static class GenericMapTypeFunctions implements TrieProvider {

        private GenericMapTypeFunctions() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new GenericMapTypeFunctions().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl();
            trie.insert(createFunctionSuggestion("size", int.class.getName()));
            trie.insert(createFunctionSuggestion("each", Void.class.getName()));
            trie.insert(createFunctionSuggestion("eachWithIndex", Void.class.getName()));
            trie.insert(createFunctionSuggestion("find", Object.class.getName()));
            trieMap.put(Map.class.getName(), trie);
            // Hashmap for objects extending from it.
            trieMap.put(HashMap.class.getName(), new TrieImpl(Map.class.getName(), null, null, null, null));
        }
    }

    public static class ListMapFirstType extends ArrayList<MapFirstType> implements TrieProvider {

        private ListMapFirstType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new ListMapFirstType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl(ArrayList.class.getName(), MapFirstType.class.getName(), null, null, null);
            trieMap.put(ListMapFirstType.class.getName(), trie);
        }
    }

    public static class ListMyItemType extends ArrayList<MyItemType> implements TrieProvider {

        private ListMyItemType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new ListMyItemType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl(ArrayList.class.getName(), MyItemType.class.getName(), null, null, null);
            trieMap.put(ListMyItemType.class.getName(), trie);
        }
    }

    public static class ListMyUnknownType extends ArrayList<MyUnknownType> implements TrieProvider {

        private ListMyUnknownType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new ListMyUnknownType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl(ArrayList.class.getName(), MyUnknownType.class.getName(), null, null, null);
            trieMap.put(ListMyUnknownType.class.getName(), trie);
        }
    }


    public static class MapFirstType extends HashMap<String, Serializable> implements TrieProvider {

        private MapFirstType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new MapFirstType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl(HashMap.class.getName(), null, null, null, null);
            trie.insert(createPropertySuggestion("firstProperty1", String.class.getName()));
            trie.insert(createPropertySuggestion("firstProperty2", String.class.getName()));
            trieMap.put(MapFirstType.class.getName(), trie);
        }
    }

    public static class MapSecondType extends HashMap<String, Serializable> implements TrieProvider {

        private MapSecondType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new MapSecondType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl(HashMap.class.getName(), null, null, null, null);
            trie.insert(createPropertySuggestion("secondProperty1", String.class.getName()));
            trie.insert(createPropertySuggestion("secondProperty2", long.class.getName()));
            trieMap.put(MapSecondType.class.getName(), trie);
        }
    }

    public static class MessageAttributeType implements TrieProvider {

        private MessageAttributeType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new MessageAttributeType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl();
            trie.insert(createPropertySuggestion("component", String.class.getName()));
            trieMap.put(MessageAttributes.class.getName(), trie);
        }
    }

    public static class MyAttributeType extends MessageAttributes implements TrieProvider {

        private MyAttributeType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new MyAttributeType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl(MessageAttributes.class.getName(), null, null, null, null);
            trie.insert(createPropertySuggestion("attributeProperty1", String.class.getName()));
            trie.insert(createPropertySuggestion("attributeProperty2", long.class.getName()));
            trieMap.put(MyAttributeType.class.getName(), trie);
        }
    }

    public static class MyItemType implements TrieProvider {

        private MyItemType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new MyItemType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl();
            trie.insert(SuggestionTestUtils.createFunctionSuggestion("method1", String.class.getName()));
            trieMap.put(MyItemType.class.getName(), trie);
        }
    }

    public static class MyUnknownType {
    }

    public static class MessageType implements TrieProvider {

        private MessageType() {
        }

        public static void initialize(Map<String, Trie> trieMap) {
            new MessageType().register(trieMap);
        }

        @Override
        public void register(Map<String, Trie> trieMap) {
            Trie trie = new TrieImpl();
            trie.insert(createFunctionSuggestion("payload", MessagePayload.class.getName()));
            trie.insert(createFunctionSuggestion("attributes", MessageAttributes.class.getName()));
            trieMap.put(Message.class.getName(), trie);
        }
    }
}
