package com.dzytsiuk.ioc.context;


import com.dzytsiuk.ioc.context.cast.JavaNumberTypeCast;
import com.dzytsiuk.ioc.exception.BeanInstantiationException;
import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.io.BeanDefinitionReader;
import com.dzytsiuk.ioc.io.factory.BeanDefinitionFactoryMethod;

import java.lang.reflect.Method;
import java.util.*;

public class ClassPathApplicationContext implements ApplicationContext {
    private static final String SETTER_PREFIX = "set";
    private static final int SETTER_PARAMETER_NUMBER = 0;

    private List<String> path;
    private Set<BeanDefinitionReader> beanDefinitionReaderSet;
    private Map<String, Bean> beans;
    private List<BeanDefinition> beanDefinitions;

    public ClassPathApplicationContext(String... path) {
        this.path = Arrays.asList(path);
        beanDefinitions = new ArrayList<>();
        beans = new HashMap<>();
        createBeansFromBeanDefinitions();
    }

    private void createBeansFromBeanDefinitions() {

        setBeanDefinitionReaders();
        setImports();

        for (BeanDefinitionReader definitionReader : beanDefinitionReaderSet) {
            beanDefinitions.addAll(definitionReader.readBeanDefinitions());
        }

        for (BeanDefinition beanDefinition : beanDefinitions) {
            injectBeanIdAndValue(beanDefinition);
        }

        for (BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = beans.get(beanDefinition.getId());
            if (beanDefinition.getDependencies() != null) {
                injectDependencies(beanDefinition.getDependencies(), bean, false);
            }
            if (beanDefinition.getRefDependencies() != null) {
                injectDependencies(beanDefinition.getRefDependencies(), bean, true);
            }
        }
    }

    private void setImports() {
        for (BeanDefinitionReader definitionReader : beanDefinitionReaderSet) {
            definitionReader.setImportedContextFileNames(path);
        }
    }

    private void setBeanDefinitionReaders() {
        BeanDefinitionFactoryMethod beanDefinitionFactoryMethod = new BeanDefinitionFactoryMethod();
        beanDefinitionReaderSet = new HashSet<>();

        for (String filePath : path) {
            BeanDefinitionReader beanDefinitionReader = beanDefinitionFactoryMethod
                    .getBeanDefinitionReader(filePath.substring(filePath.lastIndexOf(".") + 1));
            setBeanDefinitionReader(beanDefinitionReader);
            beanDefinitionReader.setContextFilePath(filePath);
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
                        Class<?> argumentType = method.getParameterTypes()[SETTER_PARAMETER_NUMBER];
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
    public Object getBean(Class clazz) {
        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            if (clazz.isInstance(entry.getValue().getValue())) {
                return entry.getValue().getValue();
            }

        }
        return null;
    }

    @Override
    public Object getBean(String name, Class clazz) {
        if (clazz.isInstance(beans.get(name).getValue())) {
            return beans.get(name).getValue();
        }
        return null;
    }

    @Override
    public Object getBean(String name) {
        return beans.get(name).getValue();
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        beanDefinitionReaderSet.add(beanDefinitionReader);
    }

    //for tests
    List<BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}
