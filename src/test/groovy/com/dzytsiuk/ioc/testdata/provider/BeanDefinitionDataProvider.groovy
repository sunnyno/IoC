package com.dzytsiuk.ioc.testdata.provider

import com.dzytsiuk.ioc.entity.BeanDefinition
import org.testng.annotations.DataProvider

class BeanDefinitionDataProvider {

    @DataProvider(name = "beanDefinitionsProvider")
    static Object[][] provideBeanDefinitions() {
        def expectedBeanDefinitions = [
                (new BeanDefinition(id: "mailService",
                        beanClassName: "com.dzytsiuk.ioc.testdata.service.MailService", dependencies:
                        ["protocol": "POP3", "port": "3000"])),

                (new BeanDefinition(id: "userService",
                        beanClassName: "com.dzytsiuk.ioc.testdata.service.UserService",
                        refDependencies: ["mailService": "mailService"])),
                (new BeanDefinition(id: "paymentService",
                        beanClassName: "com.dzytsiuk.ioc.testdata.service.PaymentService",
                        refDependencies: ["mailService": "mailService"])),
                (new BeanDefinition(id: "paymentWithMaxService",
                        beanClassName: "com.dzytsiuk.ioc.testdata.service.PaymentService", dependencies:
                        ["maxAmount": "500"], refDependencies: ["mailService": "mailService"]))] as ArrayList<BeanDefinition>
        def beanDefinitionArray = new Object[1][]
        beanDefinitionArray[0] = [expectedBeanDefinitions] as Object[]

        return beanDefinitionArray;
    }

    @DataProvider(name = "beanDefinitionsPathProvider")
    static Object[][] provideBeanDefinitionsPaths() {
        def expectedPaths = ["src/test/resources/context.xml",
                             "src/test/resources/payment-context.xml",
                             "src/test/resources/payment-max-context.xml"] as ArrayList<String>
        def pathsArray = new Object[1][]
        pathsArray[0] = [expectedPaths] as Object[]
        return pathsArray
    }

}
