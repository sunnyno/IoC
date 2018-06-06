package com.dzytsiuk.ioc.io

import com.dzytsiuk.ioc.entity.BeanDefinition
import com.dzytsiuk.ioc.exception.SourceParseException

class XMLBeanDefinitionReaderTestGroovy extends GroovyTestCase {
    private def expectedBeanDefinitions = []

    @Override
    void setUp() {
        expectedBeanDefinitions.add(new BeanDefinition(id: "mailService",
                beanClassName: "com.dzytsiuk.ioc.service.MailService", dependencies:
                ["protocol": "POP3", "port": "3000"]))

        expectedBeanDefinitions.add(new BeanDefinition(id: "userService",
                beanClassName: "com.dzytsiuk.ioc.service.UserService",
                refDependencies: ["mailService": "mailService"]))
        expectedBeanDefinitions.add(new BeanDefinition(id: "paymentService",
                beanClassName: "com.dzytsiuk.ioc.service.PaymentService",
                refDependencies: ["mailService": "mailService"]))
        expectedBeanDefinitions.add(new BeanDefinition(id: "paymentWithMaxService",
                beanClassName: "com.dzytsiuk.ioc.service.PaymentService", dependencies:
                ["maxAmount": "500"], refDependencies: ["mailService": "mailService"]))

    }

    void testGetBeanDefinitions() {
        XMLBeanDefinitionReader xmlBeanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml")
        def actualBeanDefinitions = xmlBeanDefinitionReader.getBeanDefinitions()
        expectedBeanDefinitions.each { assertTrue(actualBeanDefinitions.remove(it)) }
    }

    void testGetBeanDefinitionsException() {
        shouldFail(SourceParseException) {
            new XMLBeanDefinitionReader("src/test/resources/context-exception.xml").getBeanDefinitions()
        }
    }


    void testSetImportedContextFileNames() {
        XMLBeanDefinitionReader xmlBeanDefinitionReader = new XMLBeanDefinitionReader("src/test/resources/context.xml")
        List<String> expectedPaths = ["src/test/resources/context.xml",
                                      "src/test/resources/payment-context.xml",
                                      "src/test/resources/payment-max-context.xml"]

        xmlBeanDefinitionReader.setImportedContextFileNames(Arrays.asList("src/test/resources/context.xml"))
        List actualPaths = xmlBeanDefinitionReader.contextFiles
        expectedPaths.each { assertTrue(actualPaths.remove(it)) }
    }


}
