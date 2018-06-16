package com.dzytsiuk.ioc.testdata.service;

public class NoDefaultConstructorService {

    private int id;

    public NoDefaultConstructorService(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
