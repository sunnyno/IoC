package com.dzytsiuk.ioc.io;


import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.SourceParseException;
import com.dzytsiuk.ioc.io.config.BeanDefinitionTag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {

    private static final XMLBeanDefinitionReader INSTANCE = new XMLBeanDefinitionReader();
    private static final DocumentBuilderFactory DB_FACTORY = DocumentBuilderFactory.newInstance();

    private List<String> contextFiles;

    private XMLBeanDefinitionReader() {
        contextFiles = new ArrayList<>();
    }

    public static XMLBeanDefinitionReader getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        try {
            DocumentBuilder dBuilder = DB_FACTORY.newDocumentBuilder();
            for (String xmlFilePath : contextFiles) {
                File file = new File(xmlFilePath);
                Document doc = dBuilder.parse(file);

                if (!doc.getDocumentElement().getNodeName().equals(BeanDefinitionTag.BEANS.getName())) {
                    throw new SourceParseException("Root element '" + BeanDefinitionTag.BEANS.getName() + "' not found");
                }
                NodeList beanNodeList = doc.getElementsByTagName(BeanDefinitionTag.BEAN.getName());

                for (int i = 0; i < beanNodeList.getLength(); i++) {
                    Node node = beanNodeList.item(i);
                    BeanDefinition beanDefinition = new BeanDefinition();
                    setBeanDefinitions(node, beanDefinition);
                    beanDefinitions.add(beanDefinition);
                }
            }
            return beanDefinitions;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SourceParseException("Error parsing XML", e);
        }

    }

    private void setBeanDefinitions(Node node, BeanDefinition beanDefinition) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            beanDefinition.setId(element.getAttribute(BeanDefinitionTag.ID.getName()));
            beanDefinition.setBeanClassName(element.getAttribute(BeanDefinitionTag.CLASS.getName()));
            NodeList properties = element.getElementsByTagName(BeanDefinitionTag.PROPERTY.getName());
            setBeanProperties(beanDefinition, properties);
        }
    }

    private void setBeanProperties(BeanDefinition beanDefinition, NodeList properties) {
        HashMap<String, String> beanAttributes = new HashMap<>();
        HashMap<String, String> beanRefAttributes = new HashMap<>();

        for (int i = 0; i < properties.getLength(); i++) {
            Element item = (Element) properties.item(i);
            String attributeName = item.getAttribute(BeanDefinitionTag.NAME.getName());
            String attributeValue = item.getAttribute(BeanDefinitionTag.VALUE.getName());
            String attributeRefValue = item.getAttribute(BeanDefinitionTag.REF.getName());
            if (!attributeValue.isEmpty()) {
                beanAttributes.put(attributeName, attributeValue);
                beanDefinition.setDependencies(beanAttributes);
            }
            if (!attributeRefValue.isEmpty()) {
                beanRefAttributes.put(attributeName, attributeRefValue);
                beanDefinition.setRefDependencies(beanRefAttributes);
            }

        }
    }

    public void setContextFilePath(String contextFile) {
        this.contextFiles.add(contextFile);
    }

    @Override
    public void setImportedContextFileNames(List<String> initialContextFileNames) {
        try {
            DocumentBuilder dBuilder = DB_FACTORY.newDocumentBuilder();
            List<String> importFiles = new ArrayList<>();
            for (String xmlFilePath : initialContextFileNames) {
                File file = new File(xmlFilePath);
                Document doc = dBuilder.parse(file);
                NodeList nodeList = doc.getElementsByTagName(BeanDefinitionTag.IMPORT.getName());
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    String resource = element.getAttribute(BeanDefinitionTag.RESOURCE.getName());
                    importFiles.add(resource);
                }
            }
            while (!importFiles.isEmpty()) {
                contextFiles.addAll(importFiles);
                setImportedContextFileNames(importFiles);
                importFiles.clear();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SourceParseException("Error parsing XML", e);
        }
    }
}
