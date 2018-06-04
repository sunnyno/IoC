package com.dzytsiuk.ioc.io.factory;


import com.dzytsiuk.ioc.io.BeanDefinitionReader;
import com.dzytsiuk.ioc.io.XMLBeanDefinitionReader;

public class BeanDefinitionFactoryMethod {

    private static final String XML_EXTENSION = "xml";

    public BeanDefinitionReader getBeanDefinitionReader(String extension) {
        if (extension.compareToIgnoreCase(XML_EXTENSION) == 0) {
            return XMLBeanDefinitionReader.getINSTANCE();
        }
        throw new IllegalArgumentException("Unsupported context file extension");
    }
}
