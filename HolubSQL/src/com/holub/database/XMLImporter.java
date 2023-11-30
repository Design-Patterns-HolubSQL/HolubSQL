package com.holub.database;

import com.holub.tools.ArrayIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;                                 

public class XMLImporter implements Table.Importer {
    private BufferedReader in;
    private String tableName;
    private List<String> columnNames = new ArrayList<>();
    private NodeList rows;
    private int currentRow = 0;

    // 생성자: 주어진 Reader에서 XML을 읽어오기 위해 BufferedReader로 변환합니다.
    public XMLImporter(Reader in) {
        this.in = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
    }

    // 테이블의 시작을 처리합니다.
    @Override
    public void startTable() throws IOException {
        try {
            // XML 파싱을 위한 DocumentBuilder를 생성합니다.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // XML을 읽어와 Document 객체로 파싱합니다.
            Document doc = builder.parse(new InputSource(in));
            Element tableElement = doc.getDocumentElement();
            tableName = tableElement.getAttribute("name");

            // 각 열의 이름을 가져와 리스트에 추가합니다.
            NodeList columns = tableElement.getElementsByTagName("column");
            for (int i = 0; i < columns.getLength(); i++) {
                columnNames.add(columns.item(i).getTextContent());
            }

            // 각 행을 가져오는 NodeList를 설정합니다.
            rows = tableElement.getElementsByTagName("row");
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to parse XML", e);
        }
    }

    // 테이블의 이름을 반환합니다.
    @Override
    
    public String loadTableName() {
        return tableName;
    }

    // 테이블의 폭(열의 개수)을 반환합니다.
    @Override
    public int loadWidth() {
        return columnNames.size();
    }

    // 테이블의 열 이름을 Iterator로 반환합니다.
    @Override
    public Iterator loadColumnNames() {
        return columnNames.iterator();
    }

    // 현재 행의 데이터를 반환하는 Iterator를 생성하여 반환합니다.
    @Override
    public Iterator loadRow() {
        if (currentRow >= rows.getLength()) {
            return null; // No more rows
        }

        // 현재 행의 Element를 가져와 각 셀의 데이터를 배열에 저장합니다.
        Element rowElement = (Element) rows.item(currentRow++);
        NodeList cells = rowElement.getChildNodes();
        String[] rowData = new String[cells.getLength()];
        for (int i = 0; i < cells.getLength(); i++) {
            rowData[i] = cells.item(i).getTextContent();
        }
        return new ArrayIterator(rowData);
    }

    // 테이블의 끝을 처리합니다. (여기서는 아무 작업도 수행하지 않음)
    @Override
    public void endTable() {
        // No specific action required at the end
    }
}
