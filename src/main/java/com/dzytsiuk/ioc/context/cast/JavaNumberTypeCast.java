package com.dzytsiuk.ioc.context.cast;


import com.dzytsiuk.ioc.exception.BeanInstantiationException;

import static com.dzytsiuk.ioc.context.cast.JavaNumberTypeCast.JavaNumberTypesMap.*;

public class JavaNumberTypeCast {

    public static Object castPrimitive(String value, Class<?> clazz) {
        try {
            if (clazz.getName().equals(INT.type)) {
                return Integer.parseInt(value);
            }
            if (clazz.getName().equals(BOOLEAN.type)) {
                return Boolean.parseBoolean(value);
            }
            if (clazz.getName().equals(BYTE.type)) {
                return Byte.parseByte(value);
            }
            if (clazz.getName().equals(DOUBLE.type)) {
                return Double.parseDouble(value);
            }
            if (clazz.getName().equals(FLOAT.type)) {
                return Float.parseFloat(value);
            }
            if (clazz.getName().equals(LONG.type)) {
                return Long.parseLong(value);
            }
            if (clazz.getName().equals(SHORT.type)) {
                return Short.parseShort(value);
            }
            return null;
        } catch (NumberFormatException e) {
            throw new BeanInstantiationException("Failed to cast " + value + " to " + clazz.getName(), e);
        }
    }

    enum JavaNumberTypesMap {
        INT("int"), BOOLEAN("boolean"), BYTE("byte"), DOUBLE("double"), FLOAT("float"), LONG("long"),
        SHORT("short");

        private String type;

        JavaNumberTypesMap(String type) {
            this.type = type;
        }

    }
}
