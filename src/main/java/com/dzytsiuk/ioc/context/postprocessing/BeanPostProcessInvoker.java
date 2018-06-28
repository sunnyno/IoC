package com.dzytsiuk.ioc.context.postprocessing;

import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.entity.BeanDefinition;

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

    private void postProcess(boolean before) {
        for (Bean systemBean : systemBeans) {
            BeanPostProcessor systemBeanObj = (BeanPostProcessor) systemBean.getValue();
            for (Map.Entry<String, Bean> beanEntry : beans.entrySet()) {

                Bean currentBean = beanEntry.getValue();
                Object currentBeanObject = currentBean.getValue();

                if (currentBeanObject != systemBeanObj) {
                    Object newBeanValue;
                    if (before) {
                        newBeanValue = systemBeanObj.postProcessBeforeInitialization(currentBeanObject, beanEntry.getKey());
                    } else {
                        newBeanValue = systemBeanObj.postProcessAfterInitialization(currentBeanObject, beanEntry.getKey());
                    }
                    currentBean.setValue(newBeanValue);
                }
            }
        }


    }


    public void beforeInit() {
        postProcess(true);
    }

    public void afterInit() {
        postProcess(false);
        for (Bean systemBean : systemBeans) {
            beans.remove(systemBean.getId());
        }

    }

}
