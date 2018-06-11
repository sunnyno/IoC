package com.dzytsiuk.ioc.context.inject;

import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.exception.BeanInstantiationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class Injector {
    private static final String SETTER_PREFIX = "set";
    private static final int SETTER_PARAMETER_INDEX = 0;

    private Map<String, String> dependencies;

    public void inject(Bean bean) {
        if (dependencies != null) {
            for (Map.Entry<String, String> next : dependencies.entrySet()) {
                String propertyName = next.getKey();
                String propertyValue = next.getValue();
                Class clazz = bean.getValue().getClass();
                Method[] methods = clazz.getMethods();
                boolean hasSetter = false;
                try {
                    for (Method method : methods) {
                        if (method.getName().equals(getSetterName(propertyName))) {
                            Class<?> argumentType = method.getParameterTypes()[SETTER_PARAMETER_INDEX];

                            invokeSetter(bean, propertyValue, method, argumentType);
                            hasSetter = true;
                            break;
                        }
                    }
                    if (!hasSetter) {
                        throw new BeanInstantiationException("No setter found for " + bean.getId());
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new BeanInstantiationException("Error setting property " + propertyName, e);
                }


            }
        }
    }

    abstract void invokeSetter(Bean bean, String propertyValue, Method method, Class<?> argumentType) throws IllegalAccessException, InvocationTargetException;


    private String getSetterName(String propertyName) {
        return SETTER_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    void setDependencies(Map<String, String> dependencies) {
        this.dependencies = dependencies;
    }

}
