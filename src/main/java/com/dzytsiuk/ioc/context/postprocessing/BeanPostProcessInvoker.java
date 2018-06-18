package com.dzytsiuk.ioc.context.postprocessing;

import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.BeanInstantiationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeanPostProcessInvoker {

    private Map<String, Bean> beans;
    private List<Bean> systemBeans;
    private List<BeanDefinition> beanDefinitions;

    public BeanPostProcessInvoker(Map<String, Bean> beans, List<BeanDefinition> beanDefinitions) {
        this.beans = beans;
        this.beanDefinitions = beanDefinitions;
        systemBeans = getSystemBeans();
    }


    private List<Bean> getSystemBeans() {
        List<Bean> systemBeans = new ArrayList<>();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition.isSystem()) {
                systemBeans.add(beans.get(beanDefinition.getId()));
            }
        }
        return systemBeans;
    }

    private void postProcess(String systemBeanClassMethod) {
        try {
            for (Bean systemBean : systemBeans) {
                Object systemBeanObj = systemBean.getValue();
                Class<?> systemBeanClass = systemBeanObj.getClass();
                Method postProcess = systemBeanClass.getMethod(systemBeanClassMethod, Object.class, String.class);
                for (Map.Entry<String, Bean> beanEntry : beans.entrySet()) {

                    Bean currentBean = beanEntry.getValue();
                    Object currentBeanObject = currentBean.getValue();

                    if (!currentBeanObject.getClass().equals(systemBeanClass)) {
                        currentBean.setValue(postProcess.invoke(systemBeanObj, currentBeanObject, beanEntry.getKey()));
                    }

                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException("Post processor error", e);
        }

    }


    public void postProcessAfterInitialization() {
        postProcess("postProcessAfterInitialization");
        for (Bean systemBean : systemBeans) {
            beans.remove(systemBean.getId());
        }

    }

    public void postProcessBeforeInitialization() {
        postProcess("postProcessBeforeInitialization");
    }

}
