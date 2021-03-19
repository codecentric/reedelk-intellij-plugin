package de.codecentric.reedelk.plugin.commons;

public class ImplementsInterface {

    private ImplementsInterface() {
    }

    public static boolean by(Class<?> target, Class<?> targetInterfaceClazz) {
        if (target == null) return false;
        Class<?>[] interfaces = target.getInterfaces();
        for (Class<?> interfaceClazz : interfaces) {
            if (interfaceClazz == targetInterfaceClazz) return true;
        }
        return by(target.getSuperclass(), targetInterfaceClazz);
    }
}
