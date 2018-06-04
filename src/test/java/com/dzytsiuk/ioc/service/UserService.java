package com.dzytsiuk.ioc.service;


public class UserService {
    private MailService mailService;

    public UserService() {
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public String toString() {
        return "UserService{" +
                "mailService=" + mailService +
                '}';
    }
}
