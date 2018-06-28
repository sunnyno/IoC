package com.dzytsiuk.ioc.context.cast

import org.testng.annotations.Test

import static org.testng.Assert.*;

class JavaNumberTypeCastTest {
    @Test
    void castIntTest() {
        assertEquals(JavaNumberTypeCast.parsePrimitive("1", int.class).getClass(), Integer.class)
    }

    @Test
    void castBooleanTest() {
        assertEquals(JavaNumberTypeCast.parsePrimitive("1", boolean.class).getClass(), Boolean.class)
    }

    @Test
    void castByteTest() {
        assertEquals(JavaNumberTypeCast.parsePrimitive("1", byte.class).getClass(), Byte.class)
    }

    @Test
    void castDoubleTest() {
        assertEquals(JavaNumberTypeCast.parsePrimitive("1.0", double.class).getClass(), Double.class)
    }

    @Test
    void castFloatTest() {
        assertEquals(JavaNumberTypeCast.parsePrimitive("1.0", float.class).getClass(), Float.class)
    }

    @Test
    void castLongTest() {
        assertEquals(JavaNumberTypeCast.parsePrimitive("1", long.class).getClass(), Long.class)
    }

    @Test
    void castShortTest() {
        assertEquals(JavaNumberTypeCast.parsePrimitive("1", short.class).getClass(), Short.class)
    }

}
