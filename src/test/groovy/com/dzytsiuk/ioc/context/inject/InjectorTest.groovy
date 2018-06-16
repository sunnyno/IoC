package com.dzytsiuk.ioc.context.inject

import com.dzytsiuk.ioc.entity.Bean
import com.dzytsiuk.ioc.entity.BeanDefinition
import com.dzytsiuk.ioc.testdata.provider.BeanDataProvider
import com.dzytsiuk.ioc.testdata.provider.BeanDefinitionDataProvider
import com.dzytsiuk.ioc.testdata.service.PaymentService
import com.dzytsiuk.ioc.testdata.service.UserService
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

class InjectorTest {

    @Test(dataProvider = "beanProvider", dataProviderClass = BeanDataProvider.class)
    void injectUserServiceTest(beans) {

        def beanDefinitions = BeanDefinitionDataProvider.provideBeanDefinitions()[0] as List<BeanDefinition>
        def userServiceBD = beanDefinitions[0].find { it.id == "userService" } as BeanDefinition
        Injector refInjector = new RefInjector(userServiceBD.refDependencies, beans);
        Bean userServiceBean = new Bean(id: "userService", value: new UserService())
        refInjector.inject(userServiceBean)
        assertEquals(userServiceBean.getValue(), beans.get("userService").getValue())

    }

    @Test(dataProvider = "beanProvider", dataProviderClass = BeanDataProvider.class)
    void injectPaymentTest(beans) {
        def beanDefinitions = BeanDefinitionDataProvider.provideBeanDefinitions()[0] as List<BeanDefinition>
        def paymentServiceBD = beanDefinitions[0].find { it.id == "paymentWithMaxService" } as BeanDefinition
        Bean paymentServiceBean = new Bean(id: "paymentServiceWithMaxAmount", value: new PaymentService())
        def injector = [new RefInjector(paymentServiceBD.refDependencies, beans),
                        new ValueInjector(paymentServiceBD.dependencies)]
        injector.each { it.inject(paymentServiceBean) }
        assertEquals(paymentServiceBean.getValue(), beans.get("paymentServiceWithMaxAmount").getValue())

    }

}
