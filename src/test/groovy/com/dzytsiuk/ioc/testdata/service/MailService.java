package com.dzytsiuk.ioc.testdata.service;


import javax.annotation.PostConstruct;
import java.util.Objects;

public class MailService {
    private int port;
    private String protocol;
    private String domain;

    public MailService() {
    }

    @PostConstruct
    public void init(){
        domain = "gmail";
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public void setDomain(String domain) {
        this.domain = domain;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailService that = (MailService) o;
        return port == that.port &&
                Objects.equals(protocol, that.protocol);
    }

    @Override
    public int hashCode() {

        return Objects.hash(port, protocol);
    }

}
