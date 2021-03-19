package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import java.util.Map;

class TypeBuiltInPrimitive implements TypeBuiltIn.BuiltInType {

    public static final TypeProxy INT = TypeProxy.create(int.class);
    public static final TypeProxy OBJECT = TypeProxy.create(Object.class);
    public static final TypeProxy STRING = TypeProxy.create(String.class);
    public static final TypeProxy BOOLEAN = TypeProxy.create(boolean.class);

    @Override
    public void register(Map<String, Trie> typeTrieMap, TypeAndTries typeAndTries) {
        registerPrimitive(String.class, typeTrieMap);
        registerPrimitive(int.class, typeTrieMap);
        registerPrimitive(Integer.class, typeTrieMap);
        registerPrimitive(long.class, typeTrieMap);
        registerPrimitive(Long.class, typeTrieMap);
        registerPrimitive(float.class, typeTrieMap);
        registerPrimitive(Float.class, typeTrieMap);
        registerPrimitive(double.class, typeTrieMap);
        registerPrimitive(Double.class, typeTrieMap);
        registerPrimitive(boolean.class, typeTrieMap);
        registerPrimitive(Boolean.class, typeTrieMap);
        registerPrimitive(Void.class, typeTrieMap, "void");
        registerPrimitive(byte[].class, typeTrieMap, "byte[]");
        registerPrimitive(Byte[].class, typeTrieMap, "byte[]");
    }

    private void registerPrimitive(Class<?> clazz, Map<String, Trie> typeTrieMap) {
        registerPrimitive(clazz, typeTrieMap, clazz.getSimpleName());
    }

    private void registerPrimitive(Class<?> clazz, Map<String, Trie> typeTrieMap, String displayName) {
        Trie trie = new TrieDefault(clazz.getName(), Object.class.getName(), displayName);
        typeTrieMap.put(clazz.getName(), trie);
    }
}
