package com.dzytsiuk.ioc.context.postprocessing;

import com.dzytsiuk.ioc.entity.BeanDefinition;

import java.util.List;

public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList);

}
