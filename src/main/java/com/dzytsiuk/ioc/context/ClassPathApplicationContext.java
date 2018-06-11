package com.dzytsiuk.ioc.context;


import com.dzytsiuk.ioc.context.inject.Injector;
import com.dzytsiuk.ioc.context.inject.RefInjector;
import com.dzytsiuk.ioc.context.inject.ValueInjector;
import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.BeanInstantiationException;
import com.dzytsiuk.ioc.exception.MultipleBeansForClassException;
import com.dzytsiuk.ioc.io.BeanDefinitionReader;
import com.dzytsiuk.ioc.io.XMLBeanDefinitionReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext {

    private Map<String, Bean> beans;
    private List<BeanDefinition> beanDefinitions;

    public ClassPathApplicationContext() {

    }

    public ClassPathApplicationContext(String... path) {
        setBeanDefinitionReader(new XMLBeanDefinitionReader(path));
        start();
    }

    public void start() {
        constructBeans();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = beans.get(beanDefinition.getId());

            for (Injector injector : new Injector[]{
                    new RefInjector(beanDefinition.getRefDependencies(), beans),
                    new ValueInjector(beanDefinition.getDependencies())}) {
                injector.inject(bean);
            }

        }
    }

    private void constructBeans() {
        beans = new HashMap<>();

        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                Bean bean = new Bean();

                bean.setValue(Class.forName(beanDefinition.getBeanClassName()).newInstance());
                bean.setId(beanDefinition.getId());
                beans.put(beanDefinition.getId(), bean);

            } catch (InstantiationException e) {
                throw new BeanInstantiationException("No constructor found for " + beanDefinition.getBeanClassName(), e);
            } catch (IllegalAccessException e) {
                throw new BeanInstantiationException("Illegal access to " + beanDefinition.getBeanClassName() + " constructor", e);
            } catch (ClassNotFoundException e) {
                throw new BeanInstantiationException("Class not found " + beanDefinition.getBeanClassName(), e);
            }

        }


    }


    @Override
    public <T> T getBean(Class<T> clazz) {
        T bean = null;
        int count = 0;
        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            Bean tmpBean = entry.getValue();
            if (clazz.isInstance(tmpBean.getValue())) {
                count++;
                bean = clazz.cast(tmpBean.getValue());
            }
            if (count > 1) {
                throw new MultipleBeansForClassException("Multiple beans found for " + clazz.getName());
            }
        }

        return bean;
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        Bean bean = beans.get(id);
        if (clazz.isInstance(bean.getValue())) {
            return clazz.cast(bean.getValue());
        }
        return null;
    }

    @Override
    public Object getBean(String id) {
        return beans.get(id).getValue();
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        this.beanDefinitions = beanDefinitionReader.getBeanDefinitions();
    }

}
