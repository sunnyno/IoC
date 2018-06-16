package com.dzytsiuk.ioc.testdata.service;

import com.dzytsiuk.ioc.context.BeanFactoryPostProcessor;
import com.dzytsiuk.ioc.entity.BeanDefinition;

import java.util.List;
import java.util.Map;

public class BeanFactoryProcessorService implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList) {
        for (BeanDefinition beanDefinition : beanDefinitionList) {
            if (beanDefinition.getId().equals("mailService")) {
                Map<String, String> properties = beanDefinition.getDependencies();
                properties.put("port", "10000");
                properties.put("protocol", "postProcessProtocol");

            }
        }
    }
}
