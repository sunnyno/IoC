package ioc.service;


public class MailService {
    private int port;
    private String protocol;

    public MailService() {
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "MailService{" +
                "port=" + port +
                ", protocol='" + protocol + '\'' +
                '}';
    }
}
