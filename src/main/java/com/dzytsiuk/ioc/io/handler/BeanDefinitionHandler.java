package com.dzytsiuk.ioc.io.handler;

import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.SourceParseException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.List;

public class BeanDefinitionHandler extends DefaultHandler {

    private static final String BEANS_TAG = "beans";
    private static final String BEAN_TAG = "bean";
    private static final String ID_ATTRIBUTE = "id";
    private static final String CLASS_ATTRIBUTE = "class";
    private static final String PROPERTY_TAG = "property";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String REF_ATTRIBUTE = "ref";
    private static final String INIT_METHOD_ATTRIBUTE = "init-method";
    private boolean hasBeansTag = false;

    private List<BeanDefinition> beanDefinitions;

    public BeanDefinitionHandler(List<BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {

        if (qName.equalsIgnoreCase(BEANS_TAG)) {
            hasBeansTag = true;
        }

        if (!hasBeansTag && !qName.equalsIgnoreCase(BEANS_TAG)) {
            throw new SourceParseException("Root Element " + BEANS_TAG + " is not found");
        }


        if (qName.equalsIgnoreCase(BEAN_TAG)) {
            BeanDefinition beanDefinition = new BeanDefinition();
            String className = attributes.getValue(CLASS_ATTRIBUTE);
            String id = attributes.getValue(ID_ATTRIBUTE);
            beanDefinition.setId((id != null)? id : className);
            beanDefinition.setBeanClassName(className);
            beanDefinition.setSystem(false);
            beanDefinitions.add(beanDefinition);
        } else {

            if (qName.equalsIgnoreCase(PROPERTY_TAG)) {
                BeanDefinition beanDefinition = beanDefinitions.get(beanDefinitions.size() - 1);
                String name = attributes.getValue(NAME_ATTRIBUTE);
                String value = attributes.getValue(VALUE_ATTRIBUTE);
                String ref = attributes.getValue(REF_ATTRIBUTE);
                if (ref != null) {
                    if (beanDefinition.getRefDependencies() == null) {
                        beanDefinition.setRefDependencies(new HashMap<>());
                    }
                    beanDefinition.getRefDependencies().put(name, ref);
                } else {
                    if (beanDefinition.getDependencies() == null) {
                        beanDefinition.setDependencies(new HashMap<>());
                    }
                    beanDefinition.getDependencies().put(name, value);
                }
            }
        }
    }

}