package com.dzytsiuk.ioc.io;


import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.SourceParseException;
import com.dzytsiuk.ioc.io.handler.BeanDefinitionHandler;
import com.dzytsiuk.ioc.io.handler.ImportHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {


    private static final SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();


    private List<String> contextFiles;
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();

    public XMLBeanDefinitionReader(String... path) {
        contextFiles = new ArrayList<>(Arrays.asList(path));
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        setImportedContextFileNames(contextFiles);

        try {
            SAXParser saxParser = SAX_PARSER_FACTORY.newSAXParser();
            DefaultHandler beanDefinitionHandler = new BeanDefinitionHandler(beanDefinitions);
            for (String contextFile : contextFiles) {
                saxParser.parse(contextFile, beanDefinitionHandler);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SourceParseException("Error parsing XML", e);
        }
        return beanDefinitions;
    }

    void setImportedContextFileNames(List<String> initialContextFileNames) {
        try {
            SAXParser saxParser = SAX_PARSER_FACTORY.newSAXParser();
            ImportHandler importHandler = new ImportHandler();
            List<String> importFiles = importHandler.getImportFiles();
            for (String xmlFilePath : initialContextFileNames) {
                saxParser.parse(xmlFilePath, importHandler);
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

