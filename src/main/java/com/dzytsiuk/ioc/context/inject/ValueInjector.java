package com.dzytsiuk.ioc.context.inject;

import com.dzytsiuk.ioc.context.cast.JavaNumberTypeCast;
import com.dzytsiuk.ioc.entity.Bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ValueInjector extends Injector {


    public ValueInjector(Map<String, String> dependencies) {
        super.setDependencies(dependencies);
    }

    @Override
    void invokeSetter(Bean bean, String propertyValue, Method method, Class<?> argumentType) throws IllegalAccessException, InvocationTargetException {
        method.invoke(bean.getValue(), argumentType.isPrimitive() ?
                JavaNumberTypeCast.castPrimitive(propertyValue, argumentType)
                : argumentType.cast(propertyValue));
    }
}
