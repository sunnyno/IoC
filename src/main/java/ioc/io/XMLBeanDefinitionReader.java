package ioc.io;


import ioc.entity.BeanDefinition;
import ioc.exception.SourceParseException;
import ioc.io.config.BeanDefinitionTag;
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
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
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

        for (int j = 0; j < properties.getLength(); j++) {
            Element item = (Element) properties.item(j);
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
}
