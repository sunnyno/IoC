package com.dzytsiuk.ioc.context

import com.dzytsiuk.ioc.exception.MultipleBeansForClassException
import com.dzytsiuk.ioc.io.XMLBeanDefinitionReader
import com.dzytsiuk.ioc.service.MailService
import com.dzytsiuk.ioc.service.PaymentService
import com.dzytsiuk.ioc.service.UserService

class ClassPathApplicationContextITestGroovy extends GroovyTestCase {

    private UserService userService
    private MailService mailService
    private PaymentService paymentService
    private PaymentService paymentServiceWithMaxAmount
    private ApplicationContext applicationContext;

    @Override
    void setUp() {
        mailService = new MailService(protocol: "POP3", port: 3000)
        userService = new UserService(mailService: mailService)
        paymentService = new PaymentService(mailService: mailService)
        paymentServiceWithMaxAmount = new PaymentService(mailService: mailService, maxAmount: 500)
        applicationContext = new ClassPathApplicationContext("src/test/resources/context.xml")
    }

    void testApplicationContextInstantiation() {
        ApplicationContext applicationContextSetReader = new ClassPathApplicationContext()
        applicationContextSetReader.setBeanDefinitionReader(new XMLBeanDefinitionReader("src/test/resources/context.xml"))
        applicationContextSetReader.start()
        assertEquals(applicationContext.getBean(UserService.class), applicationContextSetReader.getBean(UserService.class))
        assertEquals(applicationContext.getBean("mailService", MailService.class), applicationContextSetReader.getBean("mailService", MailService.class))
        assertEquals(applicationContext.getBean("paymentWithMaxService"), applicationContextSetReader.getBean("paymentWithMaxService"))
    }

    void testGetBeanByClass() {
        assertEquals(userService, applicationContext.getBean(UserService.class))
        assertEquals(mailService, applicationContext.getBean(MailService.class))
    }

    void testGetBeanByClassException() {
        shouldFail(MultipleBeansForClassException) { applicationContext.getBean(PaymentService.class) }
    }

    void testGetBeanByNameAndClass() {
        assertEquals(userService,applicationContext.getBean("userService", UserService.class))
        assertEquals(mailService, applicationContext.getBean("mailService", MailService.class))
        assertEquals(paymentService,applicationContext.getBean("paymentService", PaymentService.class))
        assertEquals(paymentServiceWithMaxAmount, applicationContext.getBean("paymentWithMaxService", PaymentService.class))
    }

    void testGetBeanByName() {
        assertEquals(userService, applicationContext.getBean("userService"))
        assertEquals(mailService, applicationContext.getBean("mailService"))
        assertEquals(paymentService, applicationContext.getBean("paymentService"))
        assertEquals(paymentServiceWithMaxAmount, applicationContext.getBean("paymentWithMaxService"))
    }
}
