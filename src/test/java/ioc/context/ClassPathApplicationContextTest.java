package ioc.context;

import ioc.entity.BeanDefinition;
import ioc.service.MailService;
import ioc.service.PaymentService;
import ioc.service.UserService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ClassPathApplicationContextTest {

    private static List<BeanDefinition> beanDefinitions;
    private static UserService userService;
    private static MailService mailService;
    private static PaymentService paymentServiceWithMaxAmount;
    private static PaymentService paymentService;
    static ClassPathApplicationContext classPathApplicationContext;

    @BeforeClass
    public static void setUpBeanDefinitions() {
        beanDefinitions = new ArrayList<>();
        BeanDefinition beanDefinition1 = new BeanDefinition();
        beanDefinition1.setId("mailService");
        beanDefinition1.setBeanClassName("ioc.service.MailService");
        beanDefinition1.setDependencies(new HashMap<String, String>() {{
            put("protocol", "POP3");
            put("port", "3000");
        }});

        BeanDefinition beanDefinition2 = new BeanDefinition();
        beanDefinition2.setId("userService");
        beanDefinition2.setBeanClassName("ioc.service.UserService");
        beanDefinition2.setRefDependencies(new HashMap<String, String>() {{
            put("mailService", "mailService");
        }});


        BeanDefinition beanDefinition3 = new BeanDefinition();
        beanDefinition3.setId("paymentWithMaxService");
        beanDefinition3.setBeanClassName("ioc.service.PaymentService");
        beanDefinition3.setRefDependencies(new HashMap<String, String>() {{
            put("mailService", "mailService");
        }});
        beanDefinition3.setDependencies(new HashMap<String, String>() {{
            put("maxAmount", "500");
        }});


        BeanDefinition beanDefinition4 = new BeanDefinition();
        beanDefinition4.setId("paymentService");
        beanDefinition4.setBeanClassName("ioc.service.PaymentService");
        beanDefinition4.setRefDependencies(new HashMap<>());
        beanDefinition4.getRefDependencies().put("mailService", "mailService");

        beanDefinitions.add(beanDefinition1);
        beanDefinitions.add(beanDefinition2);
        beanDefinitions.add(beanDefinition3);
        beanDefinitions.add(beanDefinition4);

        userService = new UserService();
        mailService = new MailService();
        paymentService = new PaymentService();
        paymentServiceWithMaxAmount = new PaymentService();
        mailService.setPort(3000);
        mailService.setProtocol("POP3");
        userService.setMailService(mailService);
        paymentService.setMailService(mailService);
        paymentServiceWithMaxAmount.setMailService(mailService);
        paymentServiceWithMaxAmount.setMaxAmount(500);
        classPathApplicationContext = new ClassPathApplicationContext("resources/context.xml"
                , "resources/payment-context.xml");
    }


    @Test
    public void createBeansFromBeanDefinitionsTest() {
        List actualBeanDefinitions = classPathApplicationContext.getBeanDefinitions();
        for (int i = 0; i < actualBeanDefinitions.size(); i++) {
            assertEquals(beanDefinitions.get(i).toString(), actualBeanDefinitions.get(i).toString());
        }

    }

    @Test
    public void getBeanByIdAndClassTest() {
        UserService actualUserService = (UserService) classPathApplicationContext.getBean("userService", UserService.class);
        assertEquals(userService.toString(), actualUserService.toString());
    }

    @Test
    public void getBeanByClassTest() {
        UserService actualUserService2 = (UserService) classPathApplicationContext.getBean(UserService.class);
        assertEquals(userService.toString(), actualUserService2.toString());

    }

    @Test
    public void getBeanByIdTest() {
        PaymentService actualPaymentService = (PaymentService) classPathApplicationContext.getBean("paymentWithMaxService");
        assertEquals(paymentServiceWithMaxAmount.toString(), actualPaymentService.toString());

    }

}