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
import java.util.*;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {

    private static final SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();

    private List<String> contextFiles;

    public XMLBeanDefinitionReader(String... path) {
        contextFiles = new ArrayList<>(Arrays.asList(path));
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions() {

        try {
            List<String> contextsForScan = getContextsForScan();
            List<BeanDefinition> beanDefinitions = new ArrayList<>();
            SAXParser saxParser = SAX_PARSER_FACTORY.newSAXParser();
            DefaultHandler beanDefinitionHandler = new BeanDefinitionHandler(beanDefinitions);
            for (String contextFile : contextsForScan) {
                saxParser.parse(contextFile, beanDefinitionHandler);
            }
            return beanDefinitions;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SourceParseException("Error parsing XML", e);
        }

    }

    List<String> getContextsForScan() throws IOException, SAXException, ParserConfigurationException {
        List<String> allContextFiles = new ArrayList<>(contextFiles);
        Queue<String> resourceFilesQueue = new LinkedList<>(contextFiles);
        while (!resourceFilesQueue.isEmpty()) {
            SAXParser saxParser = SAX_PARSER_FACTORY.newSAXParser();
            ImportHandler importHandler = new ImportHandler();
            String pathToFile = resourceFilesQueue.remove();
            saxParser.parse(pathToFile, importHandler);

            resourceFilesQueue.addAll(importHandler.getImportFiles());
            allContextFiles.addAll(importHandler.getImportFiles());
        }
        return allContextFiles;

    }


}

