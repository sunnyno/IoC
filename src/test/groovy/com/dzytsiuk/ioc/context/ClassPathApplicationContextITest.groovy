package com.dzytsiuk.ioc.context

import com.dzytsiuk.ioc.exception.BeanInstantiationException
import com.dzytsiuk.ioc.exception.MultipleBeansForClassException
import com.dzytsiuk.ioc.io.XMLBeanDefinitionReader
import com.dzytsiuk.ioc.testdata.provider.BeanDataProvider
import com.dzytsiuk.ioc.testdata.service.MailService
import com.dzytsiuk.ioc.testdata.service.PaymentService
import com.dzytsiuk.ioc.testdata.service.UserService
import org.testng.annotations.Test

import static groovy.test.GroovyAssert.shouldFail
import static org.testng.Assert.assertEquals

class ClassPathApplicationContextITest {


    @Test
    void "initialize application context with set bean definitions"() {
        ApplicationContext applicationContextSetBDReader = new ClassPathApplicationContext()
        applicationContextSetBDReader.setBeanDefinitionReader(new XMLBeanDefinitionReader("src/test/resources/context.xml"))
        applicationContextSetBDReader.start()
        ApplicationContext applicationContext = new ClassPathApplicationContext("src/test/resources/context.xml")
        assertEquals(applicationContextSetBDReader.getBean(UserService.class), applicationContext.getBean(UserService.class))
        assertEquals(applicationContextSetBDReader.getBean("mailService", MailService.class), applicationContext.getBean("mailService", MailService.class))
        assertEquals(applicationContextSetBDReader.getBean("paymentWithMaxService"), applicationContext.getBean("paymentWithMaxService"))
    }

    @Test(dataProvider = "beanProvider", dataProviderClass = BeanDataProvider.class)
    void "get bean by class"(beans) {
        ApplicationContext applicationContext = new ClassPathApplicationContext("src/test/resources/context.xml")
        assertEquals(applicationContext.getBean(UserService.class), beans.get("userService").getValue())
        assertEquals(applicationContext.getBean(MailService.class), beans.get("mailService").getValue())
    }

    @Test
    void "multiple beans exception"() {
        ApplicationContext applicationContext = new ClassPathApplicationContext("src/test/resources/context.xml")
        def message = shouldFail(MultipleBeansForClassException) { applicationContext.getBean(PaymentService.class) }
        assertEquals(message.message, "Multiple beans found for com.dzytsiuk.ioc.testdata.service.PaymentService")
    }

    @Test
    void "no default constructor exception"() {
        def actualMessage = shouldFail(BeanInstantiationException) {
            new ClassPathApplicationContext("src/test/resources/context-nodefaultconstructor.xml")
        }
        assertEquals(actualMessage.message, "No constructor found for com.dzytsiuk.ioc.testdata.service.NoDefaultConstructorService")
    }

    @Test
    void "no setter exception"() {
        def actualMessage = shouldFail(BeanInstantiationException) {
            new ClassPathApplicationContext("src/test/resources/context-nosetter.xml")
        }
        assertEquals(actualMessage.message, "No setter found for noSetter");
    }

    @Test(dataProvider = "beanProvider", dataProviderClass = BeanDataProvider.class)
    void "get bean by name and class"(beans) {
        ApplicationContext applicationContext = new ClassPathApplicationContext("src/test/resources/context.xml")
        assertEquals(applicationContext.getBean("userService", UserService.class), beans.get("userService").getValue())
        assertEquals(applicationContext.getBean("mailService", MailService.class), beans.get("mailService").getValue())
        assertEquals(applicationContext.getBean("paymentService", PaymentService.class), beans.get("paymentService").getValue())
        assertEquals(applicationContext.getBean("paymentWithMaxService", PaymentService.class), beans.get("paymentServiceWithMaxAmount").getValue())
    }

    @Test(dataProvider = "beanProvider", dataProviderClass = BeanDataProvider.class)
    void "get bean by name"(beans) {
        ApplicationContext applicationContext = new ClassPathApplicationContext("src/test/resources/context.xml")
        assertEquals(applicationContext.getBean("userService"), beans.get("userService").getValue())
        assertEquals(applicationContext.getBean("mailService"), beans.get("mailService").getValue())
        assertEquals(applicationContext.getBean("paymentService"), beans.get("paymentService").getValue())
        assertEquals(applicationContext.getBean("paymentWithMaxService"), beans.get("paymentServiceWithMaxAmount").getValue())
    }

    @Test(dataProvider = "beanProvider", dataProviderClass = BeanDataProvider.class)
    void "get Bean with Post Processor"(beans) {
        ApplicationContext applicationContext = new ClassPathApplicationContext("src/test/resources/context-with-bean-post-processor.xml")
        assertEquals(applicationContext.getBean("mailService"), beans.get("mailServicePostProcess").getValue())

    }


    @Test(dataProvider = "beanProvider", dataProviderClass = BeanDataProvider.class)
    void "get Bean with Factory Post Processor"(beans) {
        ApplicationContext applicationContext = new ClassPathApplicationContext("src/test/resources/context-with-bean-factory-post-processor.xml")
        assertEquals(applicationContext.getBean("mailService", MailService.class), beans.get("mailServicePostProcess").getValue())

    }
}
