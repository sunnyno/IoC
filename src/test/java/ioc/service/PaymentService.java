package ioc.service;

public class PaymentService {
    private MailService mailService;
    private int maxAmount;

    public PaymentService() {
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public String toString() {
        return "PaymentService{" +
                "mailService=" + mailService +
                ", maxAmount=" + maxAmount +
                '}';
    }
}
