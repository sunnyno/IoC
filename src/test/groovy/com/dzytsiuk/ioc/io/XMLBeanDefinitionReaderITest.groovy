package com.dzytsiuk.ioc.io

import com.dzytsiuk.ioc.exception.SourceParseException
import com.dzytsiuk.ioc.testdata.provider.BeanDefinitionDataProvider
import org.testng.annotations.Test

import static groovy.test.GroovyAssert.shouldFail
import static org.testng.Assert.*;

class XMLBeanDefinitionReaderITest {

    @Test(dataProvider = "beanDefinitionsProvider", dataProviderClass = BeanDefinitionDataProvider.class)
    void getBeanDefinitionsTest(expectedBeanDefinitions) {
        XMLBeanDefinitionReader xmlBeanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml")
        def actualBeanDefinitions = xmlBeanDefinitionReader.getBeanDefinitions()
        expectedBeanDefinitions.each { assertTrue(actualBeanDefinitions.remove(it)) }
    }

    @Test
    void getBeanDefinitionsExceptionTest() {
        def actualMessage = shouldFail(SourceParseException) {
            new XMLBeanDefinitionReader("src/test/resources/context-nobeanstag.xml").getBeanDefinitions()
        }
        assertEquals(actualMessage.message, "Root Element beans is not found")
    }

    @Test(dataProvider = "beanDefinitionsPathProvider", dataProviderClass = BeanDefinitionDataProvider.class)
    void setImportedContextFileNamesTest(expectedPaths) {
        XMLBeanDefinitionReader xmlBeanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml")
        xmlBeanDefinitionReader.setImportedContextFileNames(Arrays.asList("src/test/resources/context.xml"))
        List actualPaths = xmlBeanDefinitionReader.contextFiles
        expectedPaths.each { assertTrue(actualPaths.remove(it)) }
    }


}
