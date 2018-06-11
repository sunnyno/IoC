package com.dzytsiuk.ioc.context.cast;


public class JavaNumberTypeCast {

    public static Object castPrimitive(String value, Class<?> clazz) {
        if (clazz == int.class) {
            return Integer.valueOf(value);
        }
        if (clazz == boolean.class) {
            return Boolean.valueOf(value);
        }
        if (clazz == byte.class) {
            return Byte.valueOf(value);
        }
        if (clazz == double.class) {
            return Double.valueOf(value);
        }
        if (clazz == float.class) {
            return Float.valueOf(value);
        }
        if (clazz == long.class) {
            return Long.valueOf(value);
        }
        if (clazz == short.class) {
            return Short.valueOf(value);
        }
        return null;
    }

}
