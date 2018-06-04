package com.dzytsiuk.ioc.io;


import com.dzytsiuk.ioc.entity.BeanDefinition;

import java.io.File;
import java.util.List;

public interface BeanDefinitionReader {
    List<BeanDefinition> readBeanDefinitions();

    void setContextFilePath(String contextFile);

}
