package com.dzytsiuk.ioc.context.inject;


import com.dzytsiuk.ioc.entity.Bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RefInjector extends Injector {
    private Map<String, Bean> beans;

    public RefInjector(Map<String, String> dependencies, Map<String, Bean> beans) {
        this.beans = beans;
        super.setDependencies(dependencies);
    }


    @Override
    void invokeSetter(Bean bean, String propertyValue, Method method, Class<?> argumentType) throws IllegalAccessException, InvocationTargetException {
        method.invoke(bean.getValue(), argumentType.cast(beans.get(propertyValue).getValue()));
    }
}
