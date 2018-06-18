package com.dzytsiuk.ioc.testdata.service;

import com.dzytsiuk.ioc.context.postprocessing.BeanPostProcessor;

public class BeanPostProcessorService2 implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean.getClass().equals(MailService.class)) {
            ((MailService) bean).setPort(10000);
        }

        return bean;
    }
}
