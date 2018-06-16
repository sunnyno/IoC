package com.dzytsiuk.ioc.testdata.service;


import com.dzytsiuk.ioc.context.BeanPostProcessor;

public class BeanPostProcessorService implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean.getClass().equals(MailService.class)){
            ((MailService)bean).setProtocol("postProcessProtocol");
        }
            return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
