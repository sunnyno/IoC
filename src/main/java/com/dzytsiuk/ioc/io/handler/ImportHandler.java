package com.dzytsiuk.ioc.io.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class ImportHandler extends DefaultHandler {

    private static final String IMPORT_TAG = "import";
    private static final String RESOURCE_ATTRIBUTE = "resource";

    private List<String> importFiles = new ArrayList<>();

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {

        if (qName.equalsIgnoreCase(IMPORT_TAG)) {
            importFiles.add(attributes.getValue(RESOURCE_ATTRIBUTE));
        }
    }

    public List<String> getImportFiles() {
        return importFiles;
    }

}