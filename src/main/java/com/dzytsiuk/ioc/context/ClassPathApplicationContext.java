package com.dzytsiuk.ioc.context;


import com.dzytsiuk.ioc.context.cast.JavaNumberTypeCast;
import com.dzytsiuk.ioc.exception.BeanInstantiationException;
import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.MultipleBeansForClassException;
import com.dzytsiuk.ioc.io.BeanDefinitionReader;
import com.dzytsiuk.ioc.io.XMLBeanDefinitionReader;

import java.lang.reflect.Method;
import java.util.*;

public class ClassPathApplicationContext implements ApplicationContext {
    private static final String SETTER_PREFIX = "set";
    private static final int SETTER_PARAMETER_INDEX = 0;

    private Map<String, Bean> beans;
    private List<BeanDefinition> beanDefinitions;

    public ClassPathApplicationContext() {

    }

    public ClassPathApplicationContext(String... path) {
        setBeanDefinitionReader(new XMLBeanDefinitionReader(path));
        start();
    }

    public void start() {
        beans = new HashMap<>();
        constructBeans();
        injectValueDependencies();
        injectRefDependencies();
    }

    private void constructBeans() {

        for (BeanDefinition beanDefinition : beanDefinitions) {
            injectBeanIdAndValue(beanDefinition);
        }

    }


    private void injectValueDependencies() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = beans.get(beanDefinition.getId());
            if (beanDefinition.getDependencies() != null) {
                injectDependencies(beanDefinition.getDependencies(), bean, false);
            }
        }
    }

    private void injectRefDependencies() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = beans.get(beanDefinition.getId());
            if (beanDefinition.getRefDependencies() != null) {
                injectDependencies(beanDefinition.getRefDependencies(), bean, true);
            }
        }

    }


    private void injectBeanIdAndValue(BeanDefinition beanDefinition) {
        try {
            Bean bean = new Bean();
            bean.setValue(Class.forName(beanDefinition.getBeanClassName()).newInstance());
            bean.setId(beanDefinition.getId());
            beans.put(beanDefinition.getId(), bean);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new BeanInstantiationException("Invalid class definition " + beanDefinition.getBeanClassName(), e);
        }
    }


    private void injectDependencies(Map<String, String> dependencies, Bean bean, boolean ref) {
        for (Map.Entry<String, String> next : dependencies.entrySet()) {
            String propertyName = next.getKey();
            String propertyValue = next.getValue();
            try {
                Class clazz = bean.getValue().getClass();
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getName().equals(getSetterName(propertyName))) {
                        Class<?> argumentType = method.getParameterTypes()[SETTER_PARAMETER_INDEX];
                        if (ref) {
                            method.invoke(bean.getValue(), argumentType.cast(beans.get(propertyValue).getValue()));
                        } else {
                            method.invoke(bean.getValue(), argumentType.isPrimitive() ?
                                    JavaNumberTypeCast.castPrimitive(propertyValue, argumentType)
                                    : argumentType.cast(propertyValue));
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                throw new BeanInstantiationException("Error setting property " + propertyName, e);
            }
        }
    }

    private String getSetterName(String propertyName) {
        return SETTER_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }


    @Override
    public <T> T getBean(Class clazz) {
        T bean = null;
        int count = 0;
        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            Bean tmpBean = entry.getValue();
            if (clazz.isInstance(tmpBean.getValue())) {
                count++;
                bean = (T) tmpBean.getValue();
            }
            if (count > 1) {
                throw new MultipleBeansForClassException("Multiple beans found for " + clazz.getName());
            }
        }

        return bean;
    }

    @Override
    public <T> T getBean(String name, Class clazz) {
        if (clazz.isInstance(beans.get(name).getValue())) {
            return (T) beans.get(name).getValue();
        }
        return null;
    }

    @Override
    public <T> T getBean(String name) {
        return (T) beans.get(name).getValue();
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        this.beanDefinitions = beanDefinitionReader.getBeanDefinitions();
    }

}
