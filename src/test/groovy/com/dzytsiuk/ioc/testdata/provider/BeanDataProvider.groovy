package com.dzytsiuk.ioc.testdata.provider

import com.dzytsiuk.ioc.entity.Bean
import com.dzytsiuk.ioc.testdata.service.MailService
import com.dzytsiuk.ioc.testdata.service.PaymentService
import com.dzytsiuk.ioc.testdata.service.UserService
import org.testng.annotations.DataProvider

class BeanDataProvider {

    @DataProvider(name = "beanProvider")
    static Object[][] provideBeans() {
        def mailService = new MailService(protocol: "POP3", port: 3000)
        def mailServicePostProcess = new MailService(protocol: "postProcessProtocol", port: 10000, domain: 'gmail')
        def userService = new UserService(mailService: mailService)
        def paymentService = new PaymentService(mailService: mailService)
        def paymentServiceWithMaxAmount = new PaymentService(mailService: mailService, maxAmount: 500)
        def beans = ["mailService"                : new Bean(id: "mailService", value: mailService),
                     "mailServicePostProcess"     : new Bean(id: "mailService", value: mailServicePostProcess),
                     "userService"                : new Bean(id: "userService", value: userService),
                     "paymentService"             : new Bean(id: "paymentService", value: paymentService),
                     "paymentServiceWithMaxAmount": new Bean(id: "paymentServiceWithMaxAmount", value: paymentServiceWithMaxAmount)
        ] as HashMap<String, Bean>;
        def beanArray = new Object[1][]
        beanArray[0] = [beans] as Object[]
        return beanArray;
    }

}
