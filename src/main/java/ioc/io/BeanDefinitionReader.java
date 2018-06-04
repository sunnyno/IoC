package ioc.io;


import ioc.entity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {
    List<BeanDefinition> readBeanDefinitions();

    void setContextFilePath(String contextFile);

}
