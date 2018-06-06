package com.dzytsiuk.ioc.context;


import com.dzytsiuk.ioc.io.BeanDefinitionReader;

public interface ApplicationContext {

    <T> T getBean(Class clazz);

    <T> T getBean(String name, Class clazz);

    <T> T getBean(String name);

    void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader);
}
