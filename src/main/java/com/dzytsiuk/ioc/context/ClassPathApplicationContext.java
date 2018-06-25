package com.dzytsiuk.ioc.context;


import com.dzytsiuk.ioc.context.inject.Injector;
import com.dzytsiuk.ioc.context.inject.RefInjector;
import com.dzytsiuk.ioc.context.inject.ValueInjector;
import com.dzytsiuk.ioc.context.postprocessing.BeanFactoryPostProcessor;
import com.dzytsiuk.ioc.context.postprocessing.BeanPostProcessInvoker;
import com.dzytsiuk.ioc.context.postprocessing.BeanPostProcessor;
import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.BeanInstantiationException;
import com.dzytsiuk.ioc.exception.MultipleBeansForClassException;
import com.dzytsiuk.ioc.io.BeanDefinitionReader;
import com.dzytsiuk.ioc.io.XMLBeanDefinitionReader;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext {

    private static final String POST_PROCESS_BEAN_FACTORY_METHOD = "postProcessBeanFactory";
    private Map<String, Bean> beans;
    private List<BeanDefinition> beanDefinitions;

    public ClassPathApplicationContext() {

    }

    public ClassPathApplicationContext(String... path) {
        setBeanDefinitionReader(new XMLBeanDefinitionReader(path));
        start();
    }

    public void start() {
        //construct
        invokeBeanFactoryPostProcessor();
        constructBeans();

        //inject ref and value
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = beans.get(beanDefinition.getId());

            for (Injector injector : new Injector[]{
                    new RefInjector(beanDefinition.getRefDependencies(), beans),
                    new ValueInjector(beanDefinition.getDependencies())}) {
                injector.inject(bean);
            }

        }

        //post processing
        BeanPostProcessInvoker beanPostProcessInvoker = new BeanPostProcessInvoker(beans, beanDefinitions);
        beanPostProcessInvoker.postProcessBeforeInitialization();
        invokeInitMethod();
        beanPostProcessInvoker.postProcessAfterInitialization();
    }

    private void invokeBeanFactoryPostProcessor() {
        Iterator<BeanDefinition> iterator = beanDefinitions.iterator();
        while (iterator.hasNext()) {
            try {
                BeanDefinition beanDefinition = iterator.next();
                Class<?>[] interfaces = Class.forName(beanDefinition.getBeanClassName()).getInterfaces();
                for (Class<?> classInterface : interfaces) {
                    if (classInterface.equals(BeanFactoryPostProcessor.class)) {
                        Bean bean = constructBean(beanDefinition);
                        Method postProcessBeanFactory = bean.getValue().getClass().getMethod(POST_PROCESS_BEAN_FACTORY_METHOD, List.class);
                        iterator.remove();
                        postProcessBeanFactory.invoke(bean.getValue(), beanDefinitions);
                        break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void constructBeans() {
        beans = new HashMap<>();

        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {

                beans.put(beanDefinition.getId(), constructBean(beanDefinition));

            } catch (InstantiationException e) {
                throw new BeanInstantiationException("No constructor found for " + beanDefinition.getBeanClassName(), e);
            } catch (IllegalAccessException e) {
                throw new BeanInstantiationException("Illegal access to " + beanDefinition.getBeanClassName() + " constructor", e);
            } catch (ClassNotFoundException e) {
                throw new BeanInstantiationException("Class not found " + beanDefinition.getBeanClassName(), e);
            }

        }
    }

    private Bean constructBean(BeanDefinition beanDefinition) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        //set isSystemProperty
        Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
        for (Class<?> beanInterface : beanClass.getInterfaces()) {
            if (beanInterface.equals(BeanPostProcessor.class)) {
                beanDefinition.setSystem(true);
            }
        }
        //construct bean
        Bean bean = new Bean();
        bean.setValue(beanClass.newInstance());
        bean.setId(beanDefinition.getId());
        return bean;

    }


    private void invokeInitMethod() {
        setInitMethods();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String initMethodName = beanDefinition.getInitMethodName();
            if (initMethodName != null) {
                Bean bean = beans.get(beanDefinition.getId());
                Object beanValue = bean.getValue();
                try {
                    Class<?> beanClass = beanValue.getClass();
                    Method initMethod = beanClass.getMethod(initMethodName);
                    initMethod.invoke(beanValue);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void setInitMethods() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = beans.get(beanDefinition.getId());
            Class<?> beanClass = bean.getValue().getClass();
            for (Method method : beanClass.getMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    beanDefinition.setInitMethodName(method.getName());
                    break;
                }

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
