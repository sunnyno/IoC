package com.dzytsiuk.ioc.io.config;


public enum BeanDefinitionTag {
    BEANS("beans"), BEAN("bean"), ID("id"), CLASS("class"),
    PROPERTY("property"), NAME("name"), VALUE("value"), REF("ref");


    private String name;

    BeanDefinitionTag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
